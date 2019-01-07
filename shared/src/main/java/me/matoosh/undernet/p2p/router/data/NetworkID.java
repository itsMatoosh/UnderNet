package me.matoosh.undernet.p2p.router.data;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigInteger;
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

    /**
     * The big integer value of data.
     * Used for distance measurement.
     */
    private BigInteger bigIntegerValue;

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
            logger.error("Network ID is not {} bytes long! Current number of bytes: {}", NETWORK_ID_LENGTH, data.length);
            return false;
        }
    }

    /**
     * Calculates the distance between this id and an other id.
     *
     * @param other
     * @return
     */
    public BigInteger distanceTo(NetworkID other) {
        BigInteger selfValue = getBigIntegerValue();
        BigInteger otherValue = other.getBigIntegerValue();

        return selfValue.subtract(otherValue).abs();
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
        return "NID:{" + getStringValue() + '}';
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
     * Returns the big integer value of the network id.
     * Used for distance calculations.
     * @return
     */
    public BigInteger getBigIntegerValue() {
        if(bigIntegerValue == null) {
            bigIntegerValue = new BigInteger(this.getData());
        }
        return bigIntegerValue;
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
