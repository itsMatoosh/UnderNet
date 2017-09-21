package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.server.NodeNioServerSocketChannel;

/**
 * Called when a connection is dropped.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ChannelClosedEvent extends ChannelEvent {
    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     */
    public ChannelClosedEvent(Channel c) {
        super(c);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {
        if(channel instanceof NodeNioServerSocketChannel) {
            Router.logger.info("Server connection with: " + channel.remoteAddress() + " has been dropped");
        } else if(true) {
            Router.logger.info("Client connection with: " + channel.remoteAddress() + " has been dropped");
        }

    }
}
