package me.matoosh.undernet.event.connection.message;

import me.matoosh.undernet.event.connection.ConnectionEvent;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.messages.NetworkMessage;

/**
 * Represents events regarding network messages.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public abstract class ConnectionMessageEvent extends ConnectionEvent {
    /**
     * The network message.
     */
    public NetworkMessage message;

    public ConnectionMessageEvent(Connection connection, NetworkMessage message) {
        super(connection);
        this.message = message;
    }
}
