package me.matoosh.undernet.p2p.router.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.client.ClientErrorEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.p2p.cache.NodeCache;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.connection.Connection;
import me.matoosh.undernet.p2p.router.connection.NetworkConnection;

/**
 * Client part of the router.
 *
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Client {
    /**
     * The router.
     */
    public Router router;
    /**
     * The current status of the client.
     */
    public InterfaceStatus status;

    /**
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Client.class);

    public Client(Router router) {
        this.router = router;
    }

    /**
     * Sets up the client.
     */
    public void setup() {
        //Registering the client events.
        registerEvents();
    }
    /**
     * Starts the client and connects to cached nodes based on the settings.
     */
    public void start() {
        //Attempting to connect to each of the 5 most reliable nodes.
        ArrayList<Node> nodesToConnectTo = NodeCache.getMostReliable(5, null);
        if(nodesToConnectTo == null) {
            EventManager.callEvent(new ClientErrorEvent(this, new ClientNoNodesCachedException(this)));
        } else {
            for(Node node : nodesToConnectTo) {
                connect(node);
            }
        }
    }
    /**
     * Connects the client to a node.
     */
    public void connect(Node node) {
        logger.info("Connecting to node: " + node.address);

       /* EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                ConnectionErrorEvent errorEvent = (ConnectionErrorEvent)e;
                if(errorEvent.exception.getClass() == ConnectionNotAvailableException.class && errorEvent.connection.side == ConnectionSide.CLIENT) {
                    //Establishing the network connection.
                    new NetworkConnection().establish(Client.this, errorEvent.connection.other);
                }
            }
        }, ConnectionErrorEvent.class);*/
        //Establishing a new connection. If this connection fails, another way of connecting will be used.
        //TODO: Make smarter connection choices to reduce connection time.
        //new DirectConnection().establish(this, node);

        //Establishing the network connection.
        new NetworkConnection().establish(Client.this, node);
    }

    /**
     * Stops the client.
     */
    public void stop() {
        //Disconnecting all.
        disconnectAll();
    }
    /**
     * Disconnects from all nodes.
     */
    public void disconnectAll() {
        //Dropping all the serverConnections.
        for (Connection c:
             router.connections) {
            if(c.client == this) {
                c.drop();
            }
        }
    }

    /**
     * Registers the client handlers.
     */
    private void registerEvents() {
        EventManager.registerEvent(ClientStatusEvent.class);
        EventManager.registerEvent(ClientErrorEvent.class);
    }
}
