package me.matoosh.undernet.event.channel.bytestream;

import io.netty.channel.Channel;
import me.matoosh.undernet.event.channel.ChannelEvent;

/**
 * An event concerning a bytestream transmission.
 * Created by Mateusz Rębacz on 31.08.2017.
 */

public abstract class ChannelBytestreamEvent extends ChannelEvent {
    /**
     * The received bytes.
     */
    public byte[] data;

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     */
    public ChannelBytestreamEvent(Channel c, byte[] data) {
        super(c);
        this.data = data;
    }
}
