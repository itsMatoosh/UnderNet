package me.matoosh.undernet.p2p.router.client.connection;

import java.net.InetSocketAddress;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;

/**
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class InternetConnection extends ClientConnection {

    public InternetConnection(Client client, Node node, Thread thread) throws Exception {
        super(client, node, thread);
    }

    /**
     * Establishes the connection.
     * Set up before the connection loop begins.
     */
    @Override
    protected void establish() throws Exception {
        //Connecting to the node's server.
        client.clientSocket.connect(new InetSocketAddress(node.address, 42069 /*TODO: Change the port to a random one.*/));
    }

    @Override
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    protected void onConnectionDropped() {

    }

    /**
     * A single connection session.
     */
    @Override
    protected void session() throws Exception {

    }
}
