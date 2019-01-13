package me.matoosh.undernet.p2p.router.data.resource;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.MessageTunnel;
import me.matoosh.undernet.p2p.router.data.resource.transfer.FileTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferHandler;
import me.matoosh.undernet.p2p.router.data.resource.transfer.ResourceTransferType;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
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
     *
     * @param file
     */
    public FileResource(Router router, File file) {
        super(router);
        this.file = file;
        updateAttributes();

        //Calculate checksum if file is stored.
        if(file.length() > 0) {
            calcNetworkId();
        }
    }

    /**
     * Calculates the network id of the resource based on its name.
     */
    @Override
    public void calcNetworkId() {
        if (this.getNetworkID() == null) {
            this.setNetworkID(new NetworkID(calcChecksum()));
        }
    }

    /**
     * Calculates checksum of the file.
     */
    public byte[] calcChecksum() {
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            ByteBuffer checksum = ByteBuffer.allocate(65);
            checksum.put(org.apache.commons.codec.digest.DigestUtils.sha512(fis));
            fis.close();
            return checksum.array();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Copies the file if its not in the content directory.
     */
    public boolean copyToContent() {
        if (this.getNetworkID() == null) {
            calcNetworkId();
        }
        if (!file.toString().startsWith(UnderNet.fileManager.getContentFolder().toString())) {
            //Make sure file is accessible.
            if (!file.exists()) return false;
            if (!file.canRead()) return false;
            if (file.isHidden()) return false;
            if (file.isDirectory()) return false;

            //Destination
            File destination = new File(UnderNet.fileManager.getContentFolder() + "/" + this.getInfo().attributes.get(1));
            if (destination.exists()) {
                destination.delete();
            }

            //Copy
            try {
                Files.copy(file.toPath(), destination.toPath());
            } catch (IOException e) {
                ResourceManager.logger.error("Couldn't copy file to content directory!", e);
                return false;
            }

            //Updating the path.
            this.file = destination;
        }

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
     *
     * @param resourceTransferType
     * @param tunnel
     * @param router
     * @param transferId the transfer id of the transfer, will only be used for INBOUND transfers.
     * @return
     */
    @Override
    public ResourceTransferHandler getTransferHandler(ResourceTransferType resourceTransferType, MessageTunnel tunnel, int transferId, Router router) {
        if (resourceTransferType == ResourceTransferType.OUTBOUND) {
            router.resourceManager.lastTransferId++;
            transferId = router.resourceManager.lastTransferId;
        }
        return new FileTransferHandler(this, resourceTransferType, tunnel, transferId, router);
    }

    /**
     * Clears the file resource, deleting the underlying file.
     */
    @Override
    public void clear() {
        if (file.exists()) {
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
     *
     * @return
     */
    @Override
    public boolean isLocal() {
        if (this.file == null) {
            this.file = new File(UnderNet.fileManager.getContentFolder() + "/" + getInfo().attributes.get(1));
        }
        if (this.file != null && this.file.exists()) {
            return true;
        }
        return false;
    }
}
