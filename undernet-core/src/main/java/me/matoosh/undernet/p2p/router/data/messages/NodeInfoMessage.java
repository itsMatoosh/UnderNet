package me.matoosh.undernet.p2p.router.data.messages;

import java.math.BigInteger;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Contains node info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class NodeInfoMessage implements MsgBase {
    /**
     * The netID.
     */
    public NetworkID networkID;
    /**
     * Constructs the info message using the node id.
     * @param node
     */
    public NodeInfoMessage (Node node) {
        this.networkID = node.identity.networkID;
    }
    public NodeInfoMessage(){}
    /**
     * Convert the message data to byte[].
     *
     * @return
     */
    @Override
    public byte[] toByte() {
        return networkID.data.toByteArray();
    }

    /**
     * Convert the byte[] to the message.
     *
     * @param data
     */
    @Override
    public void fromByte(byte[] data) {
        this.networkID = new NetworkID(new BigInteger(512, data));
    }
}
