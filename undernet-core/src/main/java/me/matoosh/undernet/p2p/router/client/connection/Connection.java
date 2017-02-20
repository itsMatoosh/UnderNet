package me.matoosh.undernet.p2p.router.client.connection;

import java.io.IOException;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Represents a single connection with the server.
 * Created by Mateusz Rębacz on 18.02.2017.
 */

public abstract class Connection {
    /**
     * Client making this connection.
     */
    public Client client;
    /**
     * The Thread used for this connection.
     */
    public Thread thread;
    /**
     * Node that the connection is made to.
     */
    public Node node;

    //Creates a new connection on a specific thread.
    public Connection(Client client, Node node, Thread thread) throws Exception {
        //Setting the variables.
        this.client = client;
        this.thread = thread;
        this.node = node;

        //Starting the connection session.
        init();
        while(!Thread.currentThread().isInterrupted()) {
            session();
        }
    }

    /**
     * Initializes the connection.
     */
    public abstract void init() throws ConnectionException, IOException;
    /**
     * A single connection session of the client.
     */
    public abstract void session();

    /**
     * Drops the connection.
     */
    public abstract void drop();
}