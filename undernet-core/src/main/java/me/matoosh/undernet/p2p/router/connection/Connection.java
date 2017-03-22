package me.matoosh.undernet.p2p.router.connection;

/**
 * Represents a connection.
 * Created by Mateusz Rębacz on 22.03.2017.
 */

public abstract class Connection {
    /**
     * The Thread used for this connection.
     */
    public Thread thread;

    /**
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
