package me.matoosh.undernet.p2p.router.data.message;

/**
 * Message containing node info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class NodeInfoMessage extends MsgBase {
    /**
     * Constructs the node info message.
     */
    public NodeInfoMessage () {

    }

    @Override
    public MsgType getType() {
        return MsgType.NODE_INFO;
    }
}
