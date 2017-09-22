package me.matoosh.undernet.p2p.router.data.messages;

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
    public byte[] data;
}
