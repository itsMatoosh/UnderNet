package me.matoosh.undernet.p2p.router.data.messages;

/**
 * Serializes messages from and to network streams.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class NetworkMessageSerializer {
    /**
     * Writes a message base object to a byte array.
     * @param msg the network message to serialize.
     */
    public static byte[] write(MessageBase msg) {
        //TODO: Serialisation.
        return null;
    }

    /**
     * Reads the message from a byte array into a MessageBase instance.
     */
    public static MessageBase read(int messageType, byte[] messagePayload) {
        //TODO: Deserialization.
        return new MessageBase();
    }
}
