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
        //The length of the received message.
        int messageLength = in.capacity();

        try {
            //Reading the message.
            //Origin id
            byte[] originId = new byte[NetworkID.NETWORK_ID_LENGTH];
            in.readBytes(originId);
            NetworkID msgOrigin = new NetworkID(originId);

            //Destination id
            byte[] destinationId = new byte[NetworkID.NETWORK_ID_LENGTH];
            in.readBytes(destinationId);
            NetworkID msgDestination = new NetworkID(destinationId);

            byte signatureLength = in.readByte(); //signature length
            byte direction = in.readByte(); //message direction

            //Reading the signature.
            byte[] signature = new byte[signatureLength];
            in.readBytes(signature);

            //Creating the cached message.
            NetworkMessage receivedMessage = new NetworkMessage(msgOrigin, msgDestination, signature, NetworkMessage.MessageDirection.getByValue(direction));

            //Allocating data part.
            receivedMessage.setData(new byte[messageLength - NetworkMessage.NETWORK_MESSAGE_HEADER_LENGTH - signatureLength]);

            //Reading the data of the cached message.
            in.readBytes(receivedMessage.getData());

            out.add(receivedMessage);
        } finally {
            in.release();
        }
    }
}
