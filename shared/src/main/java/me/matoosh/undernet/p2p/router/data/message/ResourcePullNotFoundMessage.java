package me.matoosh.undernet.p2p.router.data.message;

/**
 * Message sent when a pulled resource couldn't be found.
 */
public class ResourcePullNotFoundMessage extends ContentlessMsgBase {
    public ResourcePullNotFoundMessage() {}
    @Override
    public MsgType getType() {
        return MsgType.RES_PULL_NOT_FOUND;
    }
}
