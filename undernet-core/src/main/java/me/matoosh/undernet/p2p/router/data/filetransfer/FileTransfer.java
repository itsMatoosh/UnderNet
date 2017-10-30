package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.ftp.FileTransferErrorEvent;
import me.matoosh.undernet.event.ftp.FileTransferFinishedEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;

import static me.matoosh.undernet.p2p.router.Router.logger;

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
     * The file location.
     */
    public File file;

    /**
     * The standard buffer size for file chunks.
     */
    public static int BUFFER_SIZE = 1024;

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
        file = new File(UnderNet.fileManager.getContentFolder() + "/" + fileInfo.fileName);

        if(fileTransferType == FileTransferType.OUTBOUND) {
            //Creating or replacing the file.
            if (file.exists()) {
                file.delete();
            }
            try { //Creating new file.
                file.createNewFile();
                inputStream = new FileInputStream(file);
            } catch (IOException e) {
                //Calling a transfer error.
                FileTransferManager.logger.error("Couldn't create file: " + file, e);
                EventManager.callEvent(new FileTransferErrorEvent(this, e));
                return;
            }

        } else {
            try {
                outputStream = new FileOutputStream(file);
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                FileTransferManager.logger.error("Couldn't find file: " + file, new FileNotFoundException(file.toString()));
                EventManager.callEvent(new FileTransferErrorEvent(this, new FileNotFoundException(file.toString())));
                return;
            }
        }
    }

    /**
     * Starts the file transfer.
     */
    public void startSending() {
        if(fileTransferType.equals(FileTransferType.OUTBOUND)) {
            //File sending logic.
            UnderNet.router.fileTransferManager.executor.submit(new Callable() {
                @Override
                public Object call() throws Exception {
                    int read = 0;
                    try {
                        while(read < fileInfo.fileLength) {
                            byte[] buffer = new byte[BUFFER_SIZE];
                            read += inputStream.read(buffer);
                            sendChunk(buffer);
                        }
                    } catch (IOException e) {
                        FileTransferManager.logger.error("Error reading chunk from file: " + file, e);
                        EventManager.callEvent(new FileTransferErrorEvent(FileTransfer.this, e));
                    }
                    finally {
                        //File sent or error.
                        EventManager.callEvent(new FileTransferFinishedEvent(FileTransfer.this));
                    }
                    return null;
                }
            });
        }

    }

    /**
     * Sends a chunk of data to the recipient.
     * @param buffer
     */
    private void sendChunk(byte[] buffer) {
        NetworkMessage msg = new NetworkMessage(MsgType.FILE_CHUNK, NetworkMessage.serializeMessage(new FileChunk(id, buffer)));
        recipient.send(msg);
    }

    /**
     * Called when a file chunk has been received.
     * @param chunk
     */
    public void onChunkReceived(FileChunk chunk) {
        if(fileTransferType == FileTransferType.INBOUND) {
            //Adding the data to the data byte[] of the transfer.
            try {
                outputStream.write(chunk.data);
                if(file.length() <= fileInfo.fileLength) {
                    //File fully received.
                    EventManager.callEvent(new FileTransferFinishedEvent(this));
                }
            } catch (IOException e) {
                FileTransferManager.logger.error("Error appending the received file chunk to file!", e);
            }
        }
    }
}
