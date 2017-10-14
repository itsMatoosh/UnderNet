package me.matoosh.undernet.event.ftp;

import me.matoosh.undernet.event.resource.ResourceErrorEvent;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileTransfer;
import me.matoosh.undernet.p2p.router.data.resource.Resource;

/**
 * Called when an error occurs with file resource transfer.
 * Created by Mateusz Rębacz on 14.10.2017.
 */

public class FileTransferErrorEvent extends FileTransferEvent {
    /**
     * The exception.
     */
    public Exception exception;

    public FileTransferErrorEvent(FileTransfer transfer, Exception exception) {
        super(transfer);
        this.exception = exception;
    }

    @Override
    public void onCalled() {

    }
}
