package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource push is received.
 * Created by Mateusz Rębacz on 02.10.2017.
 */

public class ResourcePushReceivedEvent extends ResourceEvent {
    /**
     * The push message.
     */
    public ResourcePushMessage pushMessage;
    /**
     * The sender of the push.
     */
    public Node sender;

    public ResourcePushReceivedEvent(Resource resource, ResourcePushMessage msg, Node sender) {
        super(resource);
        this.pushMessage = msg;
        this.sender = sender;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
