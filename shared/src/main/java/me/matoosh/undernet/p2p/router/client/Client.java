package me.matoosh.undernet.p2p.router.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientExceptionEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

/**
 * Client part of the router.
 *
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Client {
    /**
     * The router.
     */
    public Router router;
    /**
     * The current status of the client.
     */
    public InterfaceStatus status;
    /**
     * The worker event loop group of the client.
     */
    public EventLoopGroup workerEventLoopGroup;
    /**
     * The connection close futures of the client.
     */
    public ArrayList<ChannelFuture> closeFutures;

    /**
     * A list of the currently active channels.
     */
    public final ChannelGroup channels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    /**
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(Router router) {
        this.router = router;
    }

    /**
     * Sets up the client.
     */
    public void setup() {
        //Registering the client events.
        registerEvents();
    }
    /**
     * Starts the client and connects to cached nodes based on the settings.
     */
    public void start() {
        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTING));

        //Attempting to connect to each of the 5 most reliable nodes.
        ArrayList<Node> nodesToConnectTo = EntryNodeCache.getMostReliable(5, null);
        if(nodesToConnectTo == null || nodesToConnectTo.size() == 0) {
            logger.warn("There are no cached nodes to connect to! The client will stop.");
            EventManager.callEvent(new ClientExceptionEvent(this, new ClientNoNodesCachedException(this)));
            return;
        }

        //Creating a new event loop group.
        workerEventLoopGroup = new NioEventLoopGroup();

        //Creating a list of client futures.
        closeFutures = new ArrayList<>();

        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));

        //Connecting to the selected nodes.
        for(Node node : nodesToConnectTo) {
            connect(node);
        }
    }
    /**
     * Connects the client to a node.
     */
    public void connect(Node node) {
        if(status == InterfaceStatus.STOPPING) {
            logger.error("Can't connect to nodes, while the client is stopping!");
            return;
        }
        if(status != InterfaceStatus.STARTED) {
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));
        }

        logger.info("Connecting to node: " + node.address);

        //Making sure the list of client futures exists.
        if(closeFutures == null) {
            closeFutures = new ArrayList<>();
        }

        //Starting the client.
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerEventLoopGroup) //Assigning the channel to the client event loop group.
        .channel(NioSocketChannel.class) //Using the non blocking io.
        .option(ChannelOption.SO_KEEPALIVE, true) //Making sure the connection is sending the keep alive signal.
        .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //Using the default pooled allocator.
        .handler(new ClientChannelInitializer(this));

        //Connecting
        ChannelFuture future = clientBootstrap.connect(node.address); //Connecting to the node.
        ChannelFuture closeFuture = future.channel().closeFuture();
        closeFuture.addListener(future1 -> {
            //Removing the future from future list.
            closeFutures.remove(future1);
            if(closeFutures.size() == 0) {
                //Stopping the worker group.
                EventManager.callEvent(new ClientStatusEvent(Client.this, InterfaceStatus.STOPPED));
            }
        });
        closeFutures.add(closeFuture);
    }

    /**
     * Stops the client.
     */
    public void stop() {
        //Checking if the client is running.
        if(status == InterfaceStatus.STOPPED) {
            logger.warn("Can't stop the client as it's not running.");
            return;
        }

        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPING));

        //Stopping the client futures.
        if(closeFutures == null) {
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPED));
        } else {
            for (ChannelFuture future:
                    closeFutures) {
                future.channel().close();
            }
        }
    }

    /**
     * Registers the client handlers.
     */
    private void registerEvents() {
        EventManager.registerEvent(ClientStatusEvent.class);
        EventManager.registerEvent(ClientExceptionEvent.class);
    }
}
