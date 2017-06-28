package me.matoosh.undernet.p2p.router.server;

import java.net.ServerSocket;

/**
 * Server module listening for incoming network connections.
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class NetworkListener extends NodeListener {
    /**
     * The port on which the listener is working.
     */
    public int port;
    /**
     * The listening socket.
     */
    public ServerSocket listenSocket;

    /**
     * Starts listening.
     */
    @Override
    public void start() {

    }

    /**
     * Stops listening.
     */
    @Override
    public void stop() {

    }
}
