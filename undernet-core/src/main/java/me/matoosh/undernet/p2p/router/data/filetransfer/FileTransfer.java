package me.matoosh.undernet.p2p.router.data.filetransfer;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;

/**
 * Represents a single active file transfer.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileTransfer {
    /**
     * Id of the transfer.
     */
    public NetworkID id;

    /**
     * The info on the transferred file.
     */
    public FileInfo fileInfo;

    /**
     * The node meant to receive the file.
     */
    public Node recipient;

    public FileTransfer(FileResource resource, Node recipient) {
        this.id = resource.networkID;
        this.fileInfo = resource.fileInfo;
        this.recipient = recipient;
    }
}
