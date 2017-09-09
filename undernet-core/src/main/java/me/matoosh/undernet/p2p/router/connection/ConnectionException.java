package me.matoosh.undernet.p2p.router.connection;

/**
 * An exception related to a connection.
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class ConnectionException extends Exception {
    /**
     * The connection.
     */
    public Connection connection;
    /**
     * The type of thread that produced this exception.
     */
    public ConnectionThreadType connectionThreadType;

    public ConnectionException(Connection c, ConnectionThreadType threadType) {
        this.connection = c;
        this.connectionThreadType = threadType;
    }
}
