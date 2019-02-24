package me.matoosh.undernet.p2p.shine.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.udt.UdtChannel;
import io.netty.channel.udt.nio.NioUdtProvider;
import io.netty.util.concurrent.DefaultThreadFactory;
import me.matoosh.undernet.p2p.shine.PunchthroughRequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ThreadFactory;

/**
 * The SHINE mediator server.
 * Pairs lonely nodes and traverses NAT :)
 */
public class ShineMediatorServer {
    private static boolean isRunning;
    private static ArrayList<Channel> connectedClients;

    public static Logger logger = LoggerFactory.getLogger("[MediatorServer]");

    public static void main(String[] args) {
        if(args.length < 2) {
            start(2018);
            return;
        }
        start(Integer.parseInt(args[1]));
    }

    /**
     * Starts the server on the current thread.
     */
    public static void start(int port) {
        //Creating the worker and boss server event groups.
        logger.info("Starting the server...");
        isRunning = true;

        connectedClients = new ArrayList<>();
        final ThreadFactory bossThreadFactory = new DefaultThreadFactory("shine-server-boss");
        final ThreadFactory workerThreadFactory = new DefaultThreadFactory("shine-server-worker");
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
            ChannelFuture serverFuture = serverBootstrap.bind(port).sync();
            serverFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
            return;
        } finally {
            //Stopping the event loop groups.
            try {
                bossEventLoopGroup.shutdownGracefully().sync();
                workerEventLoopGroup.shutdownGracefully().sync();
                isRunning = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
                return;
            }
        }
    }

    public static boolean isRunning() {
        return isRunning;
    }

    public static ArrayList<Channel> getConnectedClients() {
        return connectedClients;
    }

    /**
     * Gets the next appropriate neighbor for the given node.
     * @param forNode
     * @return
     */
    private static Channel getNextNeighbor(Channel forNode) {
        if(getConnectedClients().size() <= 1) return null;

        InetSocketAddress nodeAddress = (InetSocketAddress) forNode.remoteAddress();
        for (int i = 0; i < getConnectedClients().size(); i++) {
            Channel client = getConnectedClients().get(i);
            InetSocketAddress address = (InetSocketAddress)client.remoteAddress();
            return client;
            /*if(!address.getHostString().equalsIgnoreCase(nodeAddress.getHostString())) {
                return client;
            }*/
        }
        return null;
    }

    /**
     * Attempts to choose and send next neighbor info to node.
     * @param nodeA
     */
    public static void sendNeighborInfos(Channel nodeA) {
        //getting node b.
        Channel nodeB = getNextNeighbor(nodeA);
        if(nodeB == null) {
            logger.info("Not enough qualified nodes to send node info, {}", getConnectedClients().size());
            return;
        }

        logger.info("Sending nodes infos...");
        //sending node infos to the two nodes.
        sendSocketAddress(nodeA, (InetSocketAddress)nodeB.remoteAddress());
        sendSocketAddress(nodeB, (InetSocketAddress)nodeA.remoteAddress());

        //disconnecting both of the nodes.
        nodeA.close();
        nodeB.close();
    }

    /**
     * Sends the socket adress info to the given node.
     * @param channel
     * @param socketAddress
     */
    private static void sendSocketAddress(Channel channel, InetSocketAddress socketAddress) {
        InetAddress address = socketAddress.getAddress();
        ByteBuf buf = Unpooled.buffer(address.getAddress().length + 5);
        if(address instanceof Inet4Address) {
            buf.writeByte(0x0);
        } else {
            buf.writeByte(0x1);
        }
        buf.writeBytes(address.getAddress(), 0, address.getAddress().length);
        buf.writeInt(socketAddress.getPort());

        channel.writeAndFlush(buf);
    }
}
