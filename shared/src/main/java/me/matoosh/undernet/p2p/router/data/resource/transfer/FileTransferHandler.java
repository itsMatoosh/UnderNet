package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataChunkRequest;
import me.matoosh.undernet.p2p.router.data.message.ResourceDataMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

/**
 * Handles file transfers.
 */
public class FileTransferHandler extends ResourceTransferHandler {
    /**
     * Input buffer for sending file chunks.
     */
    private MappedByteBuffer inputBuffer;
    private FileChannel inputChannel;

    /**
     * Output for receiving file chunks.
     */
    private FileOutputStream outputStream;

    /**
     * The final length of the received file.
     */
    private long fileLength;

    /**
     * The standard buffer size for file chunks (32kb)
     */
    public static final int BUFFER_SIZE = 1024 * 32;

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

    public FileTransferHandler(FileResource resource, ResourceTransferType fileTransferType, MessageTunnel tunnel, int transferId, Router router) {
        super(resource, fileTransferType, tunnel, transferId, router);

        this.fileLength = Long.parseLong(resource.getInfo().attributes.get(0));
    }

    /**
     * Prepares the file transfer.
     */
    @Override
    public void prepare() {
        //Caching as file resource.
        File file = ((FileResource)this.getResource()).file;
        logger.info("Preparing {} streams for file: {}", this.getTransferType(), file.getName()) ;

        if(this.getTransferType() == ResourceTransferType.OUTBOUND) { //Sending
            try {
                inputChannel = new FileInputStream(file).getChannel();
                inputBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputChannel.size());
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                callError(e);
                return;
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else { //Receiving
            //Checking if file already exists.
            if(getResource().isLocal()) {
                getResource().calcNetworkId();
                if(getResource().getNetworkID().equals(getTunnel().getDestination())) {
                    //The file is already stored locally.
                    getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), -2));
                    this.close();
                    return;
                }
            }

            //Creating or replacing the file.
            if (file.exists()) {
                file.delete();
            }
            try { //Creating new file.
                file.createNewFile();
                outputStream = new FileOutputStream(file);
            } catch (IOException e) {
                //Calling a transfer error.
               callError(e);
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
    public void onClose() {
        logger.info("Closing {} streams for file {}", this.getTransferType(), ((FileResource)this.getResource()).file);
        try {
            if (inputChannel != null) {
                inputChannel.close();
                inputChannel = null;
                inputBuffer.clear();
                inputBuffer = null;
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
        if(getTransferType().equals(ResourceTransferType.OUTBOUND) && inputChannel != null) {
            //Stopping on request.
            if(chunkId < 0) {
                if(chunkId == -2) {
                    //File sent or error.
                    this.close();
                    return;
                }
            }

            //File sending logic.
            try {
                if(inputBuffer.capacity() - inputBuffer.position() > 0) {
                    //The send buffer.
                    byte[] buffer;
                    if(inputBuffer.capacity() - inputBuffer.position() > BUFFER_SIZE) {
                        buffer = new byte[BUFFER_SIZE];
                        sent += BUFFER_SIZE;
                    } else {
                        buffer = new byte[inputBuffer.capacity() - inputBuffer.position()];
                        sent += inputBuffer.capacity() - inputBuffer.position();
                    }

                    inputBuffer.get(buffer);

                    logger.info("Sending file: {} | {}% ({}kb)", this.getResource().attributes.get(1), ((float) sent / Long.parseLong(getResource().getInfo().attributes.get(0))) * 100f, sent/1024);
                    sendData(buffer, chunkId);

                    //Finish if no more bytes available!
                    if(inputBuffer.capacity() - inputBuffer.position() <= 0) {
                        //File sent fully.
                        this.close();
                    }
                } else {
                    //The file has no data. Sending an empty chunk.
                    sendData(new byte[0], chunkId);
                }
            } catch (Exception e) {
                callError(e);
            }
        }
    }

    /**
     * Sends file chunk.
     * @param data
     * @param chunkId
     */
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
    public void onDataReceived(ResourceDataMessage dataMessage) {
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
                    logger.info("Receiving file: {} | {}% ({}kb)", this.getResource().attributes.get(1), ((float)written/(float)fileLength)*100f, written/1024);
                    if(written >= fileLength) {
                        //File fully received.
                        this.close();
                    } else {
                        getTunnel().sendMessage(new ResourceDataChunkRequest(this.getTransferId(), dataMessage.getChunkId() + 1));
                    }
                } catch (IOException e) {
                    callError(e);
                }
            } else {
                //Empty chunk, ending the transfer and closing the file.
                logger.info("Empty chunk received for: {}, ending the transfer...", this.getResource().attributes.get(1));

                //File fully received.
                this.close();
            }
        }
    }

    @Override
    public void onError(Exception e) {
        //Removing file.
        if(getTransferType() == ResourceTransferType.INBOUND) {
            File f = ((FileResource) this.getResource()).file;
            if (f != null && f.exists()) {
                try {
                    Files.delete(f.toPath());
                } catch (IOException e1) {
                }
            }
        }
    }

    public long getFileLength() {
        return fileLength;
    }

    public int getWritten() {
        return written;
    }

    public int getSent() {
        return sent;
    }
}
