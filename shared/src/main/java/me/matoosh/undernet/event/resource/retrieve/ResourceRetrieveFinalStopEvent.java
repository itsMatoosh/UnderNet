package me.matoosh.undernet.event.resource.retrieve;

import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Called when a retrieve final stop at self node.
 */
public class ResourceRetrieveFinalStopEvent extends ResourceRetrieveReceivedEvent {
    public ResourceRetrieveFinalStopEvent(Resource resource, ResourceMessage resourceMessage) {
        super(resource, resourceMessage);
    }

    @Override
    public void onCalled() {
        ResourceManager.logger.info("Resource {} pulled successfully", this.resource);
    }
}
