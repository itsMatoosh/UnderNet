package me.matoosh.undernet.p2p.shine;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.p2p.shine.client.ShineMediatorClient;
import me.matoosh.undernet.p2p.shine.server.ShineEntry;
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
            ShineEntry entry = new ShineEntry(ctx.channel());
            ShineMediatorServer.getConnectedClients().add(entry);
            ShineMediatorServer.logger.info("{} connected!", ctx.channel().remoteAddress());
        } else {
            //send match request.
            ShineMediatorClient.logger.info("Connected to the SHINE mediator server!");

            int length = 4;
            if(ShineMediatorClient.ignoreIds != null) {
                length += 4 * ShineMediatorClient.ignoreIds.length;
            }

            ByteBuf buf = Unpooled.buffer(length + 4);
            buf.writeInt(length);
            buf.writeInt(ShineMediatorClient.shineId);
            if(ShineMediatorClient.ignoreIds != null) {
                for (int i = 0; i < ShineMediatorClient.ignoreIds.length; i++) {
                    //port
                    buf.writeInt(ShineMediatorClient.ignoreIds[i]);
                }
            }

            ShineMediatorClient.logger.info("Sending a SHINE match request, ignoring {} nodes...", ShineMediatorClient.ignoreIds.length);
            ctx.channel().writeAndFlush(buf);
        }

        super.channelActive(ctx);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        if(ShineMediatorServer.isRunning()) {
            ShineMediatorServer.logger.info("{} disconnected!", ctx.channel().remoteAddress());
            for (int i = 0; i < ShineMediatorServer.getConnectedClients().size(); i++) {
                ShineEntry entry = ShineMediatorServer.getConnectedClients().get(i);
                if(entry.getAddress().equals(ctx.channel().remoteAddress())) ShineMediatorServer.getConnectedClients().remove(entry);
            }
            for (int i = 0; i < ShineMediatorServer.getConnectedClients().size(); i++) {
                ShineEntry entry = ShineMediatorServer.getConnectedClients().get(i);
                for (int j = 0; j < entry.getIgnore().size(); j++) {
                    if(entry.getIgnore().get(j).equals(((InetSocketAddress)ctx.channel().remoteAddress()).getAddress())) entry.getIgnore().remove(j);
                }
            }
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

            int shineId = buffer.readInt();

            ShineMediatorClient.onConnectionInfoReceived(new InetSocketAddress(recvAddress, port), shineId);
        } else {
            //get shine entry
            ShineEntry entry = null;
            for (int i = 0; i < ShineMediatorServer.getConnectedClients().size(); i++) {
                if(ShineMediatorServer.getConnectedClients().get(i).getAddress().equals(ctx.channel().remoteAddress())) entry = ShineMediatorServer.getConnectedClients().get(i);
            }
            if(entry == null) return;
            
            ByteBuf buf = (ByteBuf)msg;
            int length = buf.readInt();

            //check if any other connected node has same id
            int shineId = buf.readInt();
            for (ShineEntry connected :
                    ShineMediatorServer.getConnectedClients()) {
                if(connected.getShineId() == shineId)  {
                    ctx.disconnect();
                    return;
                }
            }
            entry.setShineId(shineId);

            int read = 4;
            while (read < length) {
                entry.getIgnore().add(buf.readInt());
                read += 4;
            }

            //match request received
            ShineMediatorServer.logger.info("Match request received from {} ({})", ctx.channel().remoteAddress(), shineId);

            //check whether there are other waiting nodes.
            ShineMediatorServer.sendNeighborInfos(entry);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}
