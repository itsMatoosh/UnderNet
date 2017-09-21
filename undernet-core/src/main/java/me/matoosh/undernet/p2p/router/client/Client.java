package me.matoosh.undernet.p2p.router.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientConnectionEvent;
import me.matoosh.undernet.event.client.ClientExceptionEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;

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
     * The futures of the client.
     */
    public ArrayList<ChannelFuture> clientFutures;

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
        logger.info("Starting the client...");
        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTING));

        //Creating a new event loop group.
        workerEventLoopGroup = new NioEventLoopGroup();

        //Attempting to connect to each of the 5 most reliable nodes.
        ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
        if(nodesToConnectTo == null || nodesToConnectTo.size() == 0) {
            logger.warn("There are no cached nodes to connect to! The client will stop.");
            EventManager.callEvent(new ClientExceptionEvent(this, new ClientNoNodesCachedException(this)));
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPED));
            return;
        }

        //Creating a list of client futures.
        clientFutures = new ArrayList<>();

        //Connecting to the selected nodes.
        for(Node node : nodesToConnectTo) {
            connect(node);
        }
        EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STARTED));

        //Waiting for all the connections to close.
        waitForAllConnections();
    }
    /**
     * Connects the client to a node.
     */
    public void connect(Node node) {
        logger.info("Connecting to node: " + node.address);

        //Making sure the list of client futures exists.
        if(clientFutures == null) {
            clientFutures = new ArrayList<>();
        }

        //Starting the client.
        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerEventLoopGroup); //Assigning the channel to the client event loop group.
        clientBootstrap.channel(NioSocketChannel.class); //Using the non blocking io.
        clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true); //Making sure the connection is sending the keep alive signal.
        clientBootstrap.handler(new ClientChannelInitializer(this));

        //Connecting
        try {
            clientFutures.add(clientBootstrap.connect(node.address).sync()); //Connecting to the node.
        } catch (InterruptedException e) {
            logger.error("There was a problem connecting to the node: " + node.address, e);
            EventManager.callEvent(new ClientExceptionEvent(this, e));
        }
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
        for (ChannelFuture future:
             clientFutures) {
            //Closing the current channel
            future.channel().close();
            //Closing the parent channel (the one attached to the bind)
            if(future.channel().parent() != null) {
                future.channel().parent().close();
            }
        }
    }

    /**
     * Waits for all the connections to close.
     */
    private void waitForAllConnections() {
        try {
            for (ChannelFuture future:
                    clientFutures) {
                future.channel().closeFuture().sync();
            }
        } catch (InterruptedException e) {
            logger.error("One of the client connection tasks has been interrupted!", e);
        } finally {
            workerEventLoopGroup.shutdownGracefully();
            EventManager.callEvent(new ClientStatusEvent(this, InterfaceStatus.STOPPED));
        }

    }

    /**
     * Registers the client handlers.
     */
    private void registerEvents() {
        EventManager.registerEvent(ClientConnectionEvent.class);
        EventManager.registerEvent(ClientStatusEvent.class);
        EventManager.registerEvent(ClientExceptionEvent.class);
    }
}
