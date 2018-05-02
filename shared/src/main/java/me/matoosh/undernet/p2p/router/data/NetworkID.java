package me.matoosh.undernet.p2p.router.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

import static me.matoosh.undernet.p2p.router.Router.logger;

/**
 * Represents a network id.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NetworkID implements Serializable {
    /**
     * The data of the network id.
     */
    public byte[] data;

    public NetworkID() {

    }
    public NetworkID(String value) {
        this.data = getHashedDataFromString(value);
    }
    public NetworkID(byte[] id) {
        if(id.length > 65) {
            logger.error("Network id has too many bytes.");
            return;
        }
        this.data = id;
    }

    /**
     * Checks whether the network id is valid.
     * @return
     */
    public boolean isValid() {
        if(data.length == 64) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Calculates the distance between this id and an other id.
     * @param other
     * @return
     */
    public byte[] distanceTo(NetworkID other) {
        byte[] output = new byte[64];
        int i = 0;
        for(byte b : other.data) {
          output[i] = (byte)(b ^ this.data[i++]);
        }
        return output;
    }

    /**
     * Returns a random network id.
     * @return
     */
    public static NetworkID random() {
        byte[] data = new byte[64];
        UnderNet.secureRandom.nextBytes(data);
        return new NetworkID(data);
    }

    /**
     * Serialization
     * @param oos
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        if(data == null) {
            logger.error("SSSSS");
        }
        oos.write(data);
    }

    /**
     * Deserialization
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        data = new byte[64];
        ois.read(data);
    }

    @Override
    public String toString() {
        return "NetworkID{" + new String(data) +
                '}';
    }

    /**
     * Gets a SHA-512 hash code from a string.
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static byte[] getHashedDataFromString(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            NetworkMessage.logger.error("Couldn't encrypt string with SHA-512, the algorithm is missing!", e);
        }
        md.update(str.getBytes());

        return md.digest();
    }

    /**
     * Compares two byte arrays of the same size.
     * @param a
     * @param b
     * @return result of comparation; -1 = less than, 0 = equal
     */
    public static int compare(byte[] a, byte[] b) {
        int diff = 0; //How many bytes are different between these arrays.

        int i = 0;
        for (byte ba : a) {
            if (ba>b[i]){
                diff++;
            } else if(ba<b[i]) {
                diff--;
            }
            i++;
        }

        if(diff > 0) {
            return 1;
        } else if(diff < 0) {
            return -1;
        } else {
            return 0;
        }
    }
}
