package me.matoosh.undernet.p2p.router.connection;

import me.matoosh.undernet.p2p.node.Node;

/**
 * Represents a direct connection.
 * Bluetooth, Wi-Fi Direct, IR etc.
 * Created by Mateusz Rębacz on 26.06.2017.
 */

public class DirectConnection extends Connection
{
    /**
     * Establishes the connection with the specified node.
     * Needs to call runSession().
     *
     * @param other the node to connect to.
     * @return whether the connection was established correctly.
     * @throws Exception
     */
    @Override
    protected boolean establish(Node other) throws Exception {
        //TODO: Check for the node availability.
        //TODO: Establish connection.

        //For now the direct connection is just a place holder.
        return false;
    }


    /**
     * Handles the received connection.
     * Needs to call runSession().
     *
     * @param other
     * @throws Exception
     */
    @Override
    protected void receive(Node other) throws Exception {

    }

    /**
     * Called when the connection is dropped.
     */
    @Override
    protected void onConnectionDropped() {

    }

    /**
     * A single connection session.
     */
    @Override
    protected void session() throws Exception {

    }
}
