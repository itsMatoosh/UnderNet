package me.matoosh.undernet.p2p.router.connection;

/**
 * Called when an error occurs during a handshake with client and server.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class ConnectionHandshakeException extends ConnectionException {
    public ConnectionHandshakeException(Connection c, ConnectionThreadType threadType) {
        super(c, threadType);
    }
}
