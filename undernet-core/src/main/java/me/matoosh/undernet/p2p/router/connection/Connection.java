package me.matoosh.undernet.p2p.router.connection;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.connection.ConnectionDroppedEvent;
import me.matoosh.undernet.event.connection.ConnectionErrorEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Represents a connection.
 * Created by Mateusz Rębacz on 22.03.2017.
 */

public abstract class Connection {
    /**
     * The Thread used for this connection.
     * Assigned when the connection is established.
     */
    public Thread thread;
    /**
     * The server of this connection.
     * Only set if side == ConnectionSide.Server
     */
    public Server server;
    /**
     * The client of this connection.
     * Only set if side == ConnectionSide.Client
     */
    public Client client;
    /**
     * The information of the node on the other side.
     */
    public Node other;
    /**
     * The side of the connection.
     */
    public ConnectionSide side;
    /**
     * The id of this connection.
     */
    public int id;

    /**
     * Input stream of this connection.
     * Assigned when the connection is established.
     */
    public InputStream inputStream;
    /**
     * Output stream of this connection.
     * Assigned when the connection is established.
     */
    public OutputStream outputStream;

    /**
     * Establishes the connection with the specified node on a new thread.
     * Needs to call runSession() AND onConnectionEstablished event.
     * @param client the client establishing the connection.
     * @param other the node to connect to.
     */
    public void establish(Client client, Node other) {
        this.side = ConnectionSide.CLIENT;
        this.client = client;
        this.other = other;

        onEstablishingConnection();
    }

    /**
     * Handles the incoming connection on a new thread.
     * Needs to call runSession() AND onConnectionEstablished event.
     * @param server the server receiving the connection.
     * @param other the node connecting.
     */
    public void receive(Server server, Node other) {
        this.side = ConnectionSide.SERVER;
        this.server = server;
        this.other = other;

        onReceivingConnection();
    }

    /**
     * Drops the connection.
     * Interrupts the connection loop thread.
     */
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        EventManager.callEvent(new ConnectionDroppedEvent(this, other));
        try {
            inputStream.close();
            outputStream.close();
            inputStream = null;
            outputStream = null;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when the connecton is being established.
     */
    protected abstract void onEstablishingConnection();
    /**
     * Called when the connection is being received.
     */
    protected abstract void onReceivingConnection();
    /**
     * Called when a connection error occurs.
     */
    public abstract void onConnectionError(ConnectionException e);
    /**
     * Called when the connection is dropped.
     */
    public abstract void onConnectionDropped();

    /**
     * Runs the connection session.
     * @throws Exception
     */
    protected void runSession() {
        //Starting the connection session.
        while (!thread.isInterrupted()) {
            try {
                session();
            } catch (ConnectionSessionException e) {
                EventManager.callEvent(new ConnectionErrorEvent(this, e));
            }
        }
    }

    /**
     * A single connection session.
     */
    protected abstract void session() throws ConnectionSessionException;
}
