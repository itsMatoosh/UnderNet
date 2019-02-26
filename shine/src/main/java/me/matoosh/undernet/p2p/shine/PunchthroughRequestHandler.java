package me.matoosh.undernet.p2p.shine;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import me.matoosh.undernet.p2p.shine.client.ShineMediatorClient;
import me.matoosh.undernet.p2p.shine.server.ShineEntry;
import me.matoosh.undernet.p2p.shine.server.ShineMediatorServer;

import java.net.Inet4Address;
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
            ShineMediatorServer.logger.info("{} connected!", entry.getAddress());
        } else {
            ShineMediatorClient.logger.info("Connected to the SHINE mediator server!");

            int length = 0;
            if(ShineMediatorClient.ignoreAddresses != null) {
                for (int i = 0; i < ShineMediatorClient.ignoreAddresses.length; i++) {
                    length += ShineMediatorClient.ignoreAddresses[i].getAddress().getAddress().length + 4 /*port*/ + 1 /*address type*/;
                }
            }

            ByteBuf buf = Unpooled.buffer(length + 4);
            buf.writeInt(length);
            if(ShineMediatorClient.ignoreAddresses != null) {
                for (int i = 0; i < ShineMediatorClient.ignoreAddresses.length; i++) {
                    InetAddress address = ShineMediatorClient.ignoreAddresses[i].getAddress();
                    int port = ShineMediatorClient.ignoreAddresses[i].getPort();

                    //address type
                    if(address instanceof Inet4Address) {
                        buf.writeByte(0x0);
                    } else {
                        buf.writeByte(0x1);
                    }

                    //address
                    buf.writeBytes(address.getAddress());

                    //port
                    buf.writeInt(port);
                }
            }

            ShineMediatorClient.logger.info("Sending a SHINE match request, ignoring {} nodes...", ShineMediatorClient.ignoreAddresses.length);
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
        } else {
            //match request received
            ShineMediatorServer.logger.info("Match request received from {}", ctx.channel().remoteAddress());

            //get shine entry
            ShineEntry entry = null;
            for (int i = 0; i < ShineMediatorServer.getConnectedClients().size(); i++) {
                if(ShineMediatorServer.getConnectedClients().get(i).getAddress().equals(ctx.channel().remoteAddress())) entry = ShineMediatorServer.getConnectedClients().get(i);
            }
            if(entry == null) return;

            ByteBuf buf = (ByteBuf)msg;
            int length = buf.readInt();
            int read = 0;
            while (read < length) {
                //address type
                InetAddress address;
                if(buf.readByte() == 0x0) {
                    byte[] data = new byte[4];
                    buf.readBytes(data);
                    address = Inet4Address.getByAddress(data);
                    read += 5;
                } else {
                    byte[] data = new byte[16];
                    buf.readBytes(data);
                    address = Inet6Address.getByAddress(data);
                    read += 17;
                }

                //port
                int port = buf.readInt();
                read += 4;

                entry.getIgnore().add(new InetSocketAddress(address, port));
            }

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
