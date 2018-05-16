package me.matoosh.undernet.event.channel;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

public class ConnectionEstablishedEvent extends Event {
    /**
     * The node the connection was established with.
     */
    public Node other;

    /**
     * Whether we are connected as server or client.
     */
    public boolean isServer;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param node
     * @param isServer
     */
    public ConnectionEstablishedEvent(Node node, boolean isServer) {
        this.other = node;
        this.isServer = isServer;
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        if(isServer) {
            Server.logger.info("Connection has been established with: {}", other);
        } else {
            Client.logger.info("Connection has been established with: {}", other);
        }
    }
}
