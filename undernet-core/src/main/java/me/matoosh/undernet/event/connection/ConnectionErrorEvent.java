package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.ConnectionException;

/**
 * Called when a connection error occurs.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ConnectionErrorEvent extends ConnectionEvent {
    /**
     * The connection exception.
     */
    public ConnectionException exception;

    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionErrorEvent(Connection c, ConnectionException e) {
        super(c);
        this.exception = e;
    }

    /**
     * Executed when the event is called.
     */
    @Override
    public void onCalled() {

    }
}
