package me.matoosh.undernet.p2p.router.data.resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;

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
        //Creating a new ResourcePushMessage.
        ResourcePushMessage pushMessage = new ResourcePushMessage(resource);

        //Sending the message to the neighbor closest to it.
        pushForward(pushMessage);
    }

    /**
     * Forwards a pushMessage to the next appropriate node.
     * Calls resource stored event if this node is the reosurce's destination.
     * @param pushMessage
     */
    public void pushForward(ResourcePushMessage pushMessage) {
        //Getting the node closest to the resource.
        Node closest = router.neighborNodesManager.getClosestTo(pushMessage.resource.networkID);
        if(closest == Node.self) {
            //This is the final node of the resource.
            //TODO: Call a resource stored event.
        } else {
            //Calling the onPush method.
            pushMessage.resource.onPush(closest);
            //Sending the push msg.
            closest.send(new NetworkMessage(MsgType.RES_PUSH, pushMessage));
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
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
            if(messageReceivedEvent.message.msgId == MsgType.RES_PUSH.ordinal()) {
                //Resource push msg.
                ResourcePushMessage pushMessage = (ResourcePushMessage)NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());

                //TODO: Request the file transfer of the resource.

                //TODO: Once the file transfer completes, push the resource onward.
            }
        }
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {}

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }
}
