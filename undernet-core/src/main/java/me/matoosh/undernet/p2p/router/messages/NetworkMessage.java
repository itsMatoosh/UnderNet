package me.matoosh.undernet.p2p.router.messages;

import java.io.Serializable;

/**
 * A message that can be sent between client and server.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage implements Serializable {
    /**
     * The type of the message.
     */
    public int messageType;
    /**
     * The payload of the message.
     */
    public byte[] payload;

    /**
     * Creates a message object with message data.
     * @param payload
     */
    public NetworkMessage(int type, byte[] payload) {
        this.messageType = type;
        this.payload = payload;
    }
}
