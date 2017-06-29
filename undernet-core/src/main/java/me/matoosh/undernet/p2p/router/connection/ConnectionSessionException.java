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

    public ConnectionSessionException(Connection c, String message) {
        super(c);
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
