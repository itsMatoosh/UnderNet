package me.matoosh.undernet.p2p.router.messages;

/**
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class HandshakeMessage extends NetworkMessage {
    /**
     * Creates a message object with message data.
     *
     * @param type
     * @param payload
     */
    public HandshakeMessage(int type, byte[] payload) {
        super(type, payload);
    }
}
