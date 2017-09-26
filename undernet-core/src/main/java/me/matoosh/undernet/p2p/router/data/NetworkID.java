package me.matoosh.undernet.p2p.router.data;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

/**
 * Represents a network id.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NetworkID implements Serializable {
    /**
     * The data of the network id.
     */
    public BigInteger data;

    public NetworkID() {

    }
    public NetworkID(String value) {
        this.data = new BigInteger(value, 512);
    }
    public NetworkID(BigInteger id) {
        if(id.toByteArray().length > 65) {
            UnderNet.router.logger.error("Network id has too many bytes.");
            return;
        }
        this.data = id;
    }

    /**
     * Calculates the distrance between this id and an other id.
     * @param other
     * @return
     */
    public BigInteger distanceTo(NetworkID other) {
        return data.xor(other.data);
    }

    /**
     * Returns a random network id.
     * @return
     */
    public static NetworkID random() {
        NetworkID random = new NetworkID();
        random.data = new BigInteger(512, new Random());
        return random;
    }

    /**
     * Serialization
     * @param oos
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos)
            throws IOException {
        oos.writeObject(data);
    }

    /**
     * Deserialization
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        data = (BigInteger)ois.readObject();
    }

    @Override
    public String toString() {
        return "NetworkID{" + data +
                '}';
    }

    /**
     * Gets a SHA-512 hash code from a string.
     * @param str
     * @return
     * @throws NoSuchAlgorithmException
     */
    public static String getHashCodeFromString(String str) {
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            NetworkMessage.logger.error("Couldn't encrypt string with SHA-512 as the algorithm is missing!", e);
        }
        md.update(str.getBytes());
        byte byteData[] = md.digest();

        //convert the byte to hex format method 1
        StringBuffer hashCodeBuffer = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            hashCodeBuffer.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return hashCodeBuffer.toString();
    }
}
