package me.matoosh.undernet.p2p.router.connection;

/**
 * Interface for all the classes listening for connection events.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public interface ConnectionEventListener {
    /**
     * Called when an error occurs with a connection.
     * @param e the exception that occured.
     */
    public void onConnectionError(ConnectionException e);

    /**
     * Called when a connection is dropped.
     * @param c the droped connection.
     */
    public void onConnectionDropped(Connection c);
}
