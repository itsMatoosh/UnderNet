package me.matoosh.undernet.p2p.router.data.message;

/**
 * Parent for all messages with no serializable content.
 */
public abstract class ContentlessMsgBase extends MsgBase {
    @Override
    public void doDeserialize(byte[] data) {}

    @Override
    public byte[] doSerialize() { return new byte[0]; }
}
