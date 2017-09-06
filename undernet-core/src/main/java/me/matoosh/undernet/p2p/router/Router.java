package me.matoosh.undernet.p2p.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.connection.ConnectionDroppedEvent;
import me.matoosh.undernet.event.connection.ConnectionErrorEvent;
import me.matoosh.undernet.event.connection.ConnectionEstablishedEvent;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * The network router.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class Router extends EventHandler {
    /**
     * The client of this router.
     * Used for establishing connections with other nodes.
     */
    public Client client;
    /**
     * The server of this router.
     * Used for receiving connections from other nodes.
     */
    public Server server;
    /**
     * Message queue of the router.
     */
    public MessageQueue messageQueue;
    /**
     * The current status of the router.
     */
    public InterfaceStatus status = InterfaceStatus.STOPPED;

    /**
     * List of the currently active connections.
     */
    public ArrayList<Connection> connections = new ArrayList<Connection>();

    /**
     * The number of reconnect attempts, the router attempted.
     */
    private int reconnectNum = 0;

    /**
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Router.class);

    /**
     * Sets up the router.
     */
    public void setup() {
        //Checking if the router is not running.
        if(status != InterfaceStatus.STOPPED) {
            logger.warn("Can't setup the router, while it's running!");
            return;
        }

        //Setting this as the currently used router.
        Node.self.router = this;

        //Registering events.
        registerEvents();

        //Registering handlers.
        registerHandlers();

        //Creating server.
        server = new Server(this);
        server.setup();

        //Creating client.
        client = new Client(this);
        client.setup();
    }
    /**
     * Starts the router.
     * Starts the server listening process and establishes client connections.
     */
    public void start() {
        //Checking whether the router is already running.
        if(status != InterfaceStatus.STOPPED) {
            logger.warn("Can't start, because the router is already running!");
            return;
        }

        //Checking whether the setup needs to be ran.
        if(server == null || client == null) {
            setup();
        }

        //Setting the status to starting.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STARTING));

        //Starting the server.
        server.start();

        //Starting the client.
        client.start();
    }

    /**
     * Stops the router.
     */
    public void stop() {
        //Checking if the server is running.
        if(status == InterfaceStatus.STOPPED) {
            logger.debug("Can't stop the router, as it is not running!");
            return;
        }

        //Stops the server.
        server.stop();
        server = null;
        //Stops the client.
        client.stop();
        client = null;
    }

    /**
     * Registers the router events.
     */
    private void registerEvents() {
        EventManager.registerEvent(RouterStatusEvent.class);
        EventManager.registerEvent(ConnectionDroppedEvent.class);
        EventManager.registerEvent(ConnectionErrorEvent.class);
        EventManager.registerEvent(ConnectionEstablishedEvent.class);
    }

    /**
     * Registers the router handlers.
     */
    private void registerHandlers() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, RouterErrorEvent.class);
        EventManager.registerHandler(this, ConnectionEstablishedEvent.class);
        EventManager.registerHandler(this, ConnectionDroppedEvent.class);
        EventManager.registerHandler(this, ConnectionErrorEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        //Connection established.
        if(e.getClass() == ConnectionEstablishedEvent.class) {
            ConnectionEstablishedEvent establishedEvent = (ConnectionEstablishedEvent)e;
            logger.debug("New connection established with: " + establishedEvent.other);
            connections.add(establishedEvent.connection);
        }
        //Connection dropped.
        else if(e.getClass() == ConnectionDroppedEvent.class) {
            ConnectionDroppedEvent droppedEvent = (ConnectionDroppedEvent)e;
            logger.debug("Connection with: " + droppedEvent.other + " dropped");
            connections.remove(droppedEvent.connection);
        }
        //Connection error.
        else if(e.getClass() == ConnectionErrorEvent.class) {
            ConnectionErrorEvent errorEvent = (ConnectionErrorEvent)e;
            logger.warn("There was an error with the connection: " + errorEvent.connection.id);
            //TODO: Handle the error.
        } else if(e.getClass() == RouterStatusEvent.class) {
            RouterStatusEvent statusEvent = (RouterStatusEvent)e;
            switch(statusEvent.newStatus) {
                case STOPPED:
                    onConnectionEnded();
                    break;
                case STARTING:
                    break;
                case CONNECTING:
                    break;
                case STARTED:
                    break;
                case STOPPING:
                    break;
            }
            //TODO: Handle the status change.
        } else if(e.getClass() == RouterErrorEvent.class) {
            onRouterError((RouterErrorEvent) e);
        }
    }
    /**
     * Called when the connection to the network ends.
     */
    public void onConnectionEnded() {
        //Resetting the reconn num.
        reconnectNum = 0;
    }

    /**
     * Called when a router error occurs.
     * This means we can't continue and have to restart the connection.
     * @param e
     */
    public void onRouterError(RouterErrorEvent e) {
        //Printing the error.
        if(e.exception.getMessage() != null) {
            logger.error("There was a problem with the UnderNet router: " + e.exception.getMessage());
        }
        e.exception.printStackTrace();

        //Resetting the network devices.
        e.router.stop();

        //Reconnecting if possible.
        if(e.router.status != InterfaceStatus.STOPPED && e.shouldReconnect) {
            reconnectNum++;
            //Checking if we should reconnect.
            if(reconnectNum >= 5) {
                logger.error("Exceeded the maximum number of reconnect attempts!");
                onConnectionEnded();
            }

            logger.info("Attempting to reconnect for: " + reconnectNum + " time...");
        }
    }
}
