package me.matoosh.undernet.p2p.router.data.message;

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

    public int getTransferId() {
        return transferId;
    }

    public int getControlId() {
        return controlId;
    }
}
