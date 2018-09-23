package me.matoosh.undernet.p2p.router.data.resource.transfer;

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
     * Input for receiving file chunks.
     */
    public FileInputStream inputStream;
    /**
     * Output for sending file chunks.
     */
    public FileOutputStream outputStream;

    /**
     * The final length of the received file.
     */
    private long fileLength;

    /**
     * The standard buffer size for file chunks.
     */
    public static final int BUFFER_SIZE = 1024;

    /**
     * The service for executing file transfers.
     */
    public static ExecutorService ftpExecutor = Executors.newSingleThreadExecutor();

    /**
     * The amount of bytes written from the received chunks.
     */
    private int written = 0;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(FileTransferHandler.class);

    public FileTransferHandler(FileResource resource, ResourceTransferType fileTransferType, NetworkMessage.MessageDirection messageDirection, NetworkID recipient, byte transferId, Router router) {
        super(resource, fileTransferType, messageDirection, recipient, transferId, router);

        this.fileLength = Long.parseLong(resource.getInfo().attributes.get(0));

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

        if(this.transferType == ResourceTransferType.OUTBOUND) {
            try {
                inputStream = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                EventManager.callEvent(new ResourceTransferErrorEvent(this, e));
                return;
            }
        } else {
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
            //File sending logic.
            ftpExecutor.submit(() -> {
                int totalRead = 0; //Amount of bytes read from the send stream.
                try {
                    if(inputStream.available() != 0) {
                        //The send buffer.
                        byte[] buffer = new byte[BUFFER_SIZE];
                        int read;
                        while ((read = inputStream.read(buffer)) > 0) {
                            totalRead += read;

                            byte[] data = new byte[read];
                            System.arraycopy(buffer, 0, data, 0, read);

                            sendChunk(data);
                            logger.info("Chunk sent - {}%", ((float)totalRead/Long.parseLong(resource.getInfo().attributes.get(0)))*100f);
                        }
                    } else {
                        //The file has no data. Sending an empty chunk.
                        sendChunk(new byte[0]);
                    }
                } catch (IOException e) {
                    EventManager.callEvent(new ResourceTransferErrorEvent(FileTransferHandler.this, e));
                }
                finally {
                    //File sent or error.
                    EventManager.callEvent(new ResourceTransferFinishedEvent(FileTransferHandler.this));
                }
                return null;
            });
        }

    }

    /**
     * Closes the file streams.
     */
    @Override
    public void close() {
        try {
            if (inputStream != null) {
                inputStream.close();
                inputStream = null;
            }
            if (outputStream != null) {
                outputStream.close();
                outputStream = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends a chunk of data to the other.
     * @param buffer
     */
    private void sendChunk(byte[] buffer) {
        ResourceDataMessage message = new ResourceDataMessage(buffer, transferId);

        if(messageDirection == NetworkMessage.MessageDirection.TO_DESTINATION) {
            router.networkMessageManager.sendMessage(message, other);
        } else {
            router.networkMessageManager.sendResponse(message, other);
        }

        EventManager.callEvent(new ResourceTransferDataSentEvent(this, message));
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
                    logger.info("File chunk received for: {} | {}%", this.resource.getNetworkID(), ((float)written/(float)fileLength)*100f);
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
