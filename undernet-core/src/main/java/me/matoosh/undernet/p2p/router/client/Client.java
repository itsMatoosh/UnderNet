package me.matoosh.undernet.p2p.router.client;

import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Logger;

import me.matoosh.undernet.p2p.node.KnownNode;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.connection.Connection;
import me.matoosh.undernet.p2p.router.client.connection.InternetConnection;

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
     * Client socket of the client.
     */
    public Socket clientSocket;
    /**
     * List of the active connections.
     */
    public ArrayList<Connection> connections = new ArrayList<Connection>();

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
    public void connect(KnownNode node) {
        //Attempting to connect directly to the node.
        if(!connectDirectly(node)) {
            connectByInternet(node);
        }
    }

    /**
     * Connects to a node through Internet.
     * @param node
     */
    private void connectByInternet(final KnownNode node) {
        //Connecting to the node in a separate thread.
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //Adding the connection to the list and starting the connection session.
                    connections.add(new InternetConnection(Client.this, node, Thread.currentThread()));
                } catch (Exception e) {
                    System.out.println("Error while connecting to node: " + node.username + " by Internet.");
                    Logger.getGlobal().info("Connection error: " + e.toString());
                }
            }
        });

        t.start();
    }

    /**
     * Connects to a node directly.
     * Uses Wifi-Direct/Bluetooth
     * @param node
     * @return whether the direct connection was succesful.
     */
    private boolean connectDirectly(KnownNode node) {
        //TODO: Add logic.
        return false;
    }


    /**
     * Disconnects from the nodes.
     */
    public void disconnect() {
        for (Connection c:
             connections) {
            c.drop();
        }
    }
}
