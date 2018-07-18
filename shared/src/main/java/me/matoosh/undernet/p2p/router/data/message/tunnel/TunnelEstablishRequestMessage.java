package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.p2p.router.data.message.MsgBase;
import me.matoosh.undernet.p2p.router.data.message.MsgType;

/**
 * Request to establish a tunnel.
 */
public class TunnelEstablishRequestMessage extends MsgBase {
    @Override
    public MsgType getType() {
        return MsgType.TUNNEL_ESTABLISH_REQUEST;
    }
}
