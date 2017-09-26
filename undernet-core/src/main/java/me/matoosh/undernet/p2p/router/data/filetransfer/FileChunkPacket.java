package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.nio.ByteBuffer;

import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

/**
 * A chunk of a file transfered over the network.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileChunkPacket extends NetworkMessage {
    public FileChunkPacket() {}

    /**
     * Creates a network message given its type and raw data.
     * @param transferId
     * @param data
     */
    public FileChunkPacket(int transferId, byte[] data) {
        this.msgId = 1000 + transferId;
        this.data = ByteBuffer.wrap(data);
        this.expiration = 0;
        this.dataLength = (short)(Short.MIN_VALUE + data.length);
        this.checksum = calcChecksum();
    }
}
