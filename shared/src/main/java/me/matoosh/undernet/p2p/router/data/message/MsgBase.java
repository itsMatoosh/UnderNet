package me.matoosh.undernet.p2p.router.data.message;

import java.nio.ByteBuffer;

/**
 * Base of message content.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public abstract class MsgBase {
    /**
     * The network message object that carries this content.
     */
    private NetworkMessage networkMessage;

    public NetworkMessage getNetworkMessage() {
        return networkMessage;
    }

    public void setNetworkMessage(NetworkMessage networkMessage) {
        this.networkMessage = networkMessage;
    }

    public MsgBase() {}

    /**
     * Gets the type of the message.
     * @return
     */
    public abstract MsgType getType();

    /**
     * Deserializes the contents of the message from a byte array.
     * @param data
     */
    public static MsgBase deserialize(byte[] data) throws InstantiationException, IllegalAccessException {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        short type = buffer.getShort();
        byte[] msgData = new byte[data.length - 2];
        buffer.get(msgData);

        return MsgType.getById(type).getMessageInstance(msgData);
    }
    /**
     * Deserializes the contents of the message from a byte array.
     * @param data
     */
    public abstract void doDeserialize(byte[] data);

    /**
     * Serializes the contents of the message to a byte array.
     * @return
     */
    public byte[] serialize() {
        byte[] serializedData = doSerialize();
        ByteBuffer buffer = ByteBuffer.allocate(serializedData.length + 2);
        buffer.putShort(getType().id);
        buffer.put(serializedData);
        return buffer.array();
    }

    /**
     * Serializes the contents of the message to a byte array.
     * @return
     */
    public abstract byte[] doSerialize();
}
