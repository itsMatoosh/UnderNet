package me.matoosh.undernet.p2p.router.data.message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.util.List;

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
        if(in.readableBytes() >= NetworkMessage.NETWORK_MESSAGE_HEADER_LENGTH && cachedMessage == null) {
            //Decoding the message header.
            NetworkID msgOrigin = new NetworkID(in.readBytes(NetworkID.NETWORK_ID_LENGTH).array());
            NetworkID msgDestination = new NetworkID(in.readBytes(NetworkID.NETWORK_ID_LENGTH).array());
            byte[] checksum = in.readBytes(16).array();
            short dataLenght = in.readShort();
            byte direction = in.readByte();

            //Creating the cached message.
            cachedMessage = new NetworkMessage(msgOrigin, msgDestination, checksum, direction);
            cachedMessage.data = ByteBuffer.wrap(new byte[dataLenght - Short.MIN_VALUE]);
        }
        //Reading the data of the cached message.
        while(cachedMessage != null && dataWriteIndex < cachedMessage.data.capacity() && in.readableBytes() > 0) {
            cachedMessage.data.put(dataWriteIndex, in.readByte());
            dataWriteIndex++;
        }

        //Checking if all the data has been received.
        if(cachedMessage != null && dataWriteIndex >= cachedMessage.data.capacity()) {
            //Message data received. Outputting the constructed message.
            out.add(cachedMessage);

            //Resetting vars.
            cachedMessage = null;
            dataWriteIndex = 0;
        }
    }
}
