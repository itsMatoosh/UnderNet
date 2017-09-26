package me.matoosh.undernet.p2p.router.data.message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Message containing node info.
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
        this.networkID = node.getIdentity().getNetworkId();
    }
    public NodeInfoMessage(){}

    /**
     * Serialization
     * @param oos
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.writeObject(networkID.data);
    }

    /**
     * Deserialization
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        this.networkID.data = (BigInteger) ois.readObject();
    }
}
