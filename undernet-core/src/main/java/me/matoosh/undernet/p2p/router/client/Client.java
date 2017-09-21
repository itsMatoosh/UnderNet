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
     * The future of the client.
     */
    public ChannelFuture clientFuture;

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

        //Creating a new event loop group.
        workerEventLoopGroup = new NioEventLoopGroup();

        //Attempting to connect to each of the 5 most reliable nodes.
        ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
        if(nodesToConnectTo == null) {
            EventManager.callEvent(new ClientExceptionEvent(this, new ClientNoNodesCachedException(this)));
        } else {
            for(Node node : nodesToConnectTo) {
                connect(node);
            }
        }
    }
    /**
     * Connects the client to a node.
     */
    public void connect(Node node) {
        logger.info("Connecting to node: " + node.address);

        Bootstrap clientBootstrap = new Bootstrap();
        clientBootstrap.group(workerEventLoopGroup); //Assigning the channel to the client event loop group.
        clientBootstrap.channel(NioSocketChannel.class); //Using the non blocking io.
        clientBootstrap.option(ChannelOption.SO_KEEPALIVE, true); //Making sure the connection is sending the keep alive signal.
        clientBootstrap.handler(new ClientChannelInitializer(this));

        //Starting the client.
        clientFuture = clientBootstrap.connect(node.address); //Connecting to the node.
    }

    /**
     * Stops the client.
     */
    public void stop() {
        logger.info("Stopping the client...");

        try {
            //Stopping the client future.
            clientFuture.channel().closeFuture().sync();

            //Stopping the worker event group.
            workerEventLoopGroup.shutdownGracefully();
        } catch (InterruptedException e) {
            logger.error("An error occurred while stopping the server!", e);
            EventManager.callEvent(new ClientExceptionEvent(this, e));
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
