package me.matoosh.undernet.p2p.router.client.connection;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Represents a single connection with the server.
 * Created by Mateusz Rębacz on 18.02.2017.
 */

public abstract class ClientConnection extends Connection{
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
    public ClientConnection(Client client, Node node, Thread thread) throws Exception {
        super(thread);
        //Setting the variables.
        this.client = client;
        this.node = node;

        //Starting the connection session.
        setup();
    }
}