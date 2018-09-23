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
     * The currently active flag resources.
     */
    public ArrayList<FlagResource> flagResources = new ArrayList<>();

    /**
     * Cache of currently requested resources.
     */
    public ArrayList<ResourceTransferHandler> outboundHandlers = new ArrayList<>();
    /**
     * Cache of currently pushed resources.
     */
    public ArrayList<ResourceTransferHandler> inboundHandlers = new ArrayList<>();

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
        startPush(resource, NetworkMessage.MessageDirection.TO_DESTINATION, resource.getNetworkID());
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
        FileResource file = getStoredFileResource(networkID);
        if(file != null) {
            return file;
        }

        for (FlagResource flag :
                flagResources) {
            if (flag.getNetworkID().equals(networkID)) {
                return flag;
            }
        }

        return null;
    }

    /**
     * Gets a stored file resource.
     * @param resource
     * @return
     */
    public FileResource getStoredFileResource(NetworkID resource) {
        for (File file :
                UnderNet.fileManager.getContentFolder().listFiles()) {
            if(file.isHidden()) continue;
            if(!file.canRead()) continue;
            FileResource res = new FileResource(router, file);
            res.calcNetworkId();
            if(res.getNetworkID().equals(resource)) {
                return res;
            }
        }
        return null;
    }
    /**
     * Gets the file resources stored in the content folder.
     * @return
     */
    public ArrayList<FileResource> getStoredFileResources() {
        ArrayList<FileResource> resources = new ArrayList<>();
        for (File file :
                UnderNet.fileManager.getContentFolder().listFiles()) {
            if(file.isHidden()) continue;
            if(!file.canRead()) continue;
            FileResource res = new FileResource(router, file);
            res.calcNetworkId();
            resources.add(res);
        }
        return resources;
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
            if(messageReceivedEvent.networkMessage.content.getType() == MsgType.RES_INFO) {
                //Resource info.
                handleResourceInfo((ResourceInfoMessage) messageReceivedEvent.networkMessage.content);
            }
            else if(messageReceivedEvent.networkMessage.content.getType() == MsgType.RES_DATA) {
                if(messageReceivedEvent.networkMessage.getDirection() == NetworkMessage.MessageDirection.TO_DESTINATION) {
                    //Push
                    handleResourcePush((ResourceDataMessage)messageReceivedEvent.networkMessage.content);
                } else {
                    //Retrieve
                    handleResourceRetrieve((ResourceDataMessage)messageReceivedEvent.networkMessage.content);
                }
            } else if(messageReceivedEvent.networkMessage.content.getType() == MsgType.RES_PULL) {
                //Resource pull
                handleResourcePullRequest((ResourcePullMessage)messageReceivedEvent.networkMessage.content);
            }
        } else if(e instanceof ConnectionEstablishedEvent) { //Redistribute the currently available resources when a new node connects.
            ConnectionEstablishedEvent connectionEvent = (ConnectionEstablishedEvent) e;

            if(connectionEvent.other == null) return;
            if(connectionEvent.other == Node.self) return;
            if(connectionEvent.other.address.equals(Node.self.address)) return;

            //Push each available resource.
            for (FileResource file :
                    getStoredFileResources()) {
                if(router.neighborNodesManager.getClosestTo(file.getNetworkID()) != Node.self) {
                    startPush(file, NetworkMessage.MessageDirection.TO_DESTINATION, file.getNetworkID());
                }
            }
            for (FlagResource flag :
                    flagResources) {
                if(router.neighborNodesManager.getClosestTo(flag.getNetworkID()) != Node.self) {
                    startPush(flag, NetworkMessage.MessageDirection.TO_DESTINATION, flag.getNetworkID());
                }
            }
        } else if(e instanceof ResourceTransferFinishedEvent) {
            ResourceTransferFinishedEvent transferFinishedEvent = (ResourceTransferFinishedEvent)e;

            //Closing the streams.
            transferFinishedEvent.transferHandler.close();

            //Removing from the list.
            if(transferFinishedEvent.transferHandler.transferType == ResourceTransferType.INBOUND) {
                inboundHandlers.remove(transferFinishedEvent.transferHandler);
            } else {
                outboundHandlers.remove(transferFinishedEvent.transferHandler);
            }
        }
    }

    /**
     * Starts a push of a resource.
     * @param resource
     * @param direction
     * @param destination
     */
    private void startPush(Resource resource, NetworkMessage.MessageDirection direction, NetworkID destination) {
        //Getting the transfer handler.
        ResourceTransferHandler transferHandler = resource.getTransferHandler(ResourceTransferType.OUTBOUND, direction, destination, this.router);

        //Sending the resource info message.
        resource.sendInfo(destination, direction, transferHandler.transferId);

        //Sending the resource data.
        transferHandler.startSending();
        outboundHandlers.add(transferHandler);
    }

    /**
     * Returns path to a local resource.
     * @param resource
     * @return
     */
    private File getLocalResourceFile(NetworkID resource) {
        return new File(UnderNet.fileManager.getContentFolder() + "/" + resource.getStringValue());
    }

    /**
     * Handles a resource info message.
     * @param message
     */
    private void handleResourceInfo(ResourceInfoMessage message) {
        logger.info("Inbound {} resource transfer, preparing to receive...", message.resourceInfo.resourceType);

        //Checks if the resource has already started being received.
        for (ResourceTransferHandler transferHandler :
                inboundHandlers) {
            if(transferHandler.resource.getNetworkID().equals(message.networkMessage.getDestination())) {
                logger.warn("Received a duplicate resource info for: {}", message.networkMessage.getDestination());
                return;
            }
        }

        Resource resource = null;

        //Creating appropriate resources.
        if(message.resourceInfo.resourceType == ResourceType.FILE) {
            resource = new FileResource(this.router, new File(UnderNet.fileManager.getContentFolder() + "/" + message.resourceInfo.attributes.get(1)));
        }

        resource.attributes = message.resourceInfo.attributes;
        resource.calcNetworkId();
        inboundHandlers.add(resource.getTransferHandler(ResourceTransferType.INBOUND, NetworkMessage.MessageDirection.TO_DESTINATION, message.networkMessage.getOrigin(), this.router));
    }
    /**
     * Handles a resource push.
     * @param message
     */
    private void handleResourcePush(ResourceDataMessage message) {
        //Checking if the resource push is already being received.
        for (ResourceTransferHandler transferHandler :
                inboundHandlers) {
            if(transferHandler.resource.getNetworkID().equals(message.networkMessage.getDestination()) && transferHandler.transferId == message.transferId) {
                transferHandler.onResourceMessage(message);
            }
            return;
        }
    }

    /**
     * Handles resource retrieval.
     * @param message
     */
    private void handleResourceRetrieve(ResourceDataMessage message) {
        //Checking if the resource push is already being received.
        for (ResourceTransferHandler transferHandler :
                inboundHandlers) {
            if(transferHandler.resource.getNetworkID().equals(message.networkMessage.getDestination()) && message.transferId == transferHandler.transferId) {
                transferHandler.onResourceMessage(message);
            }
            return;
        }
    }

    /**
     * Handles resource pull requests.
     * @param message
     */
    private void handleResourcePullRequest(ResourcePullMessage message) {
        //Call event.
        EventManager.callEvent(new ResourcePullReceivedEvent(message));

        //Getting the requested resource.
        Resource requestedResource = getLocalResource(message.networkMessage.getDestination());

        //Retrieving the file.
        if(requestedResource != null && requestedResource.isLocal()) {
            startPush(requestedResource, NetworkMessage.MessageDirection.TO_ORIGIN, message.networkMessage.getOrigin());
        } else {
            logger.warn("Resource: {} not available on {}. The pull request will be dropped!", message.networkMessage.getDestination(), Node.self);
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
        EventManager.registerEvent(ResourceTransferDataReceivedEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, MessageReceivedEvent.class);
        EventManager.registerHandler(this, ConnectionEstablishedEvent.class);
        EventManager.registerHandler(this, ResourceTransferFinishedEvent.class);
    }
}
