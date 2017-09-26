package me.matoosh.undernet.p2p.router.data.message;

import java.nio.ByteBuffer;

/**
 * A message that can be serialized and deserialized using the NetworkMessageSerializer.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage {
    /**
     * Unique id of the message.
     */
    public int msgId;
    /**
     * The expiration time of the message.
     */
    public long expiration;
    /**
     * Ensures data integrity on the receivers side.
     */
    public byte checksum;
    /**
     * The lenght of the sent data.
     */
    public short dataLength;

    /**
     * The data transported by the message.
     * Can be up to 64 fragments (64KB)
     */
    public ByteBuffer data;

    public NetworkMessage() {}

    /**
     * Creates a network message given its type and raw data.
     * @param msgType
     * @param data
     */
    public NetworkMessage(MsgType msgType, byte[] data) {
        this.msgId = msgType.ordinal();
        this.data = ByteBuffer.wrap(data);
        this.dataLength = (short)(Short.MIN_VALUE + data.length);
        this.checksum = calcChecksum();
        this.expiration = 0;
    }

    /**
     * Creates a network message given its type and content.
     * @param msgType
     * @param msg
     */
    public NetworkMessage(MsgType msgType, MsgBase msg) {
        this.msgId = msgType.ordinal();
        this.data = ByteBuffer.wrap(msg.toByte());
        this.dataLength = (short)(Short.MIN_VALUE + this.data.array().length);
        this.checksum = calcChecksum();
        this.expiration = 0;
    }

    /**
     * Calculates the checksum of the message data.
     * @return
     */
    public byte calcChecksum() {
        byte sum = 0;
        byte[] dataArr = data.array();
        for (int i = 0; i < dataArr.length; i++)
            sum += dataArr[i];
        return sum;
    }
}
