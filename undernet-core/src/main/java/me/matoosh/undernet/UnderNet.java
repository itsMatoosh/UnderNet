package me.matoosh.undernet;

import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Client;
import me.matoosh.undernet.p2p.router.Server;

/**
 * Core of the UnderNet package.
 */
public class UnderNet {

    /**
     * Currently used client.
     */
    public static Client usedClient;
    /**
     * Currently used server.
     */
    public static Server usedServer;

    /**
     * Sets up UnderNet.
     */
    public static void setup() {
        //Loading up the node cache.
        NodeCache.load();

        //Setting up the client.
        usedClient = new Client(new Node());
        usedClient.setup();

        //Starting the server.
        usedServer = new Server(42069);
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect() {
        //Connecting the client to the network.
        try {
            //Starting the server.
            usedServer.start();

            //Attempting to connect to each of the 5 most reliable nodes.
            for(Node node : NodeCache.getMostReliable(5, null)) {
                usedClient.connect(node);
            }
        } catch (Exception e) {
            System.out.println("There was a problem while connecting to UnderNet: " + e.toString());
            e.printStackTrace();
        }
    }

    /**
     * Disconnects from the network.
     */
    public static void disconnect() {
        usedClient.disconnect();
        usedServer.stop();
    }
}
