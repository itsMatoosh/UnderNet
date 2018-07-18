package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;

/**
 * Called when a resource pull is sent.
 */
public class ResourcePullSentEvent extends ResourcePullMessageEvent {
    public ResourcePullSentEvent(ResourcePullMessage pullMessage) {
        super(pullMessage);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        logger.info("Pull request was sent for: {}", pullMessage.networkMessage.getDestination());
    }
}
