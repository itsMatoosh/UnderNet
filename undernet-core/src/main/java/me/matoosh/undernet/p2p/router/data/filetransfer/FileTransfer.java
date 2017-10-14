package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.ftp.FileTransferErrorEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
     * The type of the file transfer.
     */
    public FileTransferType fileTransferType;

    /**
     * Input for receiving file chunks.
     */
    public FileInputStream inputStream;
    /**
     * Output for sending file chunks.
     */
    public FileOutputStream outputStream;

    /**
     * The node meant to receive the file.
     */
    public Node recipient;

    public FileTransfer(FileResource resource, Node remoteNode, FileTransferType fileTransferType) {
        this.id = resource.networkID;
        this.fileInfo = resource.fileInfo;
        this.recipient = remoteNode;
        this.fileTransferType = fileTransferType;

        prepareStreams();
    }
    /**
     * Prepares the file streams for this trasfer.
     */
    private void prepareStreams() {
        //Caching the path of the file.
        File f = new File(UnderNet.fileManager.getContentFolder() + "/" + fileInfo.fileName);

        if(fileTransferType == FileTransferType.INBOUND) {
            //Creating or replacing the file.
            if (f.exists()) {
                f.delete();
            }
            try { //Creating new file.
                f.createNewFile();
                inputStream = new FileInputStream(f);
            } catch (IOException e) {
                //Calling a transfer error.
                FileTransferManager.logger.error("Couldn't create file: " + f, e);
                EventManager.callEvent(new FileTransferErrorEvent(this, e));
                return;
            }

        } else {
            try {
                outputStream = new FileOutputStream(f);
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                FileTransferManager.logger.error("Couldn't find file: " + f, new FileNotFoundException(f.toString()));
                EventManager.callEvent(new FileTransferErrorEvent(this, new FileNotFoundException(f.toString())));
                return;
            }
        }
    }

    /**
     * Starts the file transfer.
     */
    public void startSending() {
        if(fileTransferType.equals(FileTransferType.OUTBOUND)) {
            //TODO: File sending logic.
        }
        throw new NotImplementedException();
    }

    /**
     * Called when a file chunk has been received.
     * @param chunk
     */
    public void onChunkReceived(FileChunk chunk) {

    }
}
