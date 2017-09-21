package me.matoosh.undernet.event.channel;

import io.netty.channel.Channel;
import me.matoosh.undernet.event.Event;

/**
 * An event concerning a connection.
 * Created by Mateusz Rębacz on 27.06.2017.
 */

public abstract class ChannelEvent extends Event {
    /**
     * The connection.
     */
    public Channel channel;

    /**
     * Creates a new channel event, given the channel.
     * @param c
     */
    public ChannelEvent(Channel c) {
        this.channel = c;
    }
}
