package me.matoosh.undernet.p2p.router.data;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;
import uk.org.bobulous.java.crypto.keccak.FIPS202;
import uk.org.bobulous.java.crypto.keccak.KeccakSponge;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.interfaces.ECPublicKey;
import java.util.Base64;

import static me.matoosh.undernet.p2p.router.Router.logger;

/**
 * Represents a network id.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NetworkID implements Serializable {
    /**
     * The length of a network id in bytes.
     */
    public static final int NETWORK_ID_LENGTH = 65;

    /**
     * The data of the network id.
     */
    private byte[] data;

    public NetworkID() {
    }

    /**
     * Creates a network id given its text representation.
     *
     * @param value
     */
    public NetworkID(String value) {
        this.data = getByteValue(value);
    }

    /**
     * Creates a network id given its data.
     *
     * @param id
     */
    public NetworkID(byte[] id) {
        setData(id);
    }

    /**
     * Checks whether the network id is valid.
     *
     * @return
     */
    public boolean isValid() {
        if (data.length == NETWORK_ID_LENGTH) {
            return true;
        } else {
            logger.error("Network ID is not " + NETWORK_ID_LENGTH + " bytes long! Current number of bytes: " + data.length);
            return false;
        }
    }

    /**
     * Calculates the distance between this id and an other id.
     *
     * @param other
     * @return
     */
    public byte[] distanceTo(NetworkID other) {
        byte[] output = new byte[NETWORK_ID_LENGTH];
        int i = 0;
        for (byte b : other.data) {
            output[i] = (byte) (b ^ this.data[i++]);
        }
        return output;
    }

    /**
     * Serialization
     *
     * @param oos
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.write(data);
    }

    /**
     * Deserialization
     *
     * @param ois
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private void readObject(ObjectInputStream ois)
            throws ClassNotFoundException, IOException {
        data = new byte[NETWORK_ID_LENGTH];
        ois.read(data);
    }

    @Override
    public String toString() {
        return "NID:{" + getStringValue(this.data) +
                '}';
    }

    /**
     * Returns the value of the given id as a string.
     *
     * @return
     */
    public static String getStringValue(byte[] data) {
        return Base64.getEncoder().encodeToString(data);
    }

    /**
     * Returns the value of the id as a string.
     *
     * @return
     */
    public String getStringValue() {
        return getStringValue(this.data);
    }

    /**
     * Gets the network id data.
     *
     * @return
     */
    public byte[] getData() {
        return this.data;
    }

    /**
     * Gets the public key associated with the network id.
     *
     * @return
     */
    public ECPublicKey getPublicKey() {
        try {
            return KeyTools.fromUncompressedPoint(this.data);
        } catch (Exception e) {
            logger.error("Couldn't convert NetworkID: {} into a EC public key!", getStringValue(), e);
            return null;
        }
    }

    /**
     * Sets the network id data.
     *
     * @param data
     */
    public void setData(byte[] data) {
        if (data.length != NETWORK_ID_LENGTH) {
            logger.error("Network ID is not " + NETWORK_ID_LENGTH + " bytes long! Current number of bytes: " + data.length);
            return;
        }
        this.data = data;
    }

    /**
     * Gets data from net id value.
     */
    public static byte[] getByteValue(String value) {
        return Base64.getDecoder().decode(value);
    }

    /**
     * Generates a NetworkID from a public key.
     *
     * @param publicKey
     * @return
     */
    public static NetworkID generateFromPublicKey(ECPublicKey publicKey) {
        byte[] encoded = KeyTools.toUncompressedPoint(publicKey);
        if (encoded.length != NETWORK_ID_LENGTH) {
            logger.warn("Cannot generate network id from {}, length mismatch! Key length: {}, expected: {}", publicKey, encoded.length, NETWORK_ID_LENGTH);
            return null;
        }

        //Extract data from the public key.
        return new NetworkID(encoded);
    }

    /**
     * Generates a NetworkID from a string through Keccak.
     *
     * @param string
     * @return
     */
    public static NetworkID generateFromString(String string) {
        KeccakSponge spongeFunction = FIPS202.ExtendableOutputFunction.SHAKE256.withOutputLength(NETWORK_ID_LENGTH * 8);

        return new NetworkID(spongeFunction.apply(string.getBytes(Charset.forName("UTF-8"))));
    }

    /**
     * Compares two byte arrays of the same size.
     *
     * @param a
     * @param b
     * @return result of comparation; -1 = less than, 0 = equal
     */
    public static int compare(byte[] a, byte[] b) {
        int diff = 0; //How many bytes are different between these arrays.

        //Checking length.
        if (a.length != b.length) {
            return 1;
        }

        //Checking bytes.
        for (int i = 0; i < a.length; i++) {
            if (b[i] < a[i]) {
                diff--;
            } else if (b[i] > a[i]) {
                diff++;
            }
        }

        return diff;
    }

    /**
     * Checks whether the given network id is equal to this one.
     *
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if (obj.getClass() == NetworkID.class) {
            NetworkID other = (NetworkID) obj;

            //Checking length.
            if (this.getData().length != other.getData().length) {
                return false;
            }

            //Checking bytes.
            for (int i = 0; i < this.getData().length; i++) {
                if (this.getData()[i] != other.getData()[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Sends a message to the network id destination
     *
     * @param content
     */
    public void sendMessage(MsgBase content) {
        UnderNet.router.networkMessageManager.sendMessage(content, this);
    }
}
