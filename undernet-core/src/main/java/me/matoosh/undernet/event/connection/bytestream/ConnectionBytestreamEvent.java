package me.matoosh.undernet.event.connection.bytestream;

import me.matoosh.undernet.event.connection.ConnectionEvent;
import me.matoosh.undernet.p2p.router.connection.Connection;

/**
 * An event concerning a bytestream transmission.
 * Created by Mateusz Rębacz on 31.08.2017.
 */

public abstract class ConnectionBytestreamEvent extends ConnectionEvent {
    /**
     * The received bytes.
     */
    public byte[] bytes;


    /**
     * Creates a new connection event, given connection.
     *
     * @param c
     */
    public ConnectionBytestreamEvent(Connection c, byte[] bytes) {
        super(c);
        this.bytes = bytes;
    }
}
