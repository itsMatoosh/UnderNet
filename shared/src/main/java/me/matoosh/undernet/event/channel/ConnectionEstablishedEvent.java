package me.matoosh.undernet.event.channel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;

public class ConnectionEstablishedEvent extends Event {
    /**
     * The node the connection was established with.
     */
    public Node other;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param node
     */
    public ConnectionEstablishedEvent(Node node) {
        this.other = node;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        Router.logger.info("Connection has been established with: {}", other);
    }
}
