package me.matoosh.undernet.p2p.node;

import java.io.Serializable;
import java.net.InetAddress;

import me.matoosh.undernet.p2p.router.Router;

/**
 * A single node within the network.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Node implements Serializable {
    /**
     * Address of this node.
     */
    public InetAddress address;

    /**
     * Reliability of the node.
     */
    public float reliability;

    /**
     * The router of this node.
     * Known only for self.
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
}
