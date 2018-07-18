package me.matoosh.undernet.event.resource;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;

/**
 * An event concerning a resource message.
 */
public abstract class ResourceMessageEvent extends Event {

    /**
     * The resource message.
     */
    public ResourceDataMessage resourceMessage;

    public ResourceMessageEvent(ResourceDataMessage resourceMessage) {
        this.resourceMessage = resourceMessage;
    }
}
