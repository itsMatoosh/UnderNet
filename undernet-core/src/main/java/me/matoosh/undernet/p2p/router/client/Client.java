package me.matoosh.undernet.p2p.router.client;

import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientErrorEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.p2p.cache.NodeCache;
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
     * Sets up the client.
     */
    public void setup() {
        //Registering the client events.
        registerEvents();
    }
    /**
     * Starts the client and connects to cached nodes based on the settings.
     */
    public void start() {
        //Attempting to connect to each of the 5 most reliable nodes.
        ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
        if(nodesToConnectTo == null) {
            EventManager.callEvent(new ClientErrorEvent(this, new ClientNoNodesCachedException(this)));
        } else {
            for(Node node : nodesToConnectTo) {
                connect(node);
            }
        }
    }
    /**
     * Connects the client to a node.
     */
    public void connect(Node node) {
        UnderNet.logger.info("Connecting to node: " + node.address);

        //Creating a new connection instance.
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
    }

    /**
     * Stops the client.
     */
    public void stop() {
        //Disconnecting all.
        disconnectAll();
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

    /**
     * Registers the client handlers.
     */
    private void registerEvents() {
        EventManager.registerEvent(ClientStatusEvent.class);
        EventManager.registerEvent(ClientErrorEvent.class);
    }
}
