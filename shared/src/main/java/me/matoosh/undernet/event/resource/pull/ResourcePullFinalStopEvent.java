package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Called when a resource pull message reaches its destination on the self node.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class ResourcePullFinalStopEvent extends ResourcePullReceivedEvent {
    public ResourcePullFinalStopEvent(Resource resource, ResourceMessage msg) {
        super(resource, msg);
    }

    @Override
    public void onCalled() {
        ResourceManager.logger.info("Resource: {} will be pulled from {}...", resource.toString(), Node.self);
    }
}
