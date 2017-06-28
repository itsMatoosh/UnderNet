package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Called when a connection is dropped.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ConnectionDroppedEvent extends ConnectionEvent {
    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionDroppedEvent(Connection c) {
        super(c);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
