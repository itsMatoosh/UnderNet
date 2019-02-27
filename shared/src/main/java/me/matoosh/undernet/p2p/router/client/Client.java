package me.matoosh.undernet.p2p.router.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.p2p.cache.EntryNodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

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
        ArrayList<Node> nodesToConnectTo = EntryNodeCache.getRandom(5);

        //Creating a new event loop group.
        final ThreadFactory connectFactory = new DefaultThreadFactory("transport-client");
        workerEventLoopGroup = new NioEventLoopGroup(1, connectFactory, NioUdtProvider.BYTE_PROVIDER);

        //Creating a list of client futures.
        closeFutures = new ArrayList<>();

        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));

        //Connecting to the selected nodes.
        for(Node node : nodesToConnectTo) {
            connect(node.getAddress().getHostString(), node.getPort());
        }
    }

    /**
     * Connects the client to a node.
     * @param address
     * @param port
     */
    public void connect(String address, int port) {
        if(status == InterfaceStatus.STOPPING) {
            logger.error("Can't connect to nodes, while the client is stopping!");
            return;
        }
        if(status != InterfaceStatus.STARTED) {
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));
        }
        if (this.router.getConnectedNodes().size() >= UnderNet.networkConfig.maxNeighbors()) {
            logger.warn("Can't connect to more nodes!");
            return;
        }

        //Making sure node is not yet connected
        for (Node conn :
                this.router.getConnectedNodes()) {
            if (conn.getAddress().getHostString().equals(address)) {
                logger.warn("Node {} already connected!", conn);
                return;
            }
        }

        logger.info("Connecting to node: {}", address);

        //Making sure the list of client futures exists.
        if(closeFutures == null) {
            closeFutures = new ArrayList<>();
        }

        //Starting the client.
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerEventLoopGroup) //Assigning the channel to the client event loop group.
                .channelFactory(NioUdtProvider.BYTE_CONNECTOR) //Using the non blocking io.
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //Using the default pooled allocator.
                .attr(ClientNetworkMessageHandler.ATTRIBUTE_KEY_SHINE_ID, 0)
                .handler(new ClientChannelInitializer(this));


        //Connecting
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(address, port)); //Connecting to the node.
        ChannelFuture closeFuture = future.channel().closeFuture();
        closeFuture.addListener(future1 -> {
            //Removing the future from future list.
            closeFutures.remove(future1);
            if(status != InterfaceStatus.STARTED) {
                EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPED));
            }
        });
        closeFutures.add(closeFuture);
    }

    /**
     * Connects the client to a node.
     */
    public void shineConnect(String address, int port, int shineId, int selfPort) {
        if(status == InterfaceStatus.STOPPING) {
            logger.error("Can't connect to nodes, while the client is stopping!");
            return;
        }
        if(status != InterfaceStatus.STARTED) {
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));
        }
        if (this.router.getConnectedNodes().size() >= UnderNet.networkConfig.maxNeighbors()) {
            logger.warn("Can't connect to more nodes!");
            return;
        }

        //Making sure node is not yet connected
        for (Node conn :
                this.router.getConnectedNodes()) {
            if (conn.getAddress().getHostString().equals(address)) {
                logger.warn("Node {} already connected!", conn);
                return;
            }
        }

        logger.info("SHINE connecting to node: {} ({})", address, shineId);

        //Making sure the list of client futures exists.
        if(closeFutures == null) {
            closeFutures = new ArrayList<>();
        }

        //Starting the client.
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerEventLoopGroup) //Assigning the channel to the client event loop group.
                .channelFactory(NioUdtProvider.BYTE_RENDEZVOUS) //Using the non blocking io.
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //Using the default pooled allocator.
                .option(ChannelOption.SO_REUSEADDR, true) //Reusing a socket address.
                .attr(ClientNetworkMessageHandler.ATTRIBUTE_KEY_SHINE_ID, shineId)
                .handler(new ClientChannelInitializer(this));


        //Connecting
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(address, port), new InetSocketAddress(selfPort)); //Connecting to the node.
        ChannelFuture closeFuture = future.channel().closeFuture();
        closeFuture.addListener(future1 -> {
            //Removing the future from future list.
            closeFutures.remove(future1);
            if(status != InterfaceStatus.STARTED) {
                EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPED));
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
        if(closeFutures == null || closeFutures.size() == 0) {
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
    }
}
