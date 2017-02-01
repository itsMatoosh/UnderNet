package me.matoosh.undernet.p2p.node;

import java.net.InetAddress;

/**
 * A single node within the network.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Node {
    /**
     * Address of this node.
     */
    public InetAddress address;

    /**
     * Relative reliability of the node.
     */
    public float reliability;
}
