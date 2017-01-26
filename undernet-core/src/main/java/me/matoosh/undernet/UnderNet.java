package me.matoosh.undernet;

import me.matoosh.undernet.p2p.Client;
import me.matoosh.undernet.p2p.cache.NodeCache;

/**
 * Core of the UnderNet package.
 */
public class UnderNet {

    /**
     * Sets up UnderNet.
     */
    public static void setup() {
        //Loading up the node cache.
        NodeCache.load();

        //Setting up the client.
        Client.setup();
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect() {
        //Connecting the client to the network.
        Client.current.connect();
    }

    /**
     * Disconnects from the network.
     */
    public static void disconnect() {
        Client.current.disconnect();
    }
}
