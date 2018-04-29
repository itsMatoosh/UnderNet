package me.matoosh.undernet.event.resource.push;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;
import me.matoosh.undernet.p2p.router.data.resource.Resource;
import me.matoosh.undernet.p2p.router.data.resource.ResourceManager;

/**
 * Called when a resource push is received and the node is its final stop.
 * Created by Mateusz Rębacz on 02.10.2017.
 */

public class ResourceFinalStopEvent extends ResourcePushReceivedEvent {
    public ResourceFinalStopEvent(Resource resource, ResourceMessage msg, Node sender) {
        super(resource, msg, sender);
    }

    @Override
    public void onCalled() {
        ResourceManager.logger.info("Resource: {} reached its destination on the {} node and was saved in the content folder.", resource.toString(), Node.self);
    }
}
