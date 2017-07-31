package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * An event concerning a connection.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public abstract class ConnectionEvent extends Event {
    /**
     * The connection.
     */
    public Connection connection;

    /**
     * Creates a new connection event, given connection.
     * @param c
     */
    public ConnectionEvent(Connection c) {
        this.connection = c;
    }
}
