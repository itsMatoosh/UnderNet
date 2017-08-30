package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Called when a connection is accepted. Before the handshake.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class ConnectionAcceptedEvent extends ConnectionEvent {
    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionAcceptedEvent(Connection c) {
        super(c);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        UnderNet.logger.info("Accepted connection from: " + connection.other);
    }
}
