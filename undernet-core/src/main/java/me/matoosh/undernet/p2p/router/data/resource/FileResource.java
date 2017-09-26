package me.matoosh.undernet.p2p.router.data.resource;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

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

    /**
     * Calculates the network id of the resource based on its contents.
     */
    @Override
    public void calcNetworkId() {

    }

    /**
     * Serialization
     *
     * @param oos
     * @throws IOException
     */
    @Override
    public void writeObject(ObjectOutputStream oos) throws IOException {

    }

    /**
     * Deserialization
     *
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    @Override
    public void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {

    }
}
