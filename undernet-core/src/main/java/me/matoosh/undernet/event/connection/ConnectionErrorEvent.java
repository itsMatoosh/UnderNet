package me.matoosh.undernet.event.connection;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.KnownNode;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.ConnectionException;

/**
 * Called when a connection error occurs.
 * Error means that the connection was dropped due to a problem.
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
        //Debugging
        if(connection.other.getClass() == KnownNode.class){
            UnderNet.logger.error("Error while connecting to node: " + ((KnownNode) connection.other).username + " - " + connection.other.address + " over the network.");
        } else {
            UnderNet.logger.error("Error while connecting to node: " + connection.other.address + " by Internet.");
        }
        exception.printStackTrace();
        if(exception.getMessage() != null) {
            UnderNet.logger.info("Connection error: " + exception.getMessage());
        }

        //Calling the onError method.
        connection.onConnectionError(exception);

        //Dropping the connection.
        connection.drop();
    }
}
