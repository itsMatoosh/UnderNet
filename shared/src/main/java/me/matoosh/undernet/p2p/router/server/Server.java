package me.matoosh.undernet.p2p.router.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.GlobalEventExecutor;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

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
     * Whether the server should stop the startup.
     */
    private boolean shouldStop = false;

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
        shouldStop = false;
        EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STARTING));

        //Creating the worker and boss server event groups.
        final ThreadFactory bossThreadFactory = new DefaultThreadFactory("transport-server-boss");
        final ThreadFactory workerThreadFactory = new DefaultThreadFactory("transport-server-worker");
        bossEventLoopGroup = new NioEventLoopGroup(1, bossThreadFactory, NioUdtProvider.BYTE_PROVIDER);
        workerEventLoopGroup = new NioEventLoopGroup(1, workerThreadFactory, NioUdtProvider.BYTE_PROVIDER);

        //Bootstraping the server.
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup) //Assigning event loops to the server.
                .channelFactory(NioUdtProvider.BYTE_ACCEPTOR) //Using the non blocking udt io for transfer.
                .childHandler(new ServerChannelInitializer(this))
                .option(ChannelOption.SO_BACKLOG, UnderNet.networkConfig.backlogCapacity())//Setting the number of pending connections to keep in the queue.
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT); //Setting the default pooled allocator.

        //Binding and starting to accept incoming connections.
        try {
            serverFuture = serverBootstrap.bind(UnderNet.networkConfig.listeningPort()).sync();
            EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STARTED));

            //Waiting for the server to close.
            if(shouldStop) {
                serverFuture.channel().close();
            }
            serverFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            EventManager.callEvent(new RouterErrorEvent(router, e, false));
            return;
        } finally {
            //Stopping the event loop groups.
            try {
                serverFuture = null;
                bossEventLoopGroup.shutdownGracefully().sync();
                bossEventLoopGroup = null;
                workerEventLoopGroup.shutdownGracefully().sync();
                workerEventLoopGroup = null;
            } catch (InterruptedException e) {
                EventManager.callEvent(new RouterErrorEvent(router, e, false));
                return;
            }

            EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STOPPED));
        }
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Changing the server status to stopping.
        EventManager.callEvent(new ServerStatusEvent(Server.this, InterfaceStatus.STOPPING));
        shouldStop = true;

        //Stopping the server.
        if(serverFuture != null) {
            //Closing the current channel
            serverFuture.channel().close();
            //Closing the parent channel (the one attached to the bind)
            if(serverFuture.channel().parent() != null) {
                serverFuture.channel().parent().close();
            }
        }
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        //Server events.
        EventManager.registerEvent(ServerStatusEvent.class);
    }
}
