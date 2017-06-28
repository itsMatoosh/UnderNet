package me.matoosh.undernet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import me.matoosh.undernet.file.FileManager;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Core of the UnderNet package.
 */
public class UnderNet
{
    /**
     * Currently used logger.
     */
    public static Logger logger = LoggerFactory.getLogger("undernet.core");
    /**
     * File manager of the current platform.
     */
    public static FileManager fileManager;


    /**
     * Number of reconnects during the connection session.
     */
    private static int reconnectNum = 0;
    /**
     * Whether the client connection session is active.
     */
    private static boolean sessionActive = false;

    /**
     * Sets up UnderNet.
     */
    public static void setup(FileManager fileManager) {
        //Setting the file manager.
        UnderNet.fileManager = fileManager;

        //Loading up the node cache.
        NodeCache.load();

        //Setting up the self node.
        Node.self = new Node();
    }

    /**
     * Connects to one of the known nodes.
     */
    public static void connect() {
        //Connecting the client to the network.
        logger.info("Connecting to UnderNet...");
        sessionActive = true;

        //Starting the router.
        new Router().start();

        try {
            //Starting the server.
            usedServer.start();

            //Attempting to connect to each of the 5 most reliable nodes.
            ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
            if(nodesToConnectTo == null) {
                onConnectionError("No nodes cached!", null, false);

            } else {
                for(Node node : nodesToConnectTo) {
                    usedClient.connect(node);
                }
            }
        } catch (Exception e) {
            onConnectionError(e.toString(), e, true);
        }
    }

    /**
     * Disconnects from the network.
     */
    public static void disconnect() {
        Node.self.router.stop();
    }

    /**
     * Called when a connection error occurs.
     * @param s
     */
    public static void onConnectionError(String s, Exception e, boolean reconnect) {
        //Printing the error.
        if(s != null) {
            logger.error("There was a problem with the connection to UnderNet: " + s);
        }
        if(e != null) {
            e.printStackTrace();
        }

        //Resetting the network devices.
        resetNetworkDevices();

        //Reconnecting if possible.
        if(sessionActive && reconnect) {
            reconnectNum++;
            //Checking if we should reconnect.
            if(reconnectNum >= 5) {
                logger.error("Exceeded the maximum number of reconnect attempts!");
                onConnectionEnded();
            }

            logger.info("Attempting to reconnect for: " + reconnectNum + " time...");
        }
    }

    /**
     * Resets the network devices to be ready to reconnect.
     */
    private static void resetNetworkDevices() {
        usedServer.stop();
        usedClient.disconnect();
    }

    /**
     * Called when the connection ends.
     */
    public  static void onConnectionEnded() {
        //Resetting the reconn variables.
        sessionActive = false;
        reconnectNum = 0;
    }
}
