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
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) {
        //Caching the message header if not yet cached.
        if(in.readableBytes() >= NetworkMessage.NETWORK_MESSAGE_HEADER_LENGTH && cachedMessage == null) {
            //Decoding the message header.

            //Origin id
            byte[] originId = new byte[NetworkID.NETWORK_ID_LENGTH];
            in.readBytes(originId);
            NetworkID msgOrigin = new NetworkID(originId);

            //Destination id
            byte[] destinationId = new byte[NetworkID.NETWORK_ID_LENGTH];
            in.readBytes(destinationId);
            NetworkID msgDestination = new NetworkID(destinationId);

            short dataLenght = in.readShort(); //content data length.
            byte signatureLength = in.readByte(); //signature length
            byte direction = in.readByte(); //message direction

            //Reading the signature.
            byte[] signature = new byte[signatureLength];
            in.readBytes(signature);

            //Creating the cached message.
            cachedMessage = new NetworkMessage(msgOrigin, msgDestination, signature, NetworkMessage.MessageDirection.getByValue(direction));
            cachedMessage.data = ByteBuffer.allocate(dataLenght - Short.MIN_VALUE);
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
