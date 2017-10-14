package me.matoosh.undernet.p2p.router.data.filetransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.resource.FileResource;

/**
 * Manages the file transfers.
 * Created by Mateusz Rębacz on 27.09.2017.
 */

public class FileTransferManager extends Manager {
    /**
     * The currently active file transfers.
     */
    public ArrayList<FileTransfer> transfers = new ArrayList<>();

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
    public void prepareFileTranfer(FileResource resource, Node recipient) {
        //Creating a new file transfer instance.
        FileTransfer transfer = new FileTransfer(resource, recipient, FileTransferType.OUTBOUND);
        transfers.add(transfer);
    }

    /**
     * Requests a file transfer with id from a neighboring node.
     * @param receivedFrom
     * @param resource
     */
    public void requestFileTransfer(Node receivedFrom, FileResource resource) {
        logger.info("Requesting the transfer " + resource.networkID + " from " + receivedFrom);

        //Caching a new transfer instance.
        transfers.add(new FileTransfer(resource, receivedFrom, FileTransferType.INBOUND));

        //Sending a new FileRequest message.
        receivedFrom.send(new NetworkMessage(MsgType.FILE_REQ, new FileTransferRequestMessage(resource.networkID)));
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {

    }

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
        //Message handler.
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
        if(messageReceivedEvent.message.msgId == MsgType.FILE_REQ.ordinal()) { //File request received.
            //Deserializing msg.
            FileTransferRequestMessage requestMsg = (FileTransferRequestMessage)NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());

            //A file was requested from this node. Checking if the requested transfer has been prepared.
            for (final FileTransfer transfer :
                    transfers) {
                if(transfer.id == requestMsg.transferId) {
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
        } else if(messageReceivedEvent.message.msgId == MsgType.FILE_CHUNK.ordinal()) { //File chunk received.
            //Deserializing msg.
            FileChunk fileChunk = (FileChunk) NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());

            //A file was requested from this node. Checking if the requested transfer has been prepared.
            for (final FileTransfer transfer :
                    transfers) {
                if(transfer.id == fileChunk.transferId) { //Locating the right file transfer.
                    //Running chunk received callback.
                    transfer.onChunkReceived(fileChunk);
                    return;
                }
            }
        }
    }
}
