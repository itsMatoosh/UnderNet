package me.matoosh.undernet.p2p.router.connection;

/**
 * Occurs when an error happens within the connection session.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class SessionException extends ConnectionException {
    public SessionException(Connection c) {
        super(c);
    }
}
