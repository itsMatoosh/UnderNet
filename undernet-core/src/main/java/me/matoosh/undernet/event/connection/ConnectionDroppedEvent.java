package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Called when a connection is dropped.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ConnectionDroppedEvent extends ConnectionEvent {
    /**
     * The node with which the connection was dropped.
     */
    public Node other;

    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionDroppedEvent(Connection c, Node other) {
        super(c);
        this.other = other;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
