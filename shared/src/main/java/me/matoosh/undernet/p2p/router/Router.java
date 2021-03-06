package me.matoosh.undernet.p2p.router;

import io.netty.channel.Channel;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ChannelClosedEvent;
import me.matoosh.undernet.event.channel.ChannelCreatedEvent;
import me.matoosh.undernet.event.channel.ChannelErrorEvent;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.client.ClientStatusEvent;
import me.matoosh.undernet.event.router.RouterControlLoopEvent;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;
import me.matoosh.undernet.event.server.ServerStatusEvent;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.node.NeighborNodesManager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.client.ClientNetworkMessageHandler;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessageManager;
import me.matoosh.undernet.p2p.router.data.message.NodeNeighborsRequest;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnelManager;
import me.matoosh.undernet.p2p.router.data.message.tunnel.TunnelControlMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.server.Server;
import me.matoosh.undernet.p2p.shine.client.ShineMediatorClient;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.security.Security;
import java.util.ArrayList;
import java.util.concurrent.*;

/**
 * The network router.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class Router extends EventHandler {
    /**
     * The interval of the control loop.
     */
    public static final int controlLoopInterval = 15;
    /**
     * The logger.
     */
    public static Logger logger = LoggerFactory.getLogger(Router.class);
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
     * The timer used for periodically running the control function.
     */
    public ScheduledExecutorService timer;
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
     * The network message manager.
     */
    public NetworkMessageManager networkMessageManager;
    /**
     * The message tunnel manager.
     */
    public MessageTunnelManager messageTunnelManager;
    private ScheduledFuture<?> timerHandle;
    /**
     * The number of reconnect attempts, the router attempted.
     */
    private int reconnectNum = 0;
    /**
     * Nodes the router is connected to at the moment.
     */
    private ArrayList<Node> connectedNodes;

    /**
     * Sets up the router.
     */
    public void setup() {
        //Checking if the router is not running.
        if (status != InterfaceStatus.STOPPED) {
            logger.warn("Can't setup the router, while it's running!");
            return;
        }

        //Creating the connected nodes list.
        this.connectedNodes = new ArrayList<>();

        //Setting this as the currently used router.
        Node.self.router = this;

        //Registering events.
        registerEvents();

        //Registering handlers.
        registerHandlers();

        //Setting up security policies.
        setupSecurity();

        //Creating server.
        server = new Server(this);
        server.setup();

        //Creating client.
        client = new Client(this);
        client.setup();

        //Creating a scheduled executor.
        timer = Executors.newSingleThreadScheduledExecutor();

        //Setting up the network database.
        netDb = new NetworkDatabase(this);
        netDb.setup();

        //Instantiating the neighbor nodes manager.
        neighborNodesManager = new NeighborNodesManager(this);
        neighborNodesManager.setup();

        //Instantiating the resource manager.
        resourceManager = new ResourceManager(this);
        resourceManager.setup();

        //Instantiating the network message manager.
        networkMessageManager = new NetworkMessageManager(this);
        networkMessageManager.setup();

        //Instantiating the message tunnel manager.
        messageTunnelManager = new MessageTunnelManager(this);
        messageTunnelManager.setup();

        //Setting up SHINE
        if(UnderNet.networkConfig.useShine()) {
            int newID = 0;
            while (newID == 0) {
                newID = UnderNet.secureRandom.nextInt();
            }
            ShineMediatorClient.setShineId(newID);
        }
    }

    /**
     * Sets up security policies.
     */
    private void setupSecurity() {
        Security.setProperty("crypto.policy", "unlimited");
        Security.addProvider(new BouncyCastleProvider());
        Security.removeProvider("SunJSSE");
    }

    /**
     * Starts the router.
     * Starts the server listening process and establishes client connections.
     *
     * @param networkIdentity The identity with which to join the network.
     */
    public void start(NetworkIdentity networkIdentity) {
        //Checking whether the router is already running.
        if (status != InterfaceStatus.STOPPED) {
            logger.warn("Can't start, because the router is already running!");
            return;
        }

        //Clearing the connected nodes list.
        if(this.connectedNodes == null) {
            this.connectedNodes = new ArrayList<>();
        } else {
            this.connectedNodes.clear();
        }

        //Caching the network identity.
        Node.self.setIdentity(networkIdentity);

        //Checking whether the setup needs to be ran.
        if (server == null || client == null) {
            setup();
        }

        //Setting the status to starting.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STARTING));

        //Starting the client. Using a separate thread for blocking api.
        new Thread(() -> client.start()).start();

        //Starting the server. Using a separate thread for blocking api.
        new Thread(() -> server.start()).start();
    }

    /**
     * Control loop running every 15 seconds.
     */
    private void controlLoop() {
        logger.info("Checking if everything is running smoothly...");

        //Checking if enough nodes are connected.
        if (getConnectedNodes().size() < UnderNet.networkConfig.optNeighbors()) {
            //Request more neighbors from neighbors.
            if(getConnectedNodes().size() > 0) {
                int id = UnderNet.secureRandom.nextInt(getConnectedNodes().size());
                Node neighbor = getConnectedNodes().get(id);
                this.networkMessageManager.sendMessage(new NodeNeighborsRequest(), neighbor.getIdentity().getNetworkId());
            }

            //Request neighbors from SHINE
            if(UnderNet.networkConfig.useShine()) {
                ArrayList<Integer> ignoreAddresses = new ArrayList<>();
                for (int i = 0; i < connectedNodes.size(); i++) {
                    Node n = connectedNodes.get(i);
                    if(n.getShineId() != 0) ignoreAddresses.add(n.getShineId());
                }

                ShineMediatorClient.start(UnderNet.networkConfig.shineAddress(), UnderNet.networkConfig.shinePort(), new ShineMediatorClient.IMediatorClientConnectionInfoReceivedListner() {
                    @Override
                    public void onConnectionInfoReceived(InetSocketAddress socketAddress, int localPort, int shineId) {
                        if(localPort == 0) {
                            logger.warn("Local SHINE port unknown, can't initiate connection!");
                            return;
                        }
                        if(UnderNet.router != null && UnderNet.router.status == InterfaceStatus.STARTED) {
                            UnderNet.router.client.shineConnect(socketAddress.getAddress().getHostAddress(), socketAddress.getPort(), shineId, localPort);
                        } else {
                            logger.warn("UnderNet router must be running to complete a SHINE connection!");
                        }
                    }
                }, ignoreAddresses.toArray(new Integer[0]));
            }
        }

        //Checking resource transfer activity.
        for (int i = 0; i < resourceManager.inboundHandlers.size(); i++) {
            ResourceTransferHandler transferHandler = resourceManager.inboundHandlers.get(i);
            if (System.currentTimeMillis() > transferHandler.getLastMessageTime() + 2 * controlLoopInterval * 1000)
                transferHandler.callError(new TimeoutException());
        }
        for (int i = 0; i < resourceManager.outboundHandlers.size(); i++) {
            ResourceTransferHandler transferHandler = resourceManager.outboundHandlers.get(i);
            if (System.currentTimeMillis() > transferHandler.getLastMessageTime() + 2 * controlLoopInterval * 1000)
                transferHandler.callError(new TimeoutException());
        }

        //Checking tunnel activity.
        for (int i = 0; i < messageTunnelManager.messageTunnels.size(); i++) {
            MessageTunnel tunnel = messageTunnelManager.messageTunnels.get(i);

            if (System.currentTimeMillis() > tunnel.getLastMessageTime() + 2 * controlLoopInterval * 1000)
                messageTunnelManager.closeTunnel(tunnel);
            else if (tunnel.isKeepAlive()) tunnel.sendMessage(new TunnelControlMessage());
        }

        //Sending pending transfers.
        if(resourceManager.outboundHandlers.size() == 0) {
            for (int i = 0; i < resourceManager.queuedResources.size(); i++) {
                Resource r = resourceManager.queuedResources.get(i);
                if (neighborNodesManager.getClosestTo(r.getNetworkID()) != Node.self) {
                    resourceManager.queuedResources.remove(i);
                    resourceManager.startPush(r);
                    break;
                } else {
                    resourceManager.queuedResources.remove(i);
                }
            }
        }

        EventManager.callEvent(new RouterControlLoopEvent(this));
    }

    /**
     * Stops the router.
     */
    public void stop() {
        //Checking if the server is running.
        if (status == InterfaceStatus.STOPPED) {
            logger.warn("Can't stop the router, as it is not running!");
            return;
        }

        //Setting the status to stopping.
        EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STOPPING));

        //Stops the control loop.
        if(timerHandle != null)
            timerHandle.cancel(true);

        //Stops the client.
        if (client != null) {
            client.stop();
        }
        //Stops the server.
        if (server != null) {
            server.stop();
        }

        //Closes all remaining transfers.
        for (int i = 0; i < resourceManager.inboundHandlers.size(); i++) {
            resourceManager.inboundHandlers.get(i).close();
        }
        for (int i = 0; i < resourceManager.outboundHandlers.size(); i++) {
            resourceManager.outboundHandlers.get(i).close();
        }

        //Closes all remaining tunnels.
        for (int i = 0; i < messageTunnelManager.messageTunnels.size(); i++) {
            messageTunnelManager.closeTunnel(messageTunnelManager.messageTunnels.get(i));
        }

        //Closes the shine client.
        ShineMediatorClient.stop();

        //Clearing the connected nodes list.
        this.connectedNodes.clear();
    }

    /**
     * Connects directly to a node.
     *
     * @param node
     */
    public void connectNode(Node node) {
        client.connect(node.getAddress().getAddress().getHostAddress(), node.getAddress().getPort());
    }

    /**
     * Disconnects from a node.
     *
     * @param node
     */
    public void disconnectNode(Node node) {
        for (Channel channel :
                client.channels) {
            Node channelNode = channel.attr(ClientNetworkMessageHandler.ATTRIBUTE_KEY_SERVER_NODE).get();
            if (channelNode != null && channelNode == node) {
                channel.close();
            }
        }
    }

    /**
     * Gets all of the connected nodes.
     *
     * @return
     */
    public ArrayList<Node> getConnectedNodes() {
        return (ArrayList<Node>) connectedNodes.clone();
    }

    /**
     * Adds a connected node.
     */
    public void addConnectedNode(Node n) {
        logger.info("Adding {} to the connected nodes...", n);
        connectedNodes.add(n);
    }

    /**
     * Removes a connected node.
     * @param n
     */
    public void removeConnectedNode(Node n) {
        logger.info("Removing {} from the connected nodes...");
        connectedNodes.remove(n);
    }

    /**
     * Registers the router events.
     */
    private void registerEvents() {
        //Router events
        EventManager.registerEvent(RouterStatusEvent.class);
        EventManager.registerEvent(RouterControlLoopEvent.class);
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
        if (e.getClass() == RouterStatusEvent.class) {
            RouterStatusEvent statusEvent = (RouterStatusEvent) e;
            switch (statusEvent.newStatus) {
                case STOPPED:
                    onConnectionEnded();
                    break;
                case STARTING:
                    break;
                case STARTED:
                    //Starting the control loop.
                    timerHandle = timer.scheduleAtFixedRate(() -> controlLoop(), 5, controlLoopInterval, TimeUnit.SECONDS);
                    break;
                case STOPPING:
                    break;
            }
        } else if (e.getClass() == RouterErrorEvent.class) {
            onRouterError((RouterErrorEvent) e);
        } else if (e.getClass() == ServerStatusEvent.class || e.getClass() == ClientStatusEvent.class) {
            if (server.status == client.status) {
                //In this case client doesn't yet have to be started.
                if (server.status.equals(InterfaceStatus.STARTED)) {
                    //Both parts of the router started successfully.
                    onRouterStarted();
                } else if(server.status.equals(InterfaceStatus.STOPPED)) {
                    onRouterStopped();
                }
            }
        }
    }

    /**
     * Called when a router error occurs.
     * This means we can't continue and have to restart the connection.
     *
     * @param e
     */
    private void onRouterError(RouterErrorEvent e) {
        //Printing the error.
        logger.error("There was a problem with the UnderNet router!", e);
        //Resetting the network devices.
        e.router.stop();

        //Reconnecting if possible.
        if (e.shouldReconnect) {
            reconnectNum++;
            //Checking if we should reconnect.
            if (reconnectNum > UnderNet.networkConfig.maxReconnectCount()) {
                logger.error("Exceeded the maximum number of reconnect attempts!");
                onConnectionEnded();
            }

            logger.info("Attempting to reconnect for: {} time...", reconnectNum);
            this.start(Node.self.getIdentity());
        }
    }

    /**
     * Called when the router starts.
     */
    private void onRouterStarted() {
        //Setting the status to started.
        if (status != InterfaceStatus.STARTED) {
            EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STARTED));
        }
    }

    /**
     * Called when the router stops.
     */
    private void onRouterStopped() {
        //Setting the status to stopped.
        if (status != InterfaceStatus.STOPPED) {
            EventManager.callEvent(new RouterStatusEvent(this, InterfaceStatus.STOPPED));

            //GC
            System.runFinalization();
            System.gc();
        }
    }

    /**
     * Called when the connection to the network ends.
     */
    private void onConnectionEnded() {
        //Resetting the reconn num.
        reconnectNum = 0;
    }
}
