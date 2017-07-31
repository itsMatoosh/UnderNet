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
        this.networkListener = new NetworkListener(this);
        this.directListener = new DirectListener();
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

        //Disconnecting the clients.
        for (Connection c:
             router.connections) {
            if(c.server == this) {
                c.drop();
            }
        }
    }

    /**
     * Registers the server events.
     */
    private void registerEvents() {
        EventManager.registerEvent(ServerStatusEvent.class);
        EventManager.registerEvent(ServerErrorEvent.class);
    }
}
