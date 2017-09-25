package me.matoosh.undernet.p2p.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import io.netty.channel.Channel;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelClosedEvent;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.node.NeighborNodesManager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.client.ClientChannelHandler;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;
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
     * The current status of the router.
     */
    public InterfaceStatus status = InterfaceStatus.STOPPED;
    /**
     * The network database.
     */
    public NetworkDatabase netDb;
    /**
     * The neighbor nodes manager.
     */
    public NeighborNodesManager neighborNodesManager;
    /**
     * The resource manager.
     */
    public ResourceManager resourceManager;

    /**
     * The number of reconnect attempts, the router attempted.
     */
    private int reconnectNum = 0;

    /**
     * Nodes the router is connected to at the moment.
     */
    public ArrayList<Node> connectedNodes = new ArrayList<>();

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

        //Setting up the network database.
        netDb = new NetworkDatabase();
        netDb.setup();

        //Instantiating the neighbor nodes manager.
        neighborNodesManager = new NeighborNodesManager();
        neighborNodesManager.setup();

        //Instantiating the resource manager.
        resourceManager = new ResourceManager();
        resourceManager.setup();
    }
    /**
     * Starts the router.
     * Starts the server listening process and establishes client connections.
     */
    public void start(NetworkIdentity networkIdentity) {
        //Checking whether the router is already running.
        if(status != InterfaceStatus.STOPPED) {
            logger.warn("Can't start, because the router is already running!");
            return;
        }

        //Caching the network identity.
        Node.self.networkIdentity = networkIdentity;

        //Checking whether the setup needs to be ran.
        if(server == null || client == null) {
            setup();
        }

        //Setting the status to starting.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STARTING));

        //Starting the server. Using a separate thread for blocking api.
        new Thread(new Runnable() {
            public void run() {
                server.start();
            }
        }).start();

        //Starting the client. Using a separate thread for blocking api.
        new Thread(new Runnable() {
            public void run() {
                client.start();
            }
        }).start();
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

        //Setting the status to stopping.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STOPPING));

        //Stops the client.
        if(client != null) {
            client.stop();
        }
        //Stops the server.
        if(server != null) {
            server.stop();
        }
    }

    /**
     * Connects directly to a node.
     * @param node
     */
    public void connectNode(Node node) {
        client.connect(node);
    }

    /**
     * Disconnects from a node.
     * @param node
     */
    public void disconnectNode(Node node) {
        for (Channel channel:
             client.channels) {
            Node channelNode = channel.attr(ClientChannelHandler.ATTRIBUTE_KEY_SERVER_NODE).get();
            if(channelNode != null && channelNode == node) {
                channel.close();
            }
        }
    }

    /**
     * Registers the router events.
     */
    private void registerEvents() {
        //Router events
        EventManager.registerEvent(RouterStatusEvent.class);
        EventManager.registerEvent(RouterErrorEvent.class);

        //Connection events
        EventManager.registerEvent(ChannelClosedEvent.class);
        EventManager.registerEvent(ChannelErrorEvent.class);
        EventManager.registerEvent(ChannelCreatedEvent.class);

        //Message events
        EventManager.registerEvent(ChannelMessageReceivedEvent.class);
    }

    /**
     * Registers the router handlers.
     */
    private void registerHandlers() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, RouterErrorEvent.class);
        EventManager.registerHandler(this, ChannelCreatedEvent.class);
        EventManager.registerHandler(this, ChannelClosedEvent.class);
        EventManager.registerHandler(this, ChannelErrorEvent.class);
        EventManager.registerHandler(this, ClientStatusEvent.class);
        EventManager.registerHandler(this, ServerStatusEvent.class);
    }

    //EVENTS
    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        //Connection established.
        if(e.getClass() == ChannelCreatedEvent.class) {
            ChannelCreatedEvent establishedEvent = (ChannelCreatedEvent)e;
            logger.debug("New connection established with: " + establishedEvent.other);
        }
        //Connection dropped.
        else if(e.getClass() == ChannelClosedEvent.class) {
            ChannelClosedEvent droppedEvent = (ChannelClosedEvent)e;
            //logger.debug("Connection with: " + droppedEvent.other + " dropped");
        }
        //Connection error.
        else if(e.getClass() == ChannelErrorEvent.class) {
            ChannelErrorEvent errorEvent = (ChannelErrorEvent)e;
            //logger.warn("There was an error with the connection: " + errorEvent.connection.id);
            //TODO: Handle the error.
        } else if(e.getClass() == RouterStatusEvent.class) {
            RouterStatusEvent statusEvent = (RouterStatusEvent)e;
            switch(statusEvent.newStatus) {
                case STOPPED:
                    onConnectionEnded();
                    break;
                case STARTING:
                    break;
                case STARTED:
                    break;
                case STOPPING:
                    break;
            }
            //TODO: Handle the status change.
        } else if(e.getClass() == RouterErrorEvent.class) {
            onRouterError((RouterErrorEvent) e);
        } else if(e.getClass() == ServerStatusEvent.class) {
            ServerStatusEvent statusEvent = (ServerStatusEvent)e;
            if(statusEvent.newStatus.equals(InterfaceStatus.STARTED)) {
                //In this case client doesn't yet have to be started.
                onRouterStarted();
            } else if(statusEvent.newStatus.equals(InterfaceStatus.STOPPED)) {
                if(client.status.equals(InterfaceStatus.STOPPED)) {
                    //Both parts of the router stopped successfully.
                    onRouterStopped();
                }
            }
        } else if(e.getClass() == ClientStatusEvent.class) {
            ClientStatusEvent statusEvent = (ClientStatusEvent) e;
            if(statusEvent.newStatus.equals(InterfaceStatus.STARTED)) {
                if(server.status.equals(InterfaceStatus.STARTED)) {
                    //Both parts of the router started succesfully.
                    onRouterStarted();
                }
            } else if(statusEvent.newStatus.equals(InterfaceStatus.STOPPED)) {
                if(server.status.equals(InterfaceStatus.STOPPED)) {
                    //Both parts of the router stopped successfully.
                    onRouterStopped();
                }
            }
        }
    }
    /**
     * Called when a router error occurs.
     * This means we can't continue and have to restart the connection.
     * @param e
     */
    private void onRouterError(RouterErrorEvent e) {
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

    /**
     * Called when the router starts.
     */
    private void onRouterStarted() {
        //Setting the status to started.
        EventManager.callEvent(new RouterStatusEvent(this,  InterfaceStatus.STARTED));
    }
    /**
     * Called when the router stops.
     */
    private void onRouterStopped() {
        //Setting the status to stopped.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STOPPED));

        //GC
        System.runFinalization();
        System.gc();
    }
    /**
     * Called when the connection to the network ends.
     */
    private void onConnectionEnded() {
        //Resetting the reconn num.
        reconnectNum = 0;
    }
}
