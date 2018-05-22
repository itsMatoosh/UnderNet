package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Message containing node info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class NodeInfoMessage extends MsgBase {

    /**
     * The network id of the node.
     */
    public NetworkID networkID;

    /**
     * Constructs the node info message.
     */
    public NodeInfoMessage (NetworkID id) {
        this.networkID = id;
    }

    @Override
    public MsgType getType() {
        return MsgType.NODE_INFO;
    }
}
