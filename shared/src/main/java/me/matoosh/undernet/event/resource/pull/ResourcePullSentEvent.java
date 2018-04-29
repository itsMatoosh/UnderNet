package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.event.resource.ResourceEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource pull is sent.
 */
public class ResourcePullSentEvent extends ResourceEvent {
    /**
     * The pull message.
     */
    public ResourceMessage pullMessage;
    /**
     * The node the message is sent to.
     */
    public Node recipientNode;

    public ResourcePullSentEvent(Resource resource, ResourceMessage msg, Node recipientNode) {
        super(resource);
        this.pullMessage = msg;
        this.recipientNode = recipientNode;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
