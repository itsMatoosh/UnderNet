package me.matoosh.undernet;

import java.util.ArrayList;
import java.util.logging.Logger;

import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

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
     * Currently used logger.
     */
    public static Logger logger;

    /**
     * Sets up UnderNet.
     */
    public static void setup(Logger logger) {
        //Loading up the node cache.
        NodeCache.load();

        //Setting up the client.
        usedClient = new Client(new Node() /* TODO: Get the node represented by this client. */);

        //Starting the server.
        usedServer = new Server(42069);
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect() {
        //Connecting the client to the network.
        System.out.println("Connecting to UnderNet...");
        try {
            //Starting the server.
            usedServer.start();

            //Attempting to connect to each of the 5 most reliable nodes.
            ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
            if(nodesToConnectTo == null) {

            }
            for(Node node : nodesToConnectTo) {
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
