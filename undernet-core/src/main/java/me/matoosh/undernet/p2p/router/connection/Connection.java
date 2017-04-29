package me.matoosh.undernet.p2p.router.connection;

<<<<<<< HEAD
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import me.matoosh.undernet.p2p.node.Node;

/**
 * Represents a single connection with the server.
 * Created by Mateusz Rębacz on 18.02.2017.
=======
/**
 * Represents a connection.
 * Created by Mateusz Rębacz on 22.03.2017.
>>>>>>> origin/master
 */

public abstract class Connection {
    /**
     * The Thread used for this connection.
     */
    public Thread thread;

    /**
<<<<<<< HEAD
     * The self node.
     */
    public Node self;
    /**
     * Node that the connection is made to.
     */
    public Node other;

    /**
     * The input stream of the connection.
     */
    public InputStream inputStream;
    /**
     * The output stream of the connection.
     */
    public OutputStream outputStream;

    /**
     * The connection side in the current context.
     */
    public ConnectionSide connectionSide;

    //Creates a new connection on a specific thread.
    public Connection(Node self, Node other, Thread thread, ConnectionSide connectionSide) throws Exception {
        //Setting the variables.
        this.self = self;
        this.thread = thread;
        this.other = other;
        this.connectionSide = connectionSide;

        //Starting the connection session.
        init();
        while(!Thread.currentThread().isInterrupted()) {
            if(connectionSide == ConnectionSide.CLIENT) {
                clientSession();
            } else {
                serverSession();
            }
        }
    }

    /**
     * Initializes the connection.
     */
    public abstract void init() throws ConnectionException, IOException;
    /**
     * A single connection session of the client.
     */
    public abstract void clientSession() throws SessionException;

    /**
     * A single connection session of the server.
     * @throws SessionException
     */
    public abstract void serverSession() throws SessionException;

    /**
     * Drops the connection.
     */
    public abstract void drop();
}
=======
     * Creates a new connection on the specified thread.
     * @param thread
     * @throws Exception
     */
    public Connection(Thread thread) throws Exception {
        //Setting the variables.
        this.thread = thread;
    }

    /**
     * Starts the setup of the connection.
     */
    protected void setup() throws Exception {
        establish();
        runSession();
    }

    /**
     * Establishes the connection.
     * Set up before the connection loop begins.
     */
    protected abstract void establish() throws Exception;

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
     * Called when the connection is dropped.
     */
    protected abstract void onConnectionDropped();

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
>>>>>>> origin/master
