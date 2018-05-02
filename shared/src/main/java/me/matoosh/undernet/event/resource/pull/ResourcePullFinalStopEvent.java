package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Called when a resource pull content reaches its destination on the self node.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class ResourcePullFinalStopEvent extends ResourcePullReceivedEvent {

    public ResourcePullFinalStopEvent(ResourcePullMessage pullMessage) {
        super(pullMessage);
    }

    @Override
    public void onCalled() {
        super.onCalled();
        ResourceManager.logger.info("Resource: {} will be pulled from {}...", pullMessage.resourceId, Node.self);
    }
}
