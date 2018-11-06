package me.matoosh.undernet.event.resource.pull;

import me.matoosh.undernet.p2p.router.data.message.ResourcePullMessage;

/**
 * Called when a resource pull is received.
 */
public class ResourcePullReceivedEvent extends ResourcePullMessageEvent {
    public ResourcePullReceivedEvent(ResourcePullMessage pullMessage) {
        super(pullMessage);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        logger.info("Resource pull request received from: {}, for: {}", pullMessage.getNetworkMessage().getOrigin(), pullMessage.getNetworkMessage().getDestination());
    }
}
