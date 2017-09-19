package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Called when a connection has been established.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ConnectionEstablishedEvent extends ConnectionEvent {
    /**
     * The node the connection is made to.
     */
    public Node other;

    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionEstablishedEvent(Connection c, Node other) {
        super(c);
        this.other = other;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        connection.active = true;
        Router.logger.info("Connection has been established with: " + connection.other.address);
    }
}
