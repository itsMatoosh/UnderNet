package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

public class ConnectionEstablishedEvent extends ChannelCreatedEvent {
    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ConnectionEstablishedEvent(Channel c, boolean isServer) {
        super(c, isServer);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        if(isServer) {
            Server.logger.info("Connection has been established with: {}", channel.remoteAddress());
        } else {
            Client.logger.info("Connection has been established with: {}", channel.remoteAddress());
        }
    }
}
