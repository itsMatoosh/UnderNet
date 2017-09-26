package me.matoosh.undernet.p2p.router.data.resource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Represents a flag resource.
 * Flag resources have a set expiration and can contain routing information.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class FlagResource extends Resource {
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
