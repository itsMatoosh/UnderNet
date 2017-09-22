package me.matoosh.undernet.p2p.router.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerExceptionEvent;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;

/**
 * Server part of the router.
 *
 * Created by Mateusz Rębacz on 30.01.2017.
 */

public class Server
{
    /**
     * The router.
     */
    public Router router;
    /**
     * Current status of the server.
     */
    public InterfaceStatus status = InterfaceStatus.STOPPED;

    /**
     * Event loop group for accepting incoming connections.
     */
    public EventLoopGroup bossEventLoopGroup;
    /**
     * Event loop group for managing active co
     */
    public EventLoopGroup workerEventLoopGroup;
    /**
     * The future of the server.
     */
    public ChannelFuture serverFuture;

    /**
     * A list of the currently active channels.
     */
    public final ChannelGroup channels =
            new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    /**
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * Creates a server instance using a specified port.
     */
    public Server(Router router) {
        this.router = router;
    }

    /**
     * Sets up the server.
     */
    public void setup() {
        //Registering events.
        registerEvents();
    }

    /**
     * Starts the server.
     * @throws Exception
     */
    public void start() {
        //Changing the server status to starting.
        EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STARTING));

        //Creating the worker and boss server event groups.
        bossEventLoopGroup = new NioEventLoopGroup();
        workerEventLoopGroup = new NioEventLoopGroup();

        //Bootstraping the server.
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup) //Assigning event loops to the server.
                .channel(NioServerSocketChannel.class) //Using the non blocking io for transfer.
                .childHandler(new ServerChannelInitializer(this))
                .option(ChannelOption.SO_BACKLOG, 16)          //Setting the number of pending connections to keep in the queue.
                .childOption(ChannelOption.SO_KEEPALIVE, true); //Making sure the connection event loop sends keep alive messages.

        //Binding and starting to accept incoming connections.
        try {
            serverFuture = serverBootstrap.bind(UnderNet.networkConfig.listeningPort()).sync();
            EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STARTED));

            //Waiting for the server to close.
            serverFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            logger.error("Error binding the server!", e);
            //Changing the server status to stopping.
            EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STOPPING));
        } finally {
            //Stopping the event loop groups.
            bossEventLoopGroup.shutdownGracefully();
            workerEventLoopGroup.shutdownGracefully();
            EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STOPPED));
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Changing the server status to stopping.
        EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STOPPING));

        //Stopping the server.
        //Closing the current channel
        serverFuture.channel().close();
        //Closing the parent channel (the one attached to the bind)
        if(serverFuture.channel().parent() != null) {
            serverFuture.channel().parent().close();
        }
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        //Server events.
        EventManager.registerEvent(ServerStatusEvent.class);
        EventManager.registerEvent(ServerExceptionEvent.class);
    }
}
