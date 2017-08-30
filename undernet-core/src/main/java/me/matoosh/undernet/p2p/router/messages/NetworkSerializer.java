package me.matoosh.undernet.p2p.router.messages;

import java.io.OutputStream;

/**
 * Serializes messages from and to network streams.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class NetworkSerializer {
    /**
     * Writes the message to a stream.
     * @param stream
     */
    public static byte[] write(NetworkMessage msg) {
        //TODO: Serialisation.
        return null;
    }

    /**
     * Reads the message from a stream and puts its data in this class.
     */
    public static NetworkMessage read(int messageType, byte[] messagePayload) {
        //TODO: Deserialisation.
        return new NetworkMessage(messageType, messagePayload);
    }
}
