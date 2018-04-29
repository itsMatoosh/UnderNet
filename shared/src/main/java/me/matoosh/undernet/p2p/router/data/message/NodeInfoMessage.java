package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Message containing node info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class NodeInfoMessage extends MsgBase {
    /**
     * The netID.
     */
    public NetworkID networkID;
    /**
     * Constructs the info message using the node id.
     * @param node
     */
    public NodeInfoMessage (Node node) {
        this.networkID = node.getIdentity().getNetworkId();
    }
    public NodeInfoMessage(){}
}
