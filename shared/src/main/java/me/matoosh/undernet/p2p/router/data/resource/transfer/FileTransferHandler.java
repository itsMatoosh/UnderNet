package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferErrorEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferFinishedEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataChunkRequest;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

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
     * The standard buffer size for file chunks
     */
    public static final int BUFFER_SIZE = 10240;

    /**
     * The amount of bytes written from the received chunks.
     */
    private int written = 0;

    /**
     * The amount of bytes sent.
     */
    private int sent = 0;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(FileTransferHandler.class);

    public FileTransferHandler(FileResource resource, ResourceTransferType fileTransferType, MessageTunnel tunnel, byte transferId, Router router) {
        super(resource, fileTransferType, tunnel, transferId, router);

        this.fileLength = Long.parseLong(resource.getInfo().attributes.get(0));

        //Preparing file streams.
        prepareStreams();
    }

    /**
     * Prepares the file streams for this transfer.
     */
    private void prepareStreams() {
        //Caching as file resource.
        File saveFile = ((FileResource)this.getResource()).file;
        logger.info("Preparing {} streams for file: {}", this.getTransferType(), saveFile.getName()) ;

        if(this.getTransferType() == ResourceTransferType.OUTBOUND) { //Sending
            try {
                inputStream = new FileInputStream(saveFile);
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                EventManager.callEvent(new ResourceTransferErrorEvent(this, e));
                return;
            }
        } else { //Receiving
            //Checking if file already exists.
            if(getResource().isLocal()) {
                getResource().calcNetworkId();
                if(getResource().getNetworkID().equals(getTunnel().getDestination())) {
                    //The file is already stored locally.
                    EventManager.callEvent(new ResourceTransferFinishedEvent(this, "File exists already!"));
                    getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), -2));
                    return;
                }
            }

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

            //Requesting the first chunk.
            getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), 0));
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
     */
    @Override
    public void sendChunk(int chunkId) {
        if(getTransferType().equals(ResourceTransferType.OUTBOUND) && inputStream != null) {
            //Stopping on request.
            if(chunkId < 0) {
                if(chunkId == -1) {
                    //File sent or error.
                    EventManager.callEvent(new ResourceTransferFinishedEvent(FileTransferHandler.this, "File transfer complete!"));
                    return;
                }
                if(chunkId == -2) {
                    //File sent or error.
                    EventManager.callEvent(new ResourceTransferFinishedEvent(FileTransferHandler.this, "File transfer interrupted by destination!"));
                    return;
                }
            }

            //File sending logic.
            try {
                logger.info("Sending file, available bytes: {}", inputStream.available());
                if(inputStream.available() != 0) {
                    //The send buffer.
                    byte[] buffer = new byte[BUFFER_SIZE];
                    int read = inputStream.read(buffer);
                    sent += read;

                    byte[] data = new byte[read];
                    System.arraycopy(buffer, 0, data, 0, read);

                    logger.info("File transfer ({}) - {}%", this.getResource().attributes.get(1), ((float) sent / Long.parseLong(getResource().getInfo().attributes.get(0))) * 100f);
                    sendData(data, chunkId);

                    //Finish if no more bytes available!
                    if(inputStream.available() <= 0) {
                        //File sent fully.
                        EventManager.callEvent(new ResourceTransferFinishedEvent(FileTransferHandler.this, "File transfer complete!"));
                        return;
                    }
                } else {
                    //The file has no data. Sending an empty chunk.
                    sendData(new byte[0], chunkId);
                }
            } catch (IOException e) {
                EventManager.callEvent(new ResourceTransferErrorEvent(FileTransferHandler.this, e));
            }
        }
    }

    private void sendData(byte[] data, int chunkId) {
        ResourceDataMessage message = new ResourceDataMessage(data, getTransferId(), chunkId);
        getTunnel().sendMessage(message);
        EventManager.callEvent(new ResourceTransferDataSentEvent(this, message));
    }

    /**
     * Called when resource data is received.
     * @param dataMessage
     */
    @Override
    public void onResourceMessage(ResourceDataMessage dataMessage) {
        if(getTransferType() == ResourceTransferType.INBOUND && outputStream != null) {
            EventManager.callEvent(new ResourceTransferDataReceivedEvent(this, dataMessage));

            //Caching the used message tunnel.
            if(this.getTunnel() == null)
                this.setTunnel(dataMessage.getNetworkMessage().getTunnel());

            //Adding the data to the data byte[] of the transfer.
            if(dataMessage.getResourceData().length != 0) {
                try {
                    outputStream.write(dataMessage.getResourceData());
                    written += dataMessage.getResourceData().length;
                    logger.info("File chunk received for: {} | {}%", this.getResource().getNetworkID(), ((float)written/(float)fileLength)*100f);
                    if(written >= fileLength) {
                        //File fully received.
                        getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), -1));
                        EventManager.callEvent(new ResourceTransferFinishedEvent(this, "File transfer complete!"));
                    } else {
                        getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), dataMessage.getChunkId() + 1));
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
                EventManager.callEvent(new ResourceTransferFinishedEvent(this, "Empty file transfer!"));
            }
        }
    }
}
