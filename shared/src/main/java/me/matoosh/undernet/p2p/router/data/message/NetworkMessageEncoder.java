package me.matoosh.undernet.p2p.router.data.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

import static me.matoosh.undernet.UnderNet.logger;

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
     */
    @Override
    protected void encode(ChannelHandlerContext ctx, NetworkMessage msg, ByteBuf out) {
        //Setting message details.
        if(!msg.isValid()) {
            logger.warn("Message to {} is invalid!", ctx.channel().remoteAddress());
            ctx.flush();
            return;
        }

        try {
            //Allocating the buffer.
            logger.debug("Constructing a network message, allocating {} bytes",
                    msg.getTotalLength());
            out.alloc().buffer(msg.getTotalLength());

            //Writing the header.
            out.writeBytes(msg.getOrigin().getData());
            out.writeBytes(msg.getDestination().getData());
            out.writeByte(msg.getSignature().length);
            out.writeByte(msg.getDirection().value);

            //Writing the signature.
            out.writeBytes(msg.getSignature());

            //Writing the content.
            out.writeBytes(msg.getData());

            logger.debug("Message sent to: {}", ctx.channel().remoteAddress(), out.capacity());
        } finally {
            out.release();
        }
    }
}
