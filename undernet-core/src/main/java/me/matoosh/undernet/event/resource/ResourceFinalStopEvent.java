package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Called when a resource push is received and the node is its final stop.
 * Created by Mateusz Rębacz on 02.10.2017.
 */

public class ResourceFinalStopEvent extends ResourcePushReceivedEvent {
    public ResourceFinalStopEvent(Resource resource, ResourcePushMessage msg, Node sender) {
        super(resource, msg, sender);
    }

    @Override
    public void onCalled() {
        ResourceManager.logger.info("Resource: " + resource.toString() + " reached its destination on the " + Node.self + " node and was saved in the content folder.");
    }
}
