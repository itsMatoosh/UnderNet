package me.matoosh.undernet.event.ftp;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileTransfer;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileTransferType;

/**
 * Called when a file transfer finished.
 * Created by Mateusz Rębacz on 18.10.2017.
 */

public class FileTransferFinishedEvent extends FileTransferEvent {
    public FileTransferFinishedEvent(FileTransfer transfer) {
        super(transfer);
    }

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    @Override
    public void onCalled() {

    }
}
