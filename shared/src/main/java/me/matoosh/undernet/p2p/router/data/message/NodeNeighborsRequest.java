package me.matoosh.undernet.p2p.router.data.message;

/**
 * Requests neighbor information from node.
 */
public class NodeNeighborsRequest extends ContentlessMsgBase {
    @Override
    public MsgType getType() {
        return MsgType.NODE_NEIGHBORS_REQUEST;
    }
}
