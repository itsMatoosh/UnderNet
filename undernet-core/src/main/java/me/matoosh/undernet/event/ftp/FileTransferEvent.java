package me.matoosh.undernet.event.ftp;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileTransfer;

/**
 * Event regarding file transfer.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public abstract class FileTransferEvent extends Event {
    /**
     * The file transfer.
     */
    public FileTransfer transfer;

    public FileTransferEvent(FileTransfer transfer) {
        this.transfer = transfer;
    }
}
