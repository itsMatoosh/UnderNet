package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.router.client.Client;
import me.matoosh.undernet.p2p.router.server.Server;

/**
 * Called when a connection is dropped.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ChannelClosedEvent extends ChannelEvent {
    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     */
    public ChannelClosedEvent(Channel c, boolean isServer) {
        super(c, isServer);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        if(isServer) {
            Server.logger.info("Connection with: {} has been dropped", channel.remoteAddress());
        } else {
            Client.logger.info("Connection with: {} has been dropped", channel.remoteAddress());
        }
    }
}
