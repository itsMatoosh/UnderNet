package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.event.resource.ResourceEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource pull is received.
 */
public class ResourcePullReceivedEvent extends ResourcePullMessageEvent {
    public ResourcePullReceivedEvent(ResourcePullMessage pullMessage) {
        super(pullMessage);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
