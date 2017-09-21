package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.p2p.router.server.ServerChannelHandler;

/**
 * Called when a channel error occurs.
 * Error means that the connection was dropped due to a problem.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public class ChannelErrorEvent extends ChannelEvent {
    /**
     * The connection exception.
     */
    public Throwable exception;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param exception
     */
    public ChannelErrorEvent(Channel c, Throwable exception) {
        super(c);
        this.exception = exception;
    }


    /**
     * Executed when the event is called.
     */
    @Override
    public void onCalled() {

    }
}
