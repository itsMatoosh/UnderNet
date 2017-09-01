package me.matoosh.undernet.p2p.router.connection;

/**
 * Occurs when an IO happens in the connection.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ConnectionIOException extends ConnectionException {
    /**
     * The connection.
     */
    public Connection connection;

    public ConnectionIOException(Connection connection, ConnectionThreadType threadType) {
        super(connection, threadType);
    }
}
