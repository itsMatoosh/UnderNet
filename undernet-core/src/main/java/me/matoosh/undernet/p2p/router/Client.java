package me.matoosh.undernet.p2p.router;

import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;

/**
 * Client part of the router.
 *
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Client {
    /**
     * Node represented by the client.
     */
    public Node node;

    /**
     * Instantiates a client.
     * @param node
     */
    public Client(Node node) {
        this.node = node;
    }

    /**
     * Connects the client to the network.
     */
    public void connect(Node node) {

    }

    /**
     * Disconnects from the network.
     */
    public void disconnect() {
    }

    /**
     * Sets up the client.
     */
    public static void setup () {

    }
}
