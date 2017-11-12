package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.event.resource.ResourceEvent;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when a resource pull message reaches its destination on the self node.
 * Created by Mateusz Rębacz on 12.11.2017.
 */

public class ResourcePullFinalStopEvent extends ResourceEvent {
    public ResourcePullFinalStopEvent(Resource resource) {
        super(resource);
    }

    @Override
    public void onCalled() {

    }
}
