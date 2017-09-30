package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.util.ArrayList;

import me.matoosh.undernet.p2p.router.Router;

/**
 * Manages the file transfers between the nodes.
 * Created by Mateusz Rębacz on 29.09.2017.
 */

public class FileTransferManager {
    /**
     * The router.
     */
    public Router router;

    /**
     * The currently active file transfers.
     */
    public ArrayList<FileTransfer> activeTransfers = new ArrayList<>();

    /**
     * Creates a file transfer manager given router.
     * @param router
     */
    public FileTransferManager(Router router) {
        this.router = router;
    }
}
