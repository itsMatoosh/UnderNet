package me.matoosh.undernet.p2p.router.client;

import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.DirectConnection;
import me.matoosh.undernet.p2p.router.connection.NetworkConnection;

/**
 * Client part of the router.
 *
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Client {
    /**
     * List of the active serverConnections.
     */
    public ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     * Starts the client and connects to cached nodes based on the settings.
     */
    public void start() {
        //TODO
    }
    /**
     * Connects the client to a node.
     */
    public boolean connect(Node node) {
        UnderNet.logger.info("Connecting to node: " + node.address);

        //Creating a new connection instance.
        boolean success = false;
        Connection connection = new DirectConnection();
        if(!connection.establish(node)) {
            connection = new NetworkConnection();
            success = connection.establish(node);
        } else {
            success = true;
        }

        //If the connection is successfull, adding it to the connections list.
        if(success) {
            connections.add(connection);
        }

        return success;
    }

    /**
     * Disconnects from all nodes.
     */
    public void disconnectAll() {
        //Dropping all the serverConnections.
        for (Connection c:
             connections) {
            c.drop();
        }
    }
}
