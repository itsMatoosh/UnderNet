package me.matoosh.undernet.p2p.router.data.resource.transfer;

import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataReceivedEvent;
import me.matoosh.undernet.event.resource.transfer.ResourceTransferDataSentEvent;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.ResourceTransferControlMessage;
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
public class FileResourceTransferHandler extends ResourceTransferHandler<FileResource> {
    /**
     * The standard buffer size for file chunks (256kb)
     */
    public static final int BUFFER_SIZE = 1024 * 256;
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(FileResourceTransferHandler.class);
    private final double NANOS_PER_SECOND = 1000000000.0;
    private final double BYTES_PER_MIB = 1024 * 1024;

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
     * Buffer for file operations.
     */
    private byte[] buffer;
    /**
     * The final length of the received file.
     */
    private long fileLength;
    /**
     * The number of bytes written to memory from the received chunks.
     */
    private int written = 0;
    /**
     * The number of bytes saved to disk from the received chunks.
     */
    private int saved = 0;
    /**
     * The amount of bytes sent.
     */
    private int sent = 0;
    /**
     * The time the transfer started.
     */
    private long startTime;

    public FileResourceTransferHandler(FileResource resource, ResourceTransferType fileTransferType, MessageTunnel tunnel, int transferId, Router router) {
        super(resource, fileTransferType, tunnel, transferId, router);

        this.fileLength = Long.parseLong(resource.getInfo().attributes.get(0));
    }

    /**
     * Prepares the file transfer.
     */
    @Override
    public void prepare() {
        //Caching as file resource.
        File file = this.getResource().file;
        logger.info("Preparing {} streams for file: {}", this.getTransferType(), file.getName());

        if (this.getTransferType() == ResourceTransferType.OUTBOUND) {
            //Sending
            try {
                buffer = new byte[BUFFER_SIZE];
                sent = 0;
                shouldStopSending = false;
                inputChannel = new FileInputStream(file).getChannel();
                inputBuffer = inputChannel.map(FileChannel.MapMode.READ_ONLY, 0, inputChannel.size());
            } catch (FileNotFoundException e) { //File doesn't exist.
                //Calling a transfer error.
                callError(e);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            //Receiving
            //Checking if file already exists.
            written = 0;
            saved = 0;
            if (getResource().isLocal()) {
                getResource().calcNetworkId();
                if (getResource().getNetworkID().equals(getTunnel().getDestination())) {
                    //The file is already stored locally.
                    getTunnel().sendMessage(new ResourceTransferControlMessage(this.getTransferId(), -2));
                    this.close();
                    return;
                }
            }

            //Creating or replacing the file.
            buffer = new byte[BUFFER_SIZE * 16];
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
            getTunnel().sendMessage(new ResourceTransferControlMessage(this.getTransferId(), 0));
        }
    }

    @Override
    void doStartSending() {
        if (getTransferType().equals(ResourceTransferType.OUTBOUND) && inputChannel != null) {
            //File sending logic.
            try {
                startTime = System.nanoTime();
                while (inputBuffer != null && inputBuffer.capacity() - inputBuffer.position() > 0) {
                    if (!inputChannel.isOpen()) break;

                    //The send buffer.
                    int read = BUFFER_SIZE;
                    if (inputBuffer.capacity() - inputBuffer.position() <= BUFFER_SIZE) {
                        read = inputBuffer.capacity() - inputBuffer.position();
                    }
                    inputBuffer.get(buffer, 0, read);
                    sent += read;

                    double speedInMBps = NANOS_PER_SECOND / BYTES_PER_MIB * sent / (System.nanoTime() - startTime + 1);
                    logger.info("Sending file: {} | {}% ({}MB/s)", this.getResource().attributes.get(1), ((float) sent / Long.parseLong(getResource().getInfo().attributes.get(0))) * 100f, speedInMBps);
                    sendData(buffer);
                    setLastMessageTime(System.currentTimeMillis());
                }
            }
            catch (Exception e) {
                callError(e);
            }
            finally {
                //File sent fully.
                this.close();
            }
        }
    }

    /**
     * Sends file chunk.
     *
     * @param data
     */
    private void sendData(byte[] data) {
        ResourceDataMessage message = new ResourceDataMessage(data, getTransferId());
        getTunnel().sendMessage(message);
        EventManager.callEvent(new ResourceTransferDataSentEvent(this, message));
    }

    @Override
    void doStopSending() {
        close();
    }

    /**
     * Closes the file streams.
     */
    @Override
    public void onClose() {
        logger.info("Closing {} streams for file {}", this.getTransferType(), this.getResource().file);
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
     * Called when resource data is received.
     *
     * @param dataMessage
     */
    @Override
    public void doDataReceived(ResourceDataMessage dataMessage) {
        if(outputStream == null) return;
        if(startTime == 0) startTime = System.nanoTime();
        if (this.getTunnel() == null)
            this.setTunnel(dataMessage.getNetworkMessage().getTunnel());

        EventManager.callEvent(new ResourceTransferDataReceivedEvent(this, dataMessage));

        try {
            if (dataMessage.getResourceData().length != 0) {
                //Saving received chunks
                byte[] chunk = dataMessage.getResourceData();
                written += chunk.length;
                double speedInMBps = NANOS_PER_SECOND / BYTES_PER_MIB * written / (System.nanoTime() - startTime + 1);
                System.out.println();
                logger.info("Receiving file: {} | {}% ({}MB/s)", this.getResource().attributes.get(1), ((float) written / (float) fileLength) * 100f, speedInMBps);

                //add to 4mb buffer
                System.arraycopy(chunk, 0, buffer, saved, chunk.length);
                saved += chunk.length;
                if (buffer.length - saved < BUFFER_SIZE || written >= fileLength) {
                    //save and clear buffer
                    outputStream.write(buffer, 0, saved);
                    saved = 0;
                }


                if (written >= fileLength) {
                    this.close();
                }
            } else {
                //Empty chunk, ending the transfer and closing the file.
                logger.info("Empty chunk received for: {}, ending the transfer...", this.getResource().attributes.get(1));

                //File fully received.
                this.close();
            }
        } catch (Exception e) {
            callError(e);
        }
    }

    @Override
    public void onTransferControlMessage(int message) {
        if(message == 0) {
            this.startSending();
        }
        else if(message == -2) {
            this.stopSending();
        }
    }

    @Override
    public void onError(Exception e) {
        //Removing file.
        if (getTransferType() == ResourceTransferType.INBOUND) {
            File f = this.getResource().file;
            if (f != null && f.exists()) try {
                Files.delete(f.toPath());
            } catch (IOException ignored) {
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
