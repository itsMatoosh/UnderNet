package me.matoosh.undernet.p2p.shine.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.matoosh.undernet.p2p.shine.PunchthroughRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.ThreadFactory;

/**
 * The SHINE mediator client.
 * Pairs lonely nodes and traverses NAT :)
 */
public class ShineMediatorClient {
    public static Logger logger = LoggerFactory.getLogger("[MediatorClient]");
    public static IMediatorClientConnectionInfoReceivedListner infoReceivedListner;
    public static InetSocketAddress[] ignoreAddresses;

    private static int localPort = 0;
    public static ChannelFuture clientFuture;

    public static void main(String[] args) {
        if(args.length < 3) {
            logger.warn("Usage: shine-client <server-address> <server-port>");
            return;
        }
        start(args[1], Integer.parseInt(args[2]), null);
    }

    public static void start(String shineAddress, int shinePort, IMediatorClientConnectionInfoReceivedListner connectionInfoReceivedListner, InetSocketAddress... ignoreAddresses) {
        if(clientFuture != null && clientFuture.channel() != null && clientFuture.channel().isOpen()) return;
        infoReceivedListner = connectionInfoReceivedListner;
        ShineMediatorClient.ignoreAddresses = ignoreAddresses;

        //Starting the client.
        logger.info("Connecting to the SHINE mediator server ({})...", shineAddress + ":" + shinePort);

        Bootstrap clientBootstrap = new Bootstrap();
        final ThreadFactory bossThreadFactory = new DefaultThreadFactory("shine-client-boss");

        NioEventLoopGroup workerEventLoopGroup = new NioEventLoopGroup(1, bossThreadFactory, NioUdtProvider.BYTE_PROVIDER);
        clientBootstrap.group(workerEventLoopGroup) //Assigning the channel to the client event loop group.
                .channelFactory(NioUdtProvider.BYTE_CONNECTOR) //Using the non blocking io.
                .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT) //Using the default pooled allocator.
                .option(ChannelOption.SO_REUSEADDR, true)
                .handler(new ChannelInitializer<UdtChannel>() {
                    @Override
                    protected void initChannel(UdtChannel ch) throws Exception {
                        ch.pipeline().addLast(new PunchthroughRequestHandler());
                    }
                });

        //Connecting
        new Thread(() -> {
            try {
                clientFuture = clientBootstrap.connect(new InetSocketAddress(shineAddress, shinePort)).await();
                localPort = ((InetSocketAddress)clientFuture.channel().localAddress()).getPort();
                clientFuture.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finally {
                //Stopping the event loop groups.
                try {
                    workerEventLoopGroup.shutdownGracefully().sync();
                    localPort = 0;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                logger.info("SHINE client stopped!");
            }
        }).start();
    }

    /**
     * Stops the client.
     */
    public static void stop() {
        logger.info("Stopping the SHINE client...");
        if(clientFuture != null && clientFuture.channel() != null) {
            clientFuture.channel().close();
        }
    }

    /**
     * Called when a node address has been received.
     * @param socketAddress
     */
    public static void onConnectionInfoReceived(InetSocketAddress socketAddress) {
        logger.info("Received lonely node info, {}:{}, connecting from port: {}...", socketAddress.getHostString(), socketAddress.getPort(), localPort);
        clientFuture.channel().close();

        //Running callback
        if(infoReceivedListner != null) {
            infoReceivedListner.onConnectionInfoReceived(socketAddress, localPort);
        }
    }

    public static boolean isRunning() {
        if(clientFuture != null && clientFuture.channel() != null) {
            return clientFuture.channel().isOpen();
        }
        return false;
    }

    public interface IMediatorClientConnectionInfoReceivedListner {
        void onConnectionInfoReceived(InetSocketAddress socketAddress, int localPort);
    }
}
