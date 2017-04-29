package me.matoosh.undernet.p2p.node;

import java.io.Serializable;
import java.net.InetAddress;

import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

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
     * Relative reliability of the node.
     */
    public float reliability;

    /**
     * The server of this node.
     * Known only for self.
     */
    public Server server;

    /**
     * The client of this node.
     * Known only for self.
     */
    public Client client;

    /**
     * The self node.
     */
    public static Node self;
}
