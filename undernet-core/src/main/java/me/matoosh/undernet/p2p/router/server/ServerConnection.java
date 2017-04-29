package me.matoosh.undernet.p2p.router.server;

import java.net.Socket;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Represents a single connection with the server.
 * Created by Mateusz Rębacz on 18.02.2017.
 */

public class ServerConnection extends Connection {
    /**
     * Server making this connection.
     */
    public Server server;
    /**
     * The Thread used for this connection.
     */
    public Thread thread;
    /**
     * Node that the server is connected to.
     */
    public Node node;

    /**
     * Constructs a server connection.
     * @param server
     * @param thread
     * @throws Exception
     */
    public ServerConnection(Server server, Thread thread) throws Exception {
        super(thread);
        this.server = server;
        setup();
    }

    /**
     * Establishes the connection.
     * Set up before the connection loop begins.
     */
    @Override
    protected void establish() throws Exception {
        //Listen and accept the connection.
        UnderNet.logger.info("Listening for connections on: " + server.port);
        Socket clientSocket = server.serverSocket.accept();

        //TODO: ServerConnection establishment logic.
        //The node sends its current id.
        //If the id is empty, create an id based on our node id and send it back.
        //If the id is not empty, proceed.
        //Send hand-shake message.
        //Receive the node info and cache it.

        //The connection has been established, calling the event on the server.
        server.onConnectionEstablished(this);
    }

    /**
     * A single connection session of the server.
     */
    protected void session() throws Exception {
        UnderNet.logger.info("ServerConnection logic running.");
        //TODO: Logic
    }

    /**
     * Drops the connection.
     */
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
}
