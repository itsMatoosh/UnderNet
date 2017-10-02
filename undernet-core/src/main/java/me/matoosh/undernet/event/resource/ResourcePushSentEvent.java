package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource push is sent to a neighbor.
 * Created by Mateusz Rębacz on 02.10.2017.
 */

public class ResourcePushSentEvent extends ResourceEvent {
    /**
     * The push message.
     */
    public ResourcePushMessage pushMessage;
    /**
     * The node the message is sent to.
     */
    public Node recipientNode;

    public ResourcePushSentEvent(Resource resource, ResourcePushMessage msg, Node recipientNode) {
        super(resource);
        this.pushMessage = msg;
        this.recipientNode = recipientNode;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
