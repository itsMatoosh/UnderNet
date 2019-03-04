package me.matoosh.undernet.p2p.router.data.message;

import java.nio.ByteBuffer;

/**
 * Message confirming the receiving of data by the other side.
 */
public class ResourceTransferControlMessage extends MsgBase {
    /**
     * The transfer id of the data.
     */
    private int transferId;

    /**
     * The id of the chunk requested.
     */
    private int controlId;

    public ResourceTransferControlMessage(int transferId, int controlId) {
        this.transferId = transferId;
        this.controlId = controlId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_TRANSFER_CONTROL;
    }

    @Override
    public void doDeserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.transferId = buffer.getInt();
        this.controlId = buffer.getInt();
    }

    @Override
    public byte[] doSerialize() {
        ByteBuffer buffer = ByteBuffer.allocate(8);
        buffer.putInt(transferId);
        buffer.putInt(controlId);
        return buffer.array();
    }

    public int getTransferId() {
        return transferId;
    }

    public int getControlId() {
        return controlId;
    }
}
