package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.resource.transfer.FileTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

import java.io.*;
import java.util.HashMap;

/**
 * Represents a stored file resource.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileResource extends Resource {
    /**
     * The file.
     */
    public transient File file;

    /**
     * Creates a new file resource given file.
     * @param file
     */
    public FileResource(Router router, File file) {
        super(router);
        this.file = file;
        updateAttributes();
    }

    /**
     * Calculates the network id of the resource based on its name.
     */
    @Override
    public void calcNetworkId() {
        if(this.getNetworkID() == null) {
            this.setNetworkID(NetworkID.generateFromString(getInfo().attributes.get(1)));
        }
    }

    /**
     * Copies the file if its not in the content directory.
     */
    public boolean copyToContent() {
        if(this.getNetworkID() == null) {
            calcNetworkId();
        }
        if(!file.toString().startsWith(UnderNet.fileManager.getContentFolder().toString())) {
            if(!file.exists()) return false;
            if(!file.canRead()) return false;
            if(file.isHidden()) return false;
            if(file.isDirectory()) return false;

            InputStream is = null;
            OutputStream os = null;
            try {
                is = new FileInputStream(file);
                os = new FileOutputStream(UnderNet.fileManager.getContentFolder() + "/" + this.getInfo().attributes.get(1));
                byte[] buffer = new byte[1024];
                int length;
                while ((length = is.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
            } catch (IOException e) {
                ResourceManager.logger.error("An error occurred copying file: " + file.toString() + " to the content directory!", e);
                return false;
            } finally {
                try {
                    is.close();
                    os.close();
                } catch (IOException e) {
                    ResourceManager.logger.error("An error occurred while closing the copy streams for file: " + file.toString() + "!", e);
                    return false;
                }
            }
        }

        //Updating the path.
        this.file = new File(UnderNet.fileManager.getContentFolder() + "/" + this.getInfo().attributes.get(1));

        return true;
    }

    /**
     * Returns the type of the resource. E.g file resource.
     *
     * @return
     */
    @Override
    public ResourceType getResourceType() {
        return ResourceType.FILE;
    }

    @Override
    void updateAttributes() {
        this.attributes = new HashMap<>();
        this.attributes.put(0, Long.toString(this.file.length()));
        this.attributes.put(1, this.file.getName());
    }

    /**
     * Gets the transfer handler.
     * @param resourceTransferType
     * @param messageDirection
     * @param recipient
     * @param router
     * @return
     */
    @Override
    public ResourceTransferHandler getTransferHandler(ResourceTransferType resourceTransferType, NetworkMessage.MessageDirection messageDirection, NetworkID recipient, Router router) {
        byte transferId;
        if(resourceTransferType == ResourceTransferType.OUTBOUND) {
            transferId = (byte)router.resourceManager.outboundHandlers.size();
        } else {
            transferId = (byte)router.resourceManager.inboundHandlers.size();
        }
        return new FileTransferHandler(this, resourceTransferType, messageDirection, recipient, transferId, router);
    }

    /**
     * Clears the file resource, deleting the underlying file.
     */
    @Override
    public void clear() {
        if(file.exists()) {
            file.delete();
        }
    }

    @Override
    public String toString() {
        return "FileResource{" +
                "networkID=" + this.getNetworkID() +
                ", fileInfo=" + getInfo().attributes +
                '}';
    }

    /**
     * Checks whether the file is within the content folder.
     * @return
     */
    @Override
    public boolean isLocal() {
        if(this.file == null) {
            this.file = new File(UnderNet.fileManager.getContentFolder() + "/" + getInfo().attributes.get(1));
        }
        if(this.file != null && this.file.exists()) {
            return true;
        }
        return false;
    }
}
