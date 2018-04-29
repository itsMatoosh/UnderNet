package me.matoosh.undernet.p2p.router.data.message;

/**
 * Just a ping message.
 */
public class PingMessage extends MsgBase {
    /**
     * Whether this is the response message.
     */
    public boolean pong;

    public PingMessage(boolean pong) {
        this.pong = pong;
    }
}
