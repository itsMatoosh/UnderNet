package me.matoosh.undernet.p2p.router.data.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.resource.ResourceFinalStopEvent;
import me.matoosh.undernet.event.resource.ResourcePushReceivedEvent;
import me.matoosh.undernet.event.resource.ResourcePushSentEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
     * Executor used for managing resource logic.
     */
    public ExecutorService executor = Executors.newSingleThreadExecutor();

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

        //Creating a new ResourcePushMessage.
        final ResourcePushMessage pushMessage = new ResourcePushMessage(resource);

        //Log
        logger.info("Publishing resource: " + resource + "...");

        //Sending the message to the neighbor closest to it.
        executor.submit(new Runnable() {
            @Override
            public void run() {
                pushForward(pushMessage);
            }
        });
    }

    /**
     * Pulls a resource with the specified network id.
     * @param networkID
     */
    public void pull(NetworkID networkID) {
        //Making sure we're connected to the network.
        if(UnderNet.router.status != InterfaceStatus.STARTED) {
            logger.warn("Cannot pull a resource without connection to the network.");
            return;
        }

        throw new NotImplementedException();
    }

    /**
     * Forwards a pushMessage to the next appropriate node.
     * Calls resource stored event if this node is the resource's destination.
     * @param pushMessage
     */
    public void pushForward(ResourcePushMessage pushMessage) {
        //Getting the node closest to the resource.
        Node closest = router.neighborNodesManager.getClosestTo(pushMessage.resource.networkID);
        if(closest == Node.self) {
            //This is the final node of the resource.
            EventManager.callEvent(new ResourceFinalStopEvent(pushMessage.resource, pushMessage, null));
        } else {
            logger.info("Pushing resource: " + pushMessage.resource + " to node: " + closest);

            //Calling the onPush method.
            pushMessage.resource.onPush(pushMessage, closest);

            //Sending the push msg.
            closest.send(new NetworkMessage(MsgType.RES_PUSH, pushMessage));

            //Calling event.
            EventManager.callEvent(new ResourcePushSentEvent(pushMessage.resource, pushMessage, closest));
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
            if(messageReceivedEvent.message.msgId == MsgType.RES_PUSH.ordinal()) {
                executor.submit(new Runnable() {
                    @Override
                    public void run() {
                        //Resource push msg.
                        final ResourcePushMessage pushMessage = (ResourcePushMessage)NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());

                        //Run push msg received logic.
                        pushMessage.resource.onPushReceive(pushMessage, messageReceivedEvent.remoteNode);

                        //Call event.
                        EventManager.callEvent(new ResourcePushReceivedEvent(pushMessage.resource, pushMessage, messageReceivedEvent.remoteNode));
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
        EventManager.registerEvent(ResourcePushSentEvent.class);
        EventManager.registerEvent(ResourceFinalStopEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }
}
