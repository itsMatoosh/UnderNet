package me.matoosh.undernet.p2p.router.connection;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.connection.ConnectionErrorEvent;

/**
 * Represents a direct connection.
 * Bluetooth, Wi-Fi Direct, IR etc.
 * Created by Mateusz Rębacz on 26.06.2017.
 */

public class DirectConnection extends Connection
{
    /**
     * Called when the connecton is being established.
     */
    @Override
    protected void onEstablishingConnection() {
        //TODO: Check for the node availability.
        //TODO: Establish connection.

        //For now the connection is gonna throw an error unconditionally.
        EventManager.callEvent(new ConnectionErrorEvent(this, new ConnectionNotAvailableException(this, ConnectionThreadType.SEND)));
    }

    /**
     * Called when the connection is being received.
     */
    @Override
    protected void onReceivingConnection() {
        //TODO

        //For now the connection is gonna throw an error unconditionally.
        EventManager.callEvent(new ConnectionErrorEvent(this, new ConnectionNotAvailableException(this, ConnectionThreadType.SEND)));
    }

    /**
     * Called when a connection error occurs.
     *
     * @param e
     */
    @Override
    public void onConnectionError(ConnectionException e) {

    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    public void onConnectionDropped() {
        //TODO
    }

    /**
     * Receiving logic.
     */
    @Override
    protected void receive() throws ConnectionSessionException {

    }

    /**
     * Sending logic.
     *
     * @throws ConnectionSessionException
     */
    @Override
    protected void send() throws ConnectionSessionException {

    }
}
