package me.matoosh.undernet.p2p.node;

import java.io.Serializable;
import java.net.SocketAddress;
import java.util.ArrayList;

import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessage;

/**
 * A single node within the network.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Node implements Serializable {
    /**
     * Connection address of this node.
     */
    public SocketAddress address;
    /**
     * Connection port of the node.
     */
    public int port = 2017;

    /**
     * Reliability of the node.
     * TODO: Actually make this useful.
     */
    public float reliability;

    /**
     * The list of network messages awaiting to be sent to the node.
     */
    public ArrayList<NetworkMessage> messagesToSend = new ArrayList<NetworkMessage>();

    /**
     * The router of this node.
     * Known only for self node.
     **/
    public Router router;


    /**
     * The self node.
     */
    public static Node self;

    /**
     * Returns the address of the node.
     * @return
     */
    @Override
    public String toString() {
        return address.toString();
    }

    /**
     * Sets the address of the node.
     * @param address
     */
    public void setAddress(SocketAddress address) {
        //TODO: Caching integration.
        this.address = address;
    }
    public void setPort(int port) {
        //TODO: Cache update
        this.port = port;
    }
}
