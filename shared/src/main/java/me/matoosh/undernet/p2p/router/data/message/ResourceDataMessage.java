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
     * Creates a new resource message given the resource.
     * @param data
     */
    public ResourceDataMessage(byte[] data, int transferId) {
        this.resourceData = data;
        this.transferId = transferId;
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
}
