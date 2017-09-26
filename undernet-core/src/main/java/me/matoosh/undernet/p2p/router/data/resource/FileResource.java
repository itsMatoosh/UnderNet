package me.matoosh.undernet.p2p.router.data.resource;

import java.io.File;

import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.filetransfer.FileInfo;

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
     * Creates a new file resource given file.
     * @param file
     */
    public FileResource(File file) {
        this.fileInfo = new FileInfo(file);
        calcNetworkId();
    }

    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {
        networkID = new NetworkID(NetworkID.getHashCodeFromString(fileInfo.fileName));
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
}
