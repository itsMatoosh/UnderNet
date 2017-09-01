package me.matoosh.undernet.p2p.router.connection;

/**
 * Occurs when the connection you're trying to use in not available.
 * Created by Mateusz Rębacz on 29.06.2017.
 */

public class ConnectionNotAvailableException extends ConnectionException {
    public ConnectionNotAvailableException(Connection c, ConnectionThreadType threadType) {
        super(c, threadType);
    }
}
