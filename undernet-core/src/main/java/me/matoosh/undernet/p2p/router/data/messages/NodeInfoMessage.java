package me.matoosh.undernet.p2p.router.data.messages;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;

import static me.matoosh.undernet.p2p.router.NetworkDatabase.logger;

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
        this.networkID = node.getIdentity().getNetworkId();
    }
    public NodeInfoMessage(){}
    /**
     * Convert the message data to byte[].
     *
     * @return
     */
    @Override
    public byte[] toByte() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream outputStream = new ObjectOutputStream(baos);
            outputStream.writeObject(networkID.data);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Error serializing a node info message!", e);
        }
        return baos.toByteArray();
    }

    /**
     * Convert the byte[] to the message.
     *
     * @param data
     */
    @Override
    public void fromByte(byte[] data) {
        try {
        ObjectInputStream inputStream = new ObjectInputStream(new ByteArrayInputStream(data));
        this.networkID = new NetworkID((BigInteger) inputStream.readObject());
        } catch (ClassNotFoundException e) {
            logger.error("Error deserializing a node info message!", e);
        } catch (IOException e) {
            logger.error("Error deserializing a node info message!", e);
        }
    }
}
