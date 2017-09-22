package me.matoosh.undernet.event.channel.bytestream;

import io.netty.channel.Channel;

/**
 * Called when a bytestream is received by the client.
 * Created by Mateusz Rębacz on 31.08.2017.
 */

public class ChannelBytestreamReceivedEvent extends ChannelBytestreamEvent {

    /**
     * Creates a new channel event, given the channel.
     *
     * @param c
     * @param isServer
     * @param data
     */
    public ChannelBytestreamReceivedEvent(Channel c, boolean isServer, byte[] data) {
        super(c, isServer, data);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
