package me.matoosh.undernet.p2p.router.data.filetransfer;

import java.io.File;
import java.io.FileInputStream;

/**
 * Represents a single active file transfer.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FileTransfer {
    /**
     * Id of the transfer.
     */
    public short id;

    /**
     * Info of the file.
     */
    public FileInfo info;

    /**
     * The save stream.
     */
    public FileInputStream saveStream;

    public FileTransfer(File saveTo, short id) {
        this.id = id;
        this.info = new FileInfo(saveTo);
        //Putting the file in the content dir.


        //TODO: Open save stream.
    }
}
