package me.matoosh.undernet.p2p.router.data.message;

/**
 * Just a ping content.
 */
public class PingMessage extends MsgBase {
    /**
     * Whether this is the response content.
     */
    public boolean pong;

    public PingMessage(boolean pong) {
        this.pong = pong;
    }
}
