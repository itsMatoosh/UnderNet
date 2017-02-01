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
     * Currently used Client instance.
     * */
    public static Client current;

    /**
     * Node used by the client.
     * */
    public Node node;

    /**
     * Instantiates a client.
     * */
    public Client() {
        node = new Node();
    }
    /**
     * Connects the client to the network.
     * */
    public void connect() {
        //Attempting to connect to each of the 5 most reliable nodes.
        for(Node node : NodeCache.getMostReliable(5, null)) {

        }
    }

    /**
     * Disconnects from the network.
     */
    public void disconnect() {
    }

    /**
     * Sets up the client.
     * */
    public static void setup () {
        current = new Client();
    }
}
