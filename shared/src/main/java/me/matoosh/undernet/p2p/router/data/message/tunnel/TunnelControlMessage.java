package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.p2p.router.data.message.MsgBase;
import me.matoosh.undernet.p2p.router.data.message.MsgType;

/**
 * Message to check whether a tunnel is alive.
 */
public class TunnelControlMessage extends MsgBase {
    @Override
    public MsgType getType() {
        return MsgType.TUNNEL_CONTROL;
    }
}
