package me.matoosh.undernet.event.connection.message;

import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.messages.NetworkMessage;

/**
 * Called when a message is received on connection.
 * Created by Mateusz Rębacz on 30.08.2017.
 */

public class ConnectionMessageReceivedEvent extends ConnectionMessageEvent {
    public ConnectionMessageReceivedEvent(Connection connection, NetworkMessage message) {
        super(connection, message);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
