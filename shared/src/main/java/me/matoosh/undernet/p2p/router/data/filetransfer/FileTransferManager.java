package me.matoosh.undernet.p2p.router.data.filetransfer;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.ftp.FileTransferErrorEvent;
import me.matoosh.undernet.event.ftp.FileTransferFinishedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Manages the file transfers.
 * Created by Mateusz Rębacz on 27.09.2017.
 */

public class FileTransferManager extends Manager {
    /**
     * The currently active inbound file transfers.
     */
    public ArrayList<FileTransfer> inboundTransfers = new ArrayList<>();
    /**
     * The currently active outbound file transfers.
     */
    public ArrayList<FileTransfer> outboundTransfers = new ArrayList<>();

    /**
     * Executor used for async file transfers.
     */
    public ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * The logger of the manager.
     */
    public static Logger logger = LoggerFactory.getLogger(FileTransferManager.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public FileTransferManager(Router router) {
        super(router);
    }

    /**
     * Prepares a file tranfer for the specified file.
     * @param resource
     */
    public FileTransfer prepareFileTranfer(FileResource resource, Node recipient) {
        //Creating a new file transfer instance.
        FileTransfer transfer = new FileTransfer(resource, recipient, FileTransferType.OUTBOUND);
        outboundTransfers.add(transfer);
        return transfer;
    }

    /**
     * Requests a file transfer with id from a neighboring node.
     * @param receivedFrom
     * @param resource
     */
    public FileTransfer requestFileTransfer(Node receivedFrom, FileResource resource) {
        logger.info("Requesting the transfer " + resource.networkID + " from " + receivedFrom);

        //Caching a new transfer instance.
        FileTransfer transfer = new FileTransfer(resource, receivedFrom, FileTransferType.INBOUND);
        inboundTransfers.add(transfer);

        //Sending a new FileRequest message.
        receivedFrom.send(new NetworkMessage(MsgType.FILE_REQ, new FileTransferRequestMessage(resource.networkID)));
        return transfer;
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {
        EventManager.registerEvent(FileTransferErrorEvent.class);
        EventManager.registerEvent(FileTransferFinishedEvent.class);
    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        //Message handler.
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
        EventManager.registerHandler(this, FileTransferFinishedEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e instanceof ChannelMessageReceivedEvent) {
            ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
            if(messageReceivedEvent.message.msgType == MsgType.FILE_REQ) { //File request received.
                //Deserializing msg.
                FileTransferRequestMessage requestMsg = (FileTransferRequestMessage)messageReceivedEvent.message.content;

                logger.info("Received a file transfer request for: " + requestMsg.transferId);

                //A file was requested from this node. Checking if the requested transfer has been prepared.
                for (final FileTransfer transfer :
                        outboundTransfers) {
                    if(NetworkID.compare(transfer.id.data, requestMsg.transferId.data) == 0) {
                        //Checking if the recipient is the same.
                        if(transfer.recipient == messageReceivedEvent.remoteNode){
                            executor.submit(new Runnable() {
                                @Override
                                public void run() {
                                    transfer.startSending();
                                }
                            });
                        }
                    }
                }
            } else if(messageReceivedEvent.message.msgType == MsgType.FILE_CHUNK) { //File chunk received.
                //Deserializing msg.
                FileChunk fileChunk = (FileChunk) messageReceivedEvent.message.content;

                //A file was requested from this node. Checking if the requested transfer has been prepared.
                for (final FileTransfer transfer :
                        inboundTransfers) {
                    if(NetworkID.compare(transfer.id.data, fileChunk.transferId.data) == 0) { //Locating the right file transfer.
                        //Running chunk received callback.
                        transfer.onChunkReceived(fileChunk);
                        return;
                    }
                }
            }
        } else if(e instanceof FileTransferFinishedEvent) {
            //Removing the transfer from the queue.
            FileTransferFinishedEvent fileTransferFinishedEvent = (FileTransferFinishedEvent)e;
            if(fileTransferFinishedEvent.transfer.fileTransferType == FileTransferType.INBOUND) {
                inboundTransfers.remove(fileTransferFinishedEvent.transfer);
            } else {
                outboundTransfers.remove(fileTransferFinishedEvent.transfer);
            }
            logger.info("File transfer " + fileTransferFinishedEvent.transfer + " finished.");
        }
    }
}
