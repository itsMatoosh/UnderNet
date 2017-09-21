package me.matoosh.undernet.event.channel.bytestream;

import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * Called when a bytestream is received by the client.
 * Created by Mateusz Rębacz on 31.08.2017.
 */

public class ChannelBytestreamReceivedEvent extends ChannelBytestreamEvent {

    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ChannelBytestreamReceivedEvent(Connection c, byte[] bytes) {
        super(c, bytes);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
