package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Called when a connection has been established.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ChannelCreatedEvent extends ChannelEvent {
    /**
     * The node the connection is made to.
     */
    public Node other;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ChannelCreatedEvent(Channel c, boolean isServer) {
        super(c, isServer);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        if(isServer) {
            Server.logger.info("New connection established with: {}", channel.remoteAddress());
        } else {
            Client.logger.info("New connection established with: {}", channel.remoteAddress());
        }
    }
}
