package me.matoosh.undernet.p2p.router;

import java.util.ArrayList;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.server.DirectListener;
import me.matoosh.undernet.p2p.router.server.NetworkListener;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * The network router.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class Router {
    /**
     * The client of this router.
     * Used for establishing connections with other nodes.
     */
    public Client client;
    /**
     * The server of this router.
     * Used for receiving connections from other nodes.
     */
    public Server server;

    /**
     * List of the currently active connections.
     */
    public ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     * Starts the router.
     * Starts the server listening process and establishes client connections.
     */
    public void start() {
        //Setting this as the currently used router.
        Node.self.router = this;

        //Creating and starting the server.
        server = new Server(new NetworkListener(), new DirectListener());
        server.start();

        //Creating and starting the client.
        client = new Client();
        client.start();
    }

    /**
     * Stops the router.
     */
    public void stop() {

    }
}
