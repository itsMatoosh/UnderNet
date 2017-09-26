package me.matoosh.undernet.p2p.router.data.messages;

/**
 * Base of the message.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public interface MsgBase {
    /**
     * Convert the message data to byte[].
     * @return
     */
    public byte[] toByte();

    /**
     * Convert the byte[] to the message.
     */
    public void fromByte(byte[] data);
}
