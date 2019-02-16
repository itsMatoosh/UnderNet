package me.matoosh.undernet.p2p.punchthrough.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.matoosh.undernet.p2p.punchthrough.PunchthroughRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadFactory;

/**
 * The NAT/firewall punchthrough mediator server.
 */
public class PunchthroughMediatorServer {
    /**
     * Port used for negotiating a punchthrough.
     */
    public static final int PUNCH_PORT = 2018;

    private static boolean shouldStop = false;

    private static Logger logger = LoggerFactory.getLogger("[MediatorServer]");

    public static void main(String[] args) {
        start();
    }

    /**
     * Starts the server on the current thread.
     */
    public static void start() {
        //Creating the worker and boss server event groups.
        logger.info("Starting the server...");
        shouldStop = false;
        final ThreadFactory bossThreadFactory = new DefaultThreadFactory("transport-server-boss");
        final ThreadFactory workerThreadFactory = new DefaultThreadFactory("transport-server-worker");
        EventLoopGroup bossEventLoopGroup = new NioEventLoopGroup(1, bossThreadFactory, NioUdtProvider.BYTE_PROVIDER);
        EventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(1, workerThreadFactory, NioUdtProvider.BYTE_PROVIDER);

        //Bootstraping the server.
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossEventLoopGroup, workerEventLoopGroup) //Assigning event loops to the server.
                .channelFactory(NioUdtProvider.BYTE_ACCEPTOR) //Using the non blocking udt io for transfer.
                .childHandler(new ChannelInitializer<UdtChannel>() {
                    @Override
                    protected void initChannel(UdtChannel ch) throws Exception {
                        ch.pipeline().addLast(new PunchthroughRequestHandler());
                    }
                })
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT); //Setting the default pooled allocator.

        //Binding and starting to accept incoming connections.
        try {
            ChannelFuture serverFuture = serverBootstrap.bind(PUNCH_PORT).sync();

            //Waiting for the server to close.
            if(shouldStop) {
                serverFuture.channel().close();
            }
            serverFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } finally {
            //Stopping the event loop groups.
            try {
                bossEventLoopGroup.shutdownGracefully().sync();
                workerEventLoopGroup.shutdownGracefully().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }
}
