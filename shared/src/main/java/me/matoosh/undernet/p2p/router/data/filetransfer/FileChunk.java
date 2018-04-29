package me.matoosh.undernet.p2p.router.data.filetransfer;

import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;

/**
 * Represents a file chunk.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public class FileChunk extends MsgBase {
    /**
     * The id of the transfer the chunk belongs to.
     */
    public NetworkID transferId;
    /**
     * The data of this chunk.
     */
    public byte[] data;

    public FileChunk(NetworkID transferId, byte[] data) {
        this.transferId = transferId;
        this.data = data;
    }
}
