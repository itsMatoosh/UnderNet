package me.matoosh.undernet.p2p.router.data;

import me.matoosh.undernet.UnderNet;
import uk.org.bobulous.java.crypto.keccak.KeccakSponge;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.nio.charset.Charset;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import static me.matoosh.undernet.p2p.router.Router.logger;

/**
 * Represents a network id.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NetworkID implements Serializable {
    /**
     * The length of a network id.
     */
    public static int networkIdLength = 100;

    /**
     * The data of the network id.
     */
    private byte[] data;

    /**
     * The public key associated with this network id.
     */
    private PublicKey publicKey;

    public NetworkID() { }
    /**
     * Creates a network id given its text representation.
     * @param value
     */
    public NetworkID(String value) {
        this.data = getByteValue(value);
    }
    /**
     * Creates a network id given its data.
     * @param id
     */
    public NetworkID(byte[] id) {
        setData(id);
    }

    /**
     * Checks whether the network id is valid.
     * @return
     */
    public boolean isValid() {
        if(data.length == networkIdLength) {
            return true;
        } else {
            logger.error("Network ID is not " + networkIdLength + " bytes long! Current number of bytes: " + data.length);
            return false;
        }
    }

    /**
     * Calculates the distance between this id and an other id.
     * @param other
     * @return
     */
    public byte[] distanceTo(NetworkID other) {
        byte[] output = new byte[networkIdLength];
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
        byte[] data = new byte[networkIdLength];
        UnderNet.secureRandom.nextBytes(data);
        return new NetworkID(data);
    }

    /**
     * Serialization
     * @param oos
     * @throws IOException
     */
    private void writeObject(ObjectOutputStream oos) throws IOException {
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
        data = new byte[networkIdLength];
        ois.read(data);
    }

    @Override
    public String toString() {
        return "NetworkID{" + getStringValue(this.data) +
                    '}';
    }

    /**
     * Returns the value of the given id as a string.
     * @return
     */
    public static String getStringValue(byte[] data) {
        return new sun.misc.BASE64Encoder().encode(data);
    }

    /**
     * Returns the value of the id as a string.
     * @return
     */
    public String getStringValue() {
        return getStringValue(this.data);
    }
    /**
     * Gets the network id data.
     * @return
     */
    public byte[] getData() {
        return this.data;
    }
    /**
     * Gets the public key associated with the network id.
     * @return
     */
    public PublicKey getPublicKey() {
        if(this.publicKey != null) {
            return this.publicKey;
        } else {
            try {
                this.publicKey = KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(this.getData()));
            } catch (InvalidKeySpecException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            return this.publicKey;
        }
    }
    /**
     * Sets the network id data.
     * @param data
     */
    public void setData(byte[] data) {
        if(data.length != networkIdLength) {
            logger.error("Network ID is not " + networkIdLength + " bytes long! Current number of bytes: " + data.length);
            return;
        }
        this.data = data;
    }

    /**
     * Gets data from net id value.
     */
    public static byte[] getByteValue(String value) {
        try {
            return new sun.misc.BASE64Decoder().decodeBuffer(value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Generates a NetworkID from a public key.
     * @param publicKey
     * @return
     */
    public static NetworkID generateFromPublicKey(PublicKey publicKey) {
        if(publicKey.getEncoded().length != networkIdLength) {
            logger.warn("Cannot generate network id from {}, length mismatch!", publicKey);
            return null;
        }

        //Extract data from the public key.
        return new NetworkID(publicKey.getEncoded());
    }

    /**
     * Generates a NetworkID from a string through Keccak.
     * @param string
     * @return
     */
    public static NetworkID generateFromString(String string) {
        KeccakSponge spongeFunction = new KeccakSponge(40, 160, "", 800);

        return new NetworkID(spongeFunction.apply(5, string.getBytes(Charset.forName("UTF-8"))));
    }

    /**
     * Compares two byte arrays of the same size.
     * @param a
     * @param b
     * @return result of comparation; -1 = less than, 0 = equal
     */
    public static int compare(byte[] a, byte[] b) {
        int diff = 0; //How many bytes are different between these arrays.

        //Checking length.
        if(a.length != b.length) {
            return 1;
        }

        //Checking bytes.
        for (int i = 0; i < a.length; i++) {
            if(b[i] < a[i]) {
                diff--;
            } else if(b[i] > a[i]) {
                diff++;
            }
        }

        return diff;
    }

    /**
     * Checks whether the given network id is equal to this one.
     * @param obj
     * @return
     */
    @Override
    public boolean equals(Object obj) {
        if(obj.getClass() == NetworkID.class) {
            NetworkID other = (NetworkID)obj;

            //Checking length.
            if(this.getData().length != other.getData().length) {
                return false;
            }

            //Checking bytes.
            for (int i = 0; i < this.getData().length; i++) {
                if(this.getData()[i] != other.getData()[i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }
}
