package me.matoosh.undernet.p2p.router.data.resource.transfer;

import io.netty.handler.stream.ChunkedInput;
import io.netty.handler.stream.ChunkedNioFile;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferErrorEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Handles file transfers.
 */
public class FileTransferHandler extends ResourceTransferHandler {
    /**
     * Output for sending file chunks.
     */
    public FileOutputStream outputStream;

    /**
     * The standard buffer size for file chunks.
     */
    public static final int CHUNK_SIZE = 1024;

    /**
     * The amount of bytes written from the received chunks.
     */
    private int written = 0;

    /**
     * The length of the file.
     */
    private long fileLength;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(FileTransferHandler.class);

    public FileTransferHandler(FileResource resource, ResourceTransferType fileTransferType, NetworkMessage.MessageDirection messageDirection, NetworkID recipient, byte transferId, Router router) {
        super(resource, fileTransferType, messageDirection, recipient, transferId, router);
        fileLength = Long.parseLong(resource.getInfo().attributes.get(0));

        //Preparing file streams.
        prepareStreams();
    }

    /**
     * Prepares the file streams for this transfer.
     */
    private void prepareStreams() {
        //Caching as file resource.
        File saveFile = ((FileResource)this.resource).file;
        logger.info("Preparing {} streams for file: {}", this.transferType, saveFile.getName()) ;

        if(this.transferType == ResourceTransferType.INBOUND) {
            //Creating or replacing the file.
            if (saveFile.exists()) {
                saveFile.delete();
            }
            try { //Creating new file.
                saveFile.createNewFile();
                outputStream = new FileOutputStream(saveFile);
            } catch (IOException e) {
                //Calling a transfer error.
                EventManager.callEvent(new ResourceTransferErrorEvent(this, e));
                return;
            }
        }
    }

    /**
     * Starts the file transfer.
     */
    @Override
    public void startSending() {
        if(transferType.equals(ResourceTransferType.OUTBOUND)) {
            //Sending the file using the chunked file logic.
            int totalRead = 0; //Amount of bytes read from the send stream.
            try {
                if(messageDirection == NetworkMessage.MessageDirection.TO_DESTINATION) {
                    router.networkMessageManager.sendMessage(new ChunkedNioFile(((FileResource)resource).file, CHUNK_SIZE), other);
                } else {
                    router.networkMessageManager.sendResponse(new ChunkedNioFile(((FileResource)resource).file, CHUNK_SIZE), other);
                }
            } catch (IOException e) {
                EventManager.callEvent(new ResourceTransferErrorEvent(FileTransferHandler.this, e));
            }
            finally {
                //File sent or error.
                EventManager.callEvent(new ResourceTransferFinishedEvent(FileTransferHandler.this));
            }
        }

    }

    /**
     * Closes the file streams.
     */
    @Override
    public void close() {
        try {
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Called when resource data is received.
     * @param dataMessage
     */
    @Override
    public void onResourceMessage(ResourceDataMessage dataMessage) {
        if(transferType == ResourceTransferType.INBOUND) {
            EventManager.callEvent(new ResourceTransferDataReceivedEvent(this, dataMessage));

            //Adding the data to the data byte[] of the transfer.
            if(dataMessage.resourceData.length != 0) {
                try {
                    outputStream.write(dataMessage.resourceData);
                    written += dataMessage.resourceData.length;
                    logger.info("File chunk received for: {} | {}/{}", this.resource.getNetworkID(), written, fileLength);
                    if(written >= fileLength) {
                        //File fully received.
                        EventManager.callEvent(new ResourceTransferFinishedEvent(this));
                    }
                } catch (IOException e) {
                    //Error
                    EventManager.callEvent(new ResourceTransferErrorEvent(this, e));

                    //Closing the input stream.
                    try {
                        this.outputStream.close();
                    } catch (IOException f) {
                        f.printStackTrace();
                    }
                }
            } else {
                //Empty chunk, ending the transfer and closing the file.
                logger.info("Empty chunk received for: {}, ending the transfer...");

                //File fully received.
                EventManager.callEvent(new ResourceTransferFinishedEvent(this));
            }
        }
    }
}
