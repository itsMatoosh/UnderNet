package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.util.ArrayList;

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
     * Last id given to a tranfer.
     */
    private short lastId;

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
        FileTransfer transfer = new FileTransfer(resource, lastId, recipient);
        transfers.add(transfer);
        lastId++;
        if((lastId + 1000) == Short.MAX_VALUE) {
            lastId = 0;
        }
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
        if(messageReceivedEvent.message.msgId == MsgType.FILE_REQ.ordinal()) {
            //Deserializing msg.
            FileTransferRequestMessage requestMsg = (FileTransferRequestMessage)NetworkMessage.deserializeMessage(messageReceivedEvent.message.data.array());

            //A file was requested from this node. Checking if the requested transfer has been prepared.
            for (FileTransfer transfer :
                    transfers) {
                if(transfer.id == requestMsg.transferId) {
                    //Checking if the recipient is the same.
                    if(transfer.recipient == messageReceivedEvent.remoteNode){
                        //TODO: Start the transfer.
                    }
                }
            }
        }
    }
}
