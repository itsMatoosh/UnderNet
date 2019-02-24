package me.matoosh.undernet.p2p.shine;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.p2p.shine.client.ShineMediatorClient;
import me.matoosh.undernet.p2p.shine.server.ShineMediatorServer;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;

/**
 * Handles shine requests from clients.
 */
public class PunchthroughRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if(ShineMediatorServer.isRunning()) {
            //cache client info
            ShineMediatorServer.getConnectedClients().add(ctx.channel());
            ShineMediatorServer.logger.info("{} connected!", ctx.channel().remoteAddress());

            //check whether there are other waiting nodes.
            ShineMediatorServer.sendNeighborInfos(ctx.channel());
        } else {
            ShineMediatorClient.logger.info("Connected to the SHINE mediator server!");
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(ShineMediatorServer.isRunning()) {
            ShineMediatorServer.logger.info("{} disconnected!", ctx.channel().remoteAddress());
            ShineMediatorServer.getConnectedClients().remove(ctx.channel());
        }

        super.channelInactive(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if(!ShineMediatorServer.isRunning()) {
            //handle received connection info
            ByteBuf buffer = (ByteBuf)msg;
            boolean ipv6;
            if(buffer.readByte() == 0x0) {
                ipv6 = false;
            } else {
                ipv6 = true;
            }

            InetAddress recvAddress;
            if(ipv6) {
                byte[] address = new byte[16];
                buffer.readBytes(address);
                recvAddress = Inet6Address.getByAddress(address);
            } else {
                byte[] address = new byte[4];
                buffer.readBytes(address);
                recvAddress = Inet6Address.getByAddress(address);
            }

            int port = buffer.readInt();

            ShineMediatorClient.onConnectionInfoReceived(new InetSocketAddress(recvAddress, port));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        System.out.println("EXCEPTION");
        cause.printStackTrace();
        ctx.close();
    }
}
