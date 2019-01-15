package me.matoosh.undernet.p2p.router.data.message;

/**
 * Message containing resource data.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class ResourceDataMessage extends MsgBase {
    /**
     * The resource to be pushed.
     */
    private byte[] resourceData;

    /**
     * The transfer id of the data.
     */
    private int transferId;

    /**
     * The id of the file chunk
     */
    private int chunkId;

    /**
     * Creates a new resource message given the resource.
     * @param data
     */
    public ResourceDataMessage(byte[] data, int transferId, int chunkId) {
        this.resourceData = data;
        this.transferId = transferId;
        this.chunkId = chunkId;
    }

    @Override
    public MsgType getType() {
        return MsgType.RES_DATA;
    }

    public byte[] getResourceData() {
        return resourceData;
    }

    public int getTransferId() {
        return transferId;
    }

    public int getChunkId() {
        return chunkId;
    }
}
