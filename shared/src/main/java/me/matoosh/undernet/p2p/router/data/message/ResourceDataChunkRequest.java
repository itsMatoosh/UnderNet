package me.matoosh.undernet.p2p.router.data.message;

/**
 * Message confirming the receiving of data by the other side.
 */
public class ResourceDataChunkRequest extends MsgBase {
    /**
     * The transfer id of the data.
     */
    private int transferId;

    /**
     * The id of the chunk requested.
     */
    private int chunkId;

    public ResourceDataChunkRequest(int transferId, int chunkId) {
        this.transferId = transferId;
        this.chunkId = chunkId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_DATA_REQUEST;
    }

    public int getTransferId() {
        return transferId;
    }

    public int getChunkId() {
        return chunkId;
    }
}
