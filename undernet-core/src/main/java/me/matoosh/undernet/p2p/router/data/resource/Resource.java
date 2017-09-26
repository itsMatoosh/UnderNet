package me.matoosh.undernet.p2p.router.data.resource;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Represents a stored resource.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public abstract class Resource implements Serializable{
    /**
     * The network id of this resource.
     */
    public NetworkID networkID;

    /**
     * Calculates the network id of the resource based on its contents.
     */
    public abstract void calcNetworkId();

    /**
     * Serialization
     * @param oos
     * @throws IOException
     */
    public abstract void writeObject(ObjectOutputStream oos)
            throws IOException;

    /**
     * Deserialization
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    public abstract void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException;
}
