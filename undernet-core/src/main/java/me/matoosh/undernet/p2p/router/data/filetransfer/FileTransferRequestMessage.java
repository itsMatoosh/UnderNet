package me.matoosh.undernet.p2p.router.data.filetransfer;

import me.matoosh.undernet.p2p.router.data.message.MsgBase;

/**
 * Message for requesting file transfers.
 * Created by Mateusz Rębacz on 01.10.2017.
 */

public class FileTransferRequestMessage implements MsgBase {
    /**
     * The id of the requested transfer.
     */
    public short transferId;

    public FileTransferRequestMessage(short transferId) {
        this.transferId = transferId;
    }
}
