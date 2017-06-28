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

    public ConnectionException(Connection c) {
        this.connection = c;
    }
}
