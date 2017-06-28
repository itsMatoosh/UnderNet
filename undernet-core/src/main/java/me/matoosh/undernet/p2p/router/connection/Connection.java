package me.matoosh.undernet.p2p.router.connection;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import me.matoosh.undernet.p2p.node.Node;

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
     * List of the registered event listeners for this connection.
     */
    public ArrayList<ConnectionEventListener> connectionEventListeners = new ArrayList<ConnectionEventListener>();

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
     * Needs to call runSession().
     * @param other the node to connect to.
     * @throws Exception
     */
    public abstract void establish(Node other);

    /**
     * Handles the incoming connection on a new thread.
     * Needs to call runSession().
     * @param other the node to connect to.
     * @throws Exception
     */
    public abstract void receive(Node other);

    /**
     * Drops the connection.
     * Interrupts the connection loop thread.
     */
    public void drop() {
        if(thread != null && thread.isAlive()) {
            thread.interrupt();
        }
        onConnectionDropped();
    }

    /**
     * Called when a connection error occurs.
     */
    private void onConnectionError(ConnectionException e) {
        for (ConnectionEventListener listener:
             connectionEventListeners) {
            listener.onConnectionError(e);
        }
    }
    /**
     * Called when the connection is dropped.
     */
    private void onConnectionDropped() {
        for (ConnectionEventListener listener:
             connectionEventListeners) {
            listener.onConnectionDropped(this);
        }
    }

    /**
     * Runs the connection session.
     * @throws Exception
     */
    private void runSession() throws Exception {
        //Starting the connection session.
        while (!thread.isInterrupted()) {
            session();
        }
    }

    /**
     * A single connection session.
     */
    protected abstract void session() throws Exception;
}
