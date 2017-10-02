package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Base for resource events.
 * Created by Mateusz Rębacz on 02.10.2017.
 */

public abstract class ResourceEvent extends Event {
    /**
     * The resource.
     */
    public Resource resource;

    public ResourceEvent(Resource resource) {
        this.resource = resource;
    }
}
