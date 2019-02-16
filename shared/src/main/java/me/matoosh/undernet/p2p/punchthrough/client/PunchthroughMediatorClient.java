package me.matoosh.undernet.p2p.punchthrough.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import me.matoosh.undernet.p2p.punchthrough.PunchthroughRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * The NAT/firewall punchthrough mediator client.
 */
public class PunchthroughMediatorClient {

    private static Logger logger = LoggerFactory.getLogger("[MediatorClient]");
    private static final String MEDIATOR_SERVER_ADDRESS = "localhost";
    private static final int MEDIATOR_SERVER_PORT = 2018;

    public static void main(String[] args) {
        start();
    }

    public static void start() {
        //Starting the client.
        logger.info("Connecting to the NAT mediator server...");
        Bootstrap clientBootstrap = new Bootstrap();
        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup();
        clientBootstrap.group(workerEventLoopGroup) //Assigning the channel to the client event loop group.
                .channelFactory(NioUdtProvider.BYTE_CONNECTOR) //Using the non blocking io.
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //Using the default pooled allocator.
                .handler(new ChannelInitializer<UdtChannel>() {
                    @Override
                    protected void initChannel(UdtChannel ch) throws Exception {
                        ch.pipeline().addLast(new PunchthroughRequestHandler());
                    }
                });

        //Connecting
        ChannelFuture future = clientBootstrap.connect(new InetSocketAddress(MEDIATOR_SERVER_ADDRESS, MEDIATOR_SERVER_PORT)); //Connecting to the node.
        try {
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
