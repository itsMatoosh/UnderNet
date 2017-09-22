package me.matoosh.undernet.p2p.router.data.messages;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Encodes NetworkMessage objects to bytes.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkMessageEncoder extends MessageToByteEncoder<NetworkMessage> {
    /**
     * Encode a message into a {@link ByteBuf}. This method will be called for each written message that can be handled
     * by this encoder.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link MessageToByteEncoder} belongs to
     * @param msg the message to encode
     * @param out the {@link ByteBuf} into which the encoded message will be written
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkMessage msg, ByteBuf out) throws Exception {
        //Allocating the buffer.
        out.alloc().buffer(15 + msg.data.length);

        //Setting the dataLenght variable.
        msg.dataLength = (short)(Short.MIN_VALUE + msg.data.length);

        //Writing the buffer.
        out.writeInt(msg.msgId);
        out.writeLong(msg.expiration);
        out.writeByte(msg.checksum);
        out.writeShort(msg.dataLength);
        //DATA
        out.writeBytes(msg.data);
    }
}
