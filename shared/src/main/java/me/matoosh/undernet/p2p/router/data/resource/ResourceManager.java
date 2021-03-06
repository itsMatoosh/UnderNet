package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.ConnectionEstablishedEvent;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.event.resource.pull.ResourcePullReceivedEvent;
import me.matoosh.undernet.event.resource.pull.ResourcePullSentEvent;
import me.matoosh.undernet.event.resource.transfer.*;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.*;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnelSide;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;

/**
 * Manages network resources locally.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class ResourceManager extends Manager {
    /**
     * Cache of currently requested resources.
     */
    public ArrayList<ResourceTransferHandler> outboundHandlers = new ArrayList<>();
    /**
     * Cache of currently pushed resources.
     */
    public ArrayList<ResourceTransferHandler> inboundHandlers = new ArrayList<>();

    /**
     * The queued resources that need to be sent.
     */
    public ArrayList<Resource> queuedResources = new ArrayList<>();

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(ResourceManager.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public ResourceManager(Router router) {
        super(router);
    }

    /**
     * Publishes a resource on the network.
     * Updates the resource if already published and the node is owner.
     * This sends the resource to its closest node.
     * @param resource
     */
    public void publish(Resource resource) {
        //Making sure we're connected to the network.
        if(UnderNet.router.status != InterfaceStatus.STARTED) {
            logger.warn("Cannot publish a resource without connection to the network.");
            return;
        }

        //Setting the owner.
        resource.setOwner(Node.self.getIdentity().getNetworkId());

        //Calculating the network id.
        resource.calcNetworkId();

        //Log
        logger.info("Publishing resource: {}...", resource);

        //Sending the resource info message.
        MessageTunnel tunnel = router.messageTunnelManager.createTunnel(Node.self.getIdentity().getNetworkId(), resource.getNetworkID(), MessageTunnelSide.ORIGIN);
        startPush(resource, tunnel);
    }

    /**
     * Pulls a resource with the specified network id.
     * @param resourceID
     */
    public void pull(NetworkID resourceID) {
        //Making sure we're connected to the network.
        if(UnderNet.router.status != InterfaceStatus.STARTED) {
            logger.warn("Cannot pull a resource without connection to the network.");
            return;
        }

        logger.info("Pulling resource with id: {}...", resourceID);

        //Sending the resource pull content to the closest node.
        router.networkMessageManager.sendMessage(new ResourcePullMessage(), resourceID);
    }

    /**
     * Gets a resource with the specified id.
     * @param networkID
     * @return
     */
    public Resource getLocalResource(NetworkID networkID) {
        FileResource file = FileResource.getStoredFileResource(networkID, router);
        if(file != null) {
            return file;
        }
        return null;
    }

    /**
     * Starts a push of a resource.
     * @param resource
     */
    public void startPush(Resource resource) {
        //Creating push tunnel
        MessageTunnel tunnel = router.messageTunnelManager.createTunnel(Node.self.getIdentity().getNetworkId(), resource.getNetworkID(), MessageTunnelSide.ORIGIN);
        startPush(resource, tunnel);
    }

    /**
     * Starts a push of a resource.
     * @param resource
     * @param tunnel
     */
    public void startPush(Resource resource, MessageTunnel tunnel) {
        //Getting the transfer handler.
        ResourceTransferHandler transferHandler = resource.getTransferHandler(ResourceTransferType.OUTBOUND, tunnel, -1, this.router);
        logger.info("Outbound {} resource transfer, transfer id: {}", resource.getResourceType(), transferHandler.getTransferId());

        //Sending the resource info message.
        transferHandler.prepare();
        resource.sendInfo(tunnel, transferHandler.getTransferId());
    }

    /**
     * Handles a resource info message.
     * @param message
     */
    private void handleResourceInfo(ResourceInfoMessage message) {
        //Checks if the resource has already started being received.
        for (ResourceTransferHandler transferHandler :
                inboundHandlers) {
            if(transferHandler.getResource().getNetworkID().equals(message.getNetworkMessage().getDestination())) {
                logger.warn("Received a duplicate resource info for: {}", message.getNetworkMessage().getDestination());
                return;
            }
        }

        Resource resource = null;

        //Creating appropriate resources.
        if(message.getResourceInfo().resourceType == ResourceType.FILE) {
            resource = new FileResource(this.router, new File(UnderNet.fileManager.getContentFolder() + "/" + message.getResourceInfo().attributes.get(1)));
        }

        resource.attributes = message.getResourceInfo().attributes;
        resource.setNetworkID(message.getNetworkMessage().getDestination());

        //Getting a new resource handler.
        ResourceTransferHandler transferHandler = resource.getTransferHandler(ResourceTransferType.INBOUND, message.getNetworkMessage().getTunnel(), message.getTransferId(), this.router);
        logger.info("Inbound {} resource transfer, transfer id: {}", message.getResourceInfo().resourceType, transferHandler.getTransferId());

        //Preparing for incoming resource.
        transferHandler.prepare();
    }

    /**
     * Handles resource pull requests.
     * @param message
     */
    private void handleResourcePullRequest(ResourcePullMessage message) {
        //Call event.
        EventManager.callEvent(new ResourcePullReceivedEvent(message));

        //Getting the requested resource.
        Resource requestedResource = getLocalResource(message.getNetworkMessage().getDestination());

        //Retrieving the file.
        if(requestedResource != null && requestedResource.isLocal()) {
            startPush(requestedResource, message.getNetworkMessage().getTunnel());
        } else {
            logger.warn("Resource: {} not available on {}. The pull request will be dropped!", message.getNetworkMessage().getDestination(), Node.self);

            //Sending a pull error message.
            message.getNetworkMessage().getTunnel().sendMessage(new ResourcePullNotFoundMessage());
        }
    }

    /**
     * Handles a received transfer control message.
     * @param message
     */
    private void handleResourceTransferControlMessage(ResourceTransferControlMessage message) {
        //Sending next chunk from handler.
        for (ResourceTransferHandler transferHandler :
                outboundHandlers) {
            if(transferHandler.getTransferId() == message.getTransferId()) {
                transferHandler.onTransferControlMessage(message.getControlId());
                return;
            }
        }

    }

    /**
     * Handles resource retrieval.
     * @param message
     */
    private void handleResourceRetrieve(ResourceDataMessage message) {
        logger.info("Handling resource data retrieve, transId: {}", message.getTransferId());
        //Checking if the resource push is already being received.
        for (ResourceTransferHandler transferHandler :
                inboundHandlers) {
            if(message.getTransferId() == transferHandler.getTransferId()) {
                transferHandler.receiveData(message);
                return;
            }
        }
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e instanceof MessageReceivedEvent) {
            //Network message received.
            final MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) e;

            //Resource message type.
            if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.RES_INFO) {
                //Resource info.
                handleResourceInfo((ResourceInfoMessage) messageReceivedEvent.networkMessage.getContent());
            }
            else if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.RES_DATA) {
                //Retrieve
                handleResourceRetrieve((ResourceDataMessage)messageReceivedEvent.networkMessage.getContent());
            }
            else if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.RES_TRANSFER_CONTROL) {
                //Request data
                handleResourceTransferControlMessage((ResourceTransferControlMessage) messageReceivedEvent.networkMessage.getContent());
            }else if(messageReceivedEvent.networkMessage.getContent().getType() == MsgType.RES_PULL) {
                //Resource pull
                handleResourcePullRequest((ResourcePullMessage)messageReceivedEvent.networkMessage.getContent());
            }
        } else if(e instanceof ConnectionEstablishedEvent) { //Redistribute the currently available resources when a new node connects.
            ConnectionEstablishedEvent connectionEvent = (ConnectionEstablishedEvent) e;

            if(connectionEvent.other == null) return;
            if(connectionEvent.other == Node.self) return;
            if(connectionEvent.other.getAddress().equals(Node.self.getAddress())) return;

            //Push each available resource.
            for (FileResource file :
                    FileResource.getStoredFileResources(router)) {
                if(router.neighborNodesManager.getClosestTo(file.getNetworkID()) != Node.self) {
                    boolean duplicate = false;
                    for (Resource r :
                            queuedResources) {
                        if (r == file) duplicate = true;
                    }
                    if(duplicate) continue;

                    queuedResources.add(file);
                }
            }
        }
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {
        EventManager.registerEvent(ResourcePullReceivedEvent.class);
        EventManager.registerEvent(ResourcePullSentEvent.class);
        EventManager.registerEvent(ResourceTransferStartedEvent.class);
        EventManager.registerEvent(ResourceTransferFinishedEvent.class);
        EventManager.registerEvent(ResourceTransferErrorEvent.class);
        EventManager.registerEvent(ResourceTransferDataSentEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, MessageReceivedEvent.class);
        EventManager.registerHandler(this, ConnectionEstablishedEvent.class);
    }
}