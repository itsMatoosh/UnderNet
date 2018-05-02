package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.ftp.FileTransferFinishedEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileInfo;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileTransfer;
import me.matoosh.undernet.p2p.router.data.message.ResourceMessage;

import java.io.*;

/**
 * Represents a stored file resource.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileResource extends Resource {
    /**
     * Information about the file.
     */
    public FileInfo fileInfo;

    /**
     * The file.
     */
    private transient File file;

    /**
     * The file transfer of this resource.
     */
    private transient FileTransfer transfer;

    /**
     * Creates a new file resource given file.
     * @param file
     */
    public FileResource(File file) {
        this.fileInfo = new FileInfo(file);
        this.file = file;
        calcNetworkId();
        copyToContent();
    }

    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {
        networkID = new NetworkID(NetworkID.getHashedDataFromString(fileInfo.fileName));
    }

    /**
     * Makes sure the file is inside of the content dir.
     */
    private void copyToContent() {
        if(!file.toString().startsWith(UnderNet.fileManager.getContentFolder().toString())) {
            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(UnderNet.fileManager.getContentFolder() + "/" + file.getName());
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException e) {
                ResourceManager.logger.error("An error occured copying file: " + file.toString() + " to the content directory!", e);
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    ResourceManager.logger.error("An error occured while closing the copy streams for file: " + file.toString() + "!", e);
                }
            }
        }
    }

    /**
     * Returns the type of the resource. E.g file resource.
     *
     * @return
     */
    @Override
    public byte getResourceType() {
        return 0;
    }

    /**
     * Called before the resource is pushed.
     *
     * @param msg
     * @param pushTo
     */
    @Override
    public void onPushSend(ResourceMessage msg, final Node pushTo) {
        //Preparing a file transfer to the pushTo node.
        UnderNet.router.fileTransferManager.prepareFileTranfer(FileResource.this, pushTo);
    }

    /**
     * Called after the resource push has been received.
     *
     * @param msg
     * @param receivedFrom
     */
    @Override
    public void onPushReceive(ResourceMessage msg, Node receivedFrom) {
        //Requesting the file trasnfer.
        transfer = UnderNet.router.fileTransferManager.requestFileTransfer(receivedFrom, (FileResource)msg.resource);
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                FileTransferFinishedEvent transferFinishedEvent = (FileTransferFinishedEvent)e;
                if(transferFinishedEvent.transfer == FileResource.this.transfer) {
                    //Transfer of the resource has finished. The resource is ready to push.
                    onPushReady();
                }
            }
        }, FileTransferFinishedEvent.class);

    }

    @Override
    public void onPullSend(ResourceMessage msg, Node pullFrom) {
        //File resource won't ever be pulled directly.
    }

    @Override
    public void onPullReceived(ResourceMessage msg, Node receivedFrom) {
        //File resource won't ever be pulled directly.
    }

    @Override
    public String toString() {
        return "FileResource{" +
                "networkID=" + networkID +
                ", fileInfo=" + fileInfo +
                '}';
    }
}
