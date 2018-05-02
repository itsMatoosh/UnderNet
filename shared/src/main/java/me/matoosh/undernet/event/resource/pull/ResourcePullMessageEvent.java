package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;

/**
 * Event concerning a resource pull message.
 */
public abstract class ResourcePullMessageEvent extends Event {

    /**
     * The pull message.
     */
    public ResourcePullMessage pullMessage;

    public ResourcePullMessageEvent(ResourcePullMessage pullMessage) {
        this.pullMessage = pullMessage;
    }
}
