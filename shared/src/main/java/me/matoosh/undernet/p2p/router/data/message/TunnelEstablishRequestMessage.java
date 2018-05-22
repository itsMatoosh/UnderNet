package me.matoosh.undernet.p2p.router.data.message;

import java.security.PublicKey;

public class TunnelEstablishRequestMessage extends MsgBase {
    /**
     * The encryption key.
     */
    public PublicKey encryptionKey;

    /**
     * Instantiates deserialized message content.
     *
     */
    public TunnelEstablishRequestMessage(PublicKey encryptionKey) {
        this.encryptionKey = encryptionKey;
    }

    @Override
    public MsgType getType() {
        return MsgType.TUNNEL_ESTABLISH_REQUEST;
    }
}
