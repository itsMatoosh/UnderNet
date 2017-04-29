package me.matoosh.undernet.p2p.router.client;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.ConnectionException;
import me.matoosh.undernet.p2p.router.connection.ConnectionSide;
import me.matoosh.undernet.p2p.router.connection.SessionException;

/**
 * Connection made directly, Bluetooth, Wifi Direct etc.
 * Created by Mateusz Rębacz on 20.02.2017.
 */

public class DirectConnection extends Connection {

    /**
     * Default constructor
     * @param self
     * @param other
     * @param thread
     * @param connectionSide
     * @throws Exception
     */
    public DirectConnection(Node self, Node other, Thread thread, ConnectionSide connectionSide) throws Exception {
        super(self, other, thread, connectionSide);
    }

    @Override
    public void init() throws ConnectionException {

    }

    /**
     * A single connection session of the client.
     */
    @Override
    public void clientSession() throws SessionException {

    }

    /**
     * A single connection session of the server.
     *
     * @throws SessionException
     */
    @Override
    public void serverSession() throws SessionException {

    }

    @Override
    public void drop() {

    }
}
