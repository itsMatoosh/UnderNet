package me.matoosh.undernet.p2p.router.data.resource;

import java.io.File;

/**
 * Represents a stored file resource.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileResource extends Resource {
    /**
     * The file.
     */
    public File file;

    /**
     * Creates a new file resource given file.
     * @param file
     */
    public FileResource(File file) {

    }
}
