package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;
import me.matoosh.undernet.p2p.router.data.message.MsgType;

import java.security.interfaces.ECPublicKey;

public class TunnelEstablishResponseMessage extends MsgBase {

    /**
     * The public key of the destination node.
     * Different from the destination NetworkID.
     */
    public byte[] publicKey;

    public TunnelEstablishResponseMessage(ECPublicKey publicKey) {
        this.publicKey = KeyTools.toUncompressedPoint(publicKey);
    }

    @Override
    public MsgType getType() {
        return MsgType.TUNNEL_ESTABLISH_RESPONSE;
    }
}
