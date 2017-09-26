package me.matoosh.undernet.p2p.router.data.resource;

import java.util.ArrayList;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;

/**
 * Manages network resources locally.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class ResourceManager extends EventHandler {
    /**
     * List of the stored resources.
     */
    public ArrayList<Resource> resourcesStored;
    /**
     * The router of the manager.
     */
    public Router router;

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
            closest.send(new NetworkMessage(MsgType.RES_PUSH, pushMessage));
        }
    }

    /**
     * Sets up the resource manager.
     */
    public void setup(Router router) {
        this.router = router;
        registerHandlers();
    }

    /**
     * Registers the message handlers.
     */
    private void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
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
}
