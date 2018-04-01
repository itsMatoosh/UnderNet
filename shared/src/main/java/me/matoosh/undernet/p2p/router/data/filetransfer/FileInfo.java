package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.io.File;
import java.io.Serializable;

/**
 * Contains information about a file.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileInfo implements Serializable {
    /**
     * The name of the file.
     */
    public String fileName;
    /**
     * The length of the file.
     */
    public long fileLength;

    /**
     * Creates a file info given file.
     * Creates a file info given file.
     * @param file
     */
    public FileInfo(File file) {
        this.fileName = file.getName();
        this.fileLength = file.length();
    }

    @Override
    public String toString() {
        return "FileInfo{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                '}';
    }
}
