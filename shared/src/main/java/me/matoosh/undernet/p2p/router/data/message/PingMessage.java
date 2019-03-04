package me.matoosh.undernet.p2p.router.data.message;

/**
 * Just a ping content.
 */
public class PingMessage extends MsgBase {
    /**
     * Whether this is the response content.
     */
    private boolean pong;

    public PingMessage(boolean pong) {
        this.pong = pong;
    }

    @Override
    public MsgType getType() {
        return MsgType.NODE_PING;
    }

    @Override
    public void doDeserialize(byte[] data) {
        if(data[0] == 1) this.pong = true;
        else this.pong = false;
    }

    @Override
    public byte[] doSerialize() {
        if(pong) {
            return new byte[] {1};
        }
        return new byte[] {0};
    }

    public boolean isPong() {
        return pong;
    }
}
