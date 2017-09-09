package me.matoosh.undernet.p2p.router.connection;

/**
 * Occurs when an error happens within the connection session.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class ConnectionSessionException extends ConnectionException {
    /**
     * Message of the error.
     */
    public String message;

    public ConnectionSessionException(Connection c, ConnectionThreadType threadType, String message) {
        super(c, threadType);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
