package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.resource.pull.ResourcePullFinalStopEvent;
import me.matoosh.undernet.event.resource.pull.ResourcePullReceivedEvent;
import me.matoosh.undernet.event.resource.pull.ResourcePullSentEvent;
import me.matoosh.undernet.event.resource.push.ResourcePushFinalStopEvent;
import me.matoosh.undernet.event.resource.push.ResourcePushReceivedEvent;
import me.matoosh.undernet.event.resource.push.ResourcePushSentEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages network resources locally.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class ResourceManager extends Manager {
    /**
     * List of the stored resources.
     */
    public ArrayList<Resource> resourcesStored;

    /**
     * Cache of pulled resource ids and the requesting network ids.
     */
    public HashMap<NetworkID, NetworkID> pullCache = new HashMap<>();

    /**
     * Executor used for managing resource logic.
     */
    public static ExecutorService executor = Executors.newSingleThreadExecutor();

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
     * This sends the resource to its closest node.
     * @param resource
     */
    public void publish(Resource resource) {
        //Making sure we're connected to the network.
        if(UnderNet.router.status != InterfaceStatus.STARTED) {
            logger.warn("Cannot publish a resource without connection to the network.");
            return;
        }

        //Creating a new ResourceMessage.
        final ResourceMessage pushMessage = new ResourceMessage(resource);
        pushMessage.sender = Node.self;

        //Log
        logger.info("Publishing resource: {}...", resource);

        //Sending the content to the neighbor closest to it.
        executor.submit(new Runnable() {
            @Override
            public void run() {
                pushFurther(pushMessage);
            }
        });
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

        //Creating a new ResourcePullMessageEvent.
        final ResourcePullMessage pullMessage = new ResourcePullMessage(resourceID);
        pullMessage.sender = Node.self;

        logger.info("Pulling resource with id: {}...", pullMessage.resourceId);

        //Sending the resource pull content to the closest node.
        executor.submit(new Runnable() {
            @Override
            public void run() {
                pullFurther(pullMessage);
            }
        });
    }

    /**
     * Forwards a pullMessage to the next appropriate node.
     * Calls resource stored event if this node is the resource's destination.
     * @param pushMessage
     */
    public void pushFurther(final ResourceMessage pushMessage) {
        if(!pushMessage.resource.networkID.isValid()) {
            logger.warn("Network id: {} is invalid, the requested resource won't be pulled!", pushMessage.resource.networkID);
            return;
        }
        if(pushMessage.sender == null) {
            logger.warn("Pull message sender null!");
            return;
        }

        //Getting the node closest to the resource.
        final Node closest = router.neighborNodesManager.getClosestTo(pushMessage.resource.networkID);
        if(closest == Node.self) {

            //This is the final node of the resource.
            EventManager.callEvent(new ResourcePushFinalStopEvent(pushMessage.resource, pushMessage, null));
        } else {
            logger.info("Pushing resource: {}, to node: {}", pushMessage.resource, closest);

            //Calling the onPushSend method.
            pushMessage.resource.send(closest, new Resource.IResourceActionListener() {
                @Override
                public void onFinished(Node other) {
                    //Sending the push msg.
                    closest.send(new NetworkMessage(MsgType.RES_PUSH, pushMessage));

                    //Calling event.
                    EventManager.callEvent(new ResourcePushSentEvent(pushMessage.resource, pushMessage, closest));
                }
            });
        }
    }

    /**
     * Passes the pull content of a specific resource to the next closest node.
     * @param pullMessage
     */
    public void pullFurther(ResourcePullMessage pullMessage) {
        if(!pullMessage.resourceId.isValid()) {
            logger.warn("Network id: {} is invalid, the requested resource won't be pulled!", pullMessage.resourceId);
            return;
        }
        if(pullMessage.sender == null) {
            logger.warn("Resource pull message sender null!");
            return;
        }

        //Getting the node closest to the resource.
        Node closest = router.neighborNodesManager.getClosestTo(pullMessage.resourceId);

        //Save path for this pull to send the resource back after successful pull.
        pullCache.put(pullMessage.resourceId, pullMessage.sender.getIdentity().getNetworkId());

        //Checking for self.
        if(closest == Node.self) {
            //This is the final node. This node should have the requested resource.
            EventManager.callEvent(new ResourcePullFinalStopEvent(pullMessage));
        } else {
            logger.info("Pulling resource with network id: {} from node: {}", pullMessage.resourceId, closest);

            //Sending the pull msg.
            closest.send(new NetworkMessage(MsgType.RES_PULL, pullMessage));
        }
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e instanceof ChannelMessageReceivedEvent) {
            //Network message received.
            final ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;

            if(messageReceivedEvent.message.msgType == MsgType.RES_PUSH) { //Content push.
                //Deserializing the resource message.
                final ResourceMessage resourceMessage = (ResourceMessage) messageReceivedEvent.message.content;

                //Handle receive
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        //Run push msg received logic.
                        resourceMessage.resource.receive(resourceMessage.sender, new Resource.IResourceActionListener() {
                            @Override
                            public void onFinished(Node other) {
                                //Call event.
                                EventManager.callEvent(new ResourcePushReceivedEvent(resourceMessage.resource, resourceMessage, messageReceivedEvent.remoteNode));

                                //Push further
                                pushFurther(resourceMessage);
                            }
                        });
                    }
                });
            } else if(messageReceivedEvent.message.msgType == MsgType.RES_PULL) { //Pull request.
                //Deserializing the resource message.
                final ResourcePullMessage resourcePullMessage = (ResourcePullMessage) messageReceivedEvent.message.content;

                //Call event.
                EventManager.callEvent(new ResourcePullReceivedEvent(resourcePullMessage));

                //Handle pull request.
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        pullFurther(resourcePullMessage);
                    }
                });
            }
        }
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {
        EventManager.registerEvent(ResourcePushReceivedEvent.class);
        EventManager.registerEvent(ResourcePullReceivedEvent.class);
        EventManager.registerEvent(ResourcePullSentEvent.class);
        EventManager.registerEvent(ResourcePushSentEvent.class);
        EventManager.registerEvent(ResourcePushFinalStopEvent.class);
        EventManager.registerEvent(ResourcePullFinalStopEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }
}
