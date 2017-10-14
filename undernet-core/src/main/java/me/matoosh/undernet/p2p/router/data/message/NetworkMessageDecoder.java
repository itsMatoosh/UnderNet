package me.matoosh.undernet.p2p.router.data.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileChunkMessage;

/**
 * Used to decode network messages from received bytes.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkMessageDecoder extends ByteToMessageDecoder {
    /**
     * The message currently being read.
     */
    private NetworkMessage cachedMessage;
    /**
     * The current data write index.
     */
    private int dataWriteIndex = 0;
    /**
     * Number of bytes to discard.
     */
    private int bytesToDiscard = 0;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessageDecoder.class);

    /**
     * Decode the from one {@link ByteBuf} to an other. This method will be called till either the input
     * {@link ByteBuf} has nothing to read when return from this method or till nothing was read from the input
     * {@link ByteBuf}.
     *
     * @param ctx the {@link ChannelHandlerContext} which this {@link ByteToMessageDecoder} belongs to
     * @param in  the {@link ByteBuf} from which to read data
     * @param out the {@link List} to which decoded messages should be added
     * @throws Exception is thrown if an error occurs
     */
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        //Caching the message header if not yet cached.
        if(in.readableBytes() >= 15 && cachedMessage == null) {
            //Decoding the message header.
            int msgId = in.readInt();
            long expiration = in.readLong();
            byte checksum = in.readByte();
            short dataLenght = in.readShort();

            //Checking the message expiration.
            //if(System.currentTimeMillis() < expiration) {
                //Creating the cached message.
                cachedMessage = null;
                if(msgId >= 1000) {
                    cachedMessage = new FileChunkMessage();
                } else {
                    cachedMessage = new NetworkMessage();
                }

                cachedMessage.msgId = msgId;
                cachedMessage.expiration = expiration;
                cachedMessage.checksum = checksum;
                cachedMessage.dataLength = dataLenght;
                cachedMessage.data = ByteBuffer.wrap(new byte[dataLenght - Short.MIN_VALUE]);
            /*} else {
                //Discarding the bytes.
                logger.warn("Received an expired message with id: " + msgId + ", discarding...");
                bytesToDiscard = dataLenght;
            }*/
        }
        //Reading the data of the cached message.
        while((dataWriteIndex < cachedMessage.data.capacity() || bytesToDiscard > 0) && in.readableBytes() > 0) {
            if(bytesToDiscard > 0) {
                bytesToDiscard -= in.readableBytes();
                in.release();
                if(bytesToDiscard <= 0) {
                    //All necessary bytes discarded.
                    bytesToDiscard = 0;
                    cachedMessage = null;
                    dataWriteIndex = 0;
                    return;
                }
            } else {
                cachedMessage.data.put(dataWriteIndex, in.readByte());
                dataWriteIndex++;
            }
        }
        //Checking if all the data has been received.
        if(dataWriteIndex >= cachedMessage.data.capacity()) {
            //Message data received. Outputting the constructed message.
            out.add(cachedMessage);

            //Resetting vars.
            cachedMessage = null;
            dataWriteIndex = 0;
        }
    }
}
