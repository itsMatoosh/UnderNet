package me.matoosh.undernet.p2p.node;

import java.net.Inet4Address;

/**
 * Contains both the public and private addresses of a node.
 * Private address is only set if the node is on the local network.
 * Created by Mateusz Rębacz on 22.03.2017.
 */

public class NodeAddress {
    /**
     * Creates a new node address, based on the public and local IP addresses.
     */
    public NodeAddress(Inet4Address publicAddress, Inet4Address localAddress) {

    }
}
