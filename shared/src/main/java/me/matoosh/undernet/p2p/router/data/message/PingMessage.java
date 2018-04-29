package me.matoosh.undernet.p2p.router.data.message;

/**
 * Just a ping message.
 */
public class PingMessage extends MsgBase {
    public boolean pong;

    public PingMessage(boolean pong) {
        this.pong = pong;
    }
}
