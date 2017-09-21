package me.matoosh.undernet.event.channel.bytestream;

import me.matoosh.undernet.event.channel.ChannelEvent;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * An event concerning a bytestream transmission.
 * Created by Mateusz Rębacz on 31.08.2017.
 */

public abstract class ChannelBytestreamEvent extends ChannelEvent {
    /**
     * The received bytes.
     */
    public byte[] bytes;


    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ChannelBytestreamEvent(Connection c, byte[] bytes) {
        super(c);
        this.bytes = bytes;
    }
}
