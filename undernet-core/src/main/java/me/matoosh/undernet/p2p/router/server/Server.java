package me.matoosh.undernet.p2p.router.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.server.ServerErrorEvent;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Server part of the router.
 *
 * Created by Mateusz Rębacz on 30.01.2017.
 */

public class Server
{
    /**
     * The router.
     */
    public Router router;
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
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Server.class);

    /**
     * Creates a server instance using a specified port.
     */
    public Server(Router router) {
        this.router = router;
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
        logger.info("Starting the server...");

        //Changing the server status to starting.
        EventManager.callEvent(new ServerStatusEvent(Server.this, ServerStatus.STARTING));

        //Listening for network connections.
        if(networkListener == null) {
            this.networkListener = new NetworkListener(this);
        }
        networkListener.start();

        //Listening for direct connections.
        if(this.directListener == null) {
            this.directListener = new DirectListener();
        }
        directListener.start();
    }

    /**
     * Stops the server.
     */
    public void stop() {
        logger.info("Stopping the server...");

        //Stopping the listeners.
        networkListener.stop();
        directListener.stop();

        //Disconnecting the clients.
        for (int i = 0; i < router.connections.size(); i++) {
            Connection c = router.connections.get(i);
            if(c.server == this) {
                c.drop();
            }
            c = null;
        }

        //Disposing listeners.
        networkListener = null;
        directListener = null;
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        //Server events.
        EventManager.registerEvent(ServerStatusEvent.class);
        EventManager.registerEvent(ServerErrorEvent.class);
    }
}
