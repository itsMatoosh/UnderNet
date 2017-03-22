package me.matoosh.undernet.p2p.router.client.connection;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Represents a direct client connection.
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class DirectConnection extends ClientConnection {

    public DirectConnection(Client client, Node node, Thread thread) throws Exception {
        super(client, node, thread);
    }

    /**
     * Establishes the connection.
     * Set up before the connection loop begins.
     */
    @Override
    protected void establish() throws Exception {

    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    protected void onConnectionDropped() {

    }

    /**
     * A single connection session of the client.
     */
    @Override
    public void session() {

    }
}
