package me.matoosh.undernet.p2p.router.data.message;

import java.nio.ByteBuffer;

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

    public ResourceDataMessage() {}

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

    @Override
    public void doDeserialize(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        this.resourceData = new byte[data.length - 4];
        this.transferId = buffer.getInt();
        buffer.get(resourceData);
    }

    @Override
    public byte[] doSerialize() {
        ByteBuffer buffer = ByteBuffer.allocate(resourceData.length + 4);
        buffer.putInt(transferId);
        buffer.put(resourceData);
        return buffer.array();
    }

    public byte[] getResourceData() {
        return resourceData;
    }

    public int getTransferId() {
        return transferId;
    }
}
