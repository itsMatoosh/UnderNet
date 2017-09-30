package me.matoosh.undernet.p2p.router.data.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.Callable;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileInfo;
import me.matoosh.undernet.p2p.router.data.message.ResourcePushMessage;

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
        networkID = new NetworkID(NetworkID.getHashCodeFromString(fileInfo.fileName));
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
    public Callable onPush(ResourcePushMessage msg, final Node pushTo) {
        return new Callable() {
            @Override
            public Object call() throws Exception {
                //Preparing a file transfer to the pushTo node.
                UnderNet.router.fileTransferManager.prepareFileTranfer(FileResource.this, pushTo);
                return null;
            }
        };
    }

    /**
     * Called after the resource push has been received.
     *
     * @param msg
     * @param receivedFrom
     */
    @Override
    public Callable onPushReceive(ResourcePushMessage msg, Node receivedFrom) {
        //TODO: Request the file trasnfer.
        return null;
    }
}
