package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.nio.ByteBuffer;

import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

/**
 * A message type to transfer chunks of data.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileChunkMessage extends NetworkMessage {
    /**
     * The file chunk of this message.
     */
    public FileChunk fileChunk;

    public FileChunkMessage() {}

    /**
     * Creates a network message given its type and raw data.
     * @param data
     */
    public FileChunkMessage(FileChunk data) {
        //Serializes the chunk data.
        byte[] chunkDat = NetworkMessage.serializeMessage(data);

        this.msgId = 1000;
        this.data = ByteBuffer.wrap(chunkDat);
        this.expiration = 0;
        this.dataLength = (short)(Short.MIN_VALUE + chunkDat.length);
        this.checksum = calcChecksum();
    }
}
