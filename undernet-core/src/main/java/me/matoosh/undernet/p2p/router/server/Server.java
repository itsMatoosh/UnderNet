package me.matoosh.undernet.p2p.router.server;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerStatusEvent;

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
     * Sets up the server.
     */
    public void setup() {
        //Registering events.
        registerEvents();
    }

    /**
     * Starts the server.
     * @throws Exception
     */
    public void start() {
        //Changine the server status to starting.
        EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.STARTING));

        //Listening for network connections.
        networkListener.start();

        //Listening for direct connections.
        directListener.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        //Stopping the listeners.
        networkListener.stop();
        directListener.stop();
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        EventManager.registerEvent(ServerStatusEvent.class);
    }
}
