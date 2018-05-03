package me.matoosh.undernet.event.resource.retrieve;

import me.matoosh.undernet.event.resource.ResourceEvent;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource retrieve is received.
 */
public class ResourceRetrieveReceivedEvent extends ResourceEvent {
    /**
     * The resource message.
     */
    public ResourceMessage resourceMessage;


    public ResourceRetrieveReceivedEvent(Resource resource, ResourceMessage resourceMessage) {
        super(resource);
        this.resourceMessage = resourceMessage;
    }

    @Override
    public void onCalled() {

    }
}
