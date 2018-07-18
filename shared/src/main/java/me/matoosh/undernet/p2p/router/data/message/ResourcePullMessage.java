package me.matoosh.undernet.p2p.router.data.message;

/**
 * Message used to pull resources.
 */
public class ResourcePullMessage extends MsgBase {
    @Override
    public MsgType getType() {
        return MsgType.RES_PULL;
    }
}
