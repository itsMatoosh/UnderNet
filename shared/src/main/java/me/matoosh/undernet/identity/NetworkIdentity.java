package me.matoosh.undernet.identity;

import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.security.interfaces.ECPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;

/**
 * Represents the network identity used to connect.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkIdentity implements Serializable {

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkIdentity.class);

    /**
     * The public key.
     */
    private ECPublicKey publicKey;
    /**
     * The private key.
     */
    private ECPrivateKey privateKey;
    /**
     * The network id.
     */
    private NetworkID networkID;

    /**
     * Default to random identity.
     */
    public NetworkIdentity() {
        generateKeys();
        networkID = NetworkID.generateFromPublicKey(this.publicKey);

        logger.info("Generated new Network identity: {}", toString());
    }

    /**
     * Network id already specified.
     * @param networkID
     */
    public NetworkIdentity(NetworkID networkID) {
        setNetworkId(networkID);
    }

    /**
     * Checks whether the NetworkIdentity is correct.
     * @return
     */
    public boolean isCorrect() {
        if (networkID == null) {
            return false;
        } else {
            return networkID.isValid();
        }
    }

    /**
     * Sets the network id.
     * @param id
     */
    public void setNetworkId(NetworkID id) {
        this.networkID = id;
        try {
            this.publicKey = networkID.getPublicKey();
        } catch (Exception e) {
            e.printStackTrace();
        }
        this.privateKey = null;
    }
    /**
     * Gets the network id.
     * @return
     */
    public NetworkID getNetworkId() {
        return networkID;
    }


    /**
     * Generates the public and private keypair for the identity.
     */
    public void generateKeys() {
        //Generating keys.
        KeyPair kp = KeyTools.generateKeypair();
        this.publicKey = (ECPublicKey)kp.getPublic();
        this.privateKey = (ECPrivateKey)kp.getPrivate();
    }

    /**
     * Gets the public key.
     * @return
     */
    public ECPublicKey getPublicKey() {
        return this.publicKey;
    }
    /**
     * Gets the private key.
     * @return
     */
    public ECPrivateKey getPrivateKey() { return this.privateKey; }

    @Override
    public String toString() {
        return getNetworkId().toString();
    }

    private void writeObject(java.io.ObjectOutputStream out) throws IOException {
        //write network identity identifier
        out.write(KeyTools.toUncompressedPoint(this.getPublicKey()));
        byte[] privateKeyData = this.getPrivateKey().getEncoded();
        out.write(privateKeyData);
    }
    private void readObject(java.io.ObjectInputStream in) throws Exception {
        //read public key
        byte[] publicKeyData = new byte[NetworkID.NETWORK_ID_LENGTH];
        in.read(publicKeyData);
        this.publicKey = KeyTools.fromUncompressedPoint(publicKeyData);

        //read private key
        KeyFactory ecKeyFac = KeyFactory.getInstance("EC", "BC");
        byte[] privateKeyData = new byte[591];
        in.read(privateKeyData);
        PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(privateKeyData);
        this.privateKey = (ECPrivateKey) ecKeyFac.generatePrivate(pkcs8EncodedKeySpec);

        //restore network id
        networkID = NetworkID.generateFromPublicKey(this.publicKey);
    }
}
