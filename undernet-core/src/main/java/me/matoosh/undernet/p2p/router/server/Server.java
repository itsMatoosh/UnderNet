package me.matoosh.undernet.p2p.router.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Server part of the router.
 *
 * Created by Mateusz Rębacz on 30.01.2017.
 */

public class Server
{
    /**
     * Current status of the server.
     */
    public ServerStatus status = ServerStatus.NOT_STARTED;
    /**
     * The network listener of this server.
     */
    public NetworkListener networkListener;
    /**
     * The direct listener of this server.
     */
    public DirectListener directListener;

    /**
     * Creates a server instance using a specified port.
     */
    public Server(NetworkListener networkListener, DirectListener directListener) {
        this.networkListener = networkListener;
        this.directListener = directListener;
    }

    /**
     * Starts the server.
     * @throws Exception
     */
    public void start() throws Exception {
        //Changine the server status to starting.
        EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.STARTING));

        //Registering events
        registerEvents();

        //Listening for network connections.
        networkListener.start();

        //Listening for direct connections.
        directListener.start();
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        //ServerStatusEvent
        EventManager.registerEvent(ServerStatusEvent.class);
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Stopping the listeners.
        networkListener.stop();
        directListener.stop();
    }
    //Events

    /**
     * Called when a connection has been established.
     * @param c
     */
    public void onConnectionEstablished(ServerConnection c) {
        UnderNet.logger.info("New connection established with " + c.node);
        //Accepting new serverConnections.
        acceptingConnections = true;
    }
}
