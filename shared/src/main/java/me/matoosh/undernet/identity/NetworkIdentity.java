package me.matoosh.undernet.identity;

import me.matoosh.undernet.p2p.router.data.NetworkID;

import java.io.Serializable;
import java.security.*;

/**
 * Represents the network identity used to connect.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkIdentity implements Serializable {
    /**
     * The public key.
     */
    private PublicKey publicKey;
    /**
     * The private key.
     */
    private PrivateKey privateKey;
    /**
     * The network id.
     */
    private NetworkID networkID;

    /**
     * Default to random identity.
     */
    public NetworkIdentity() {
        try {
            generateKeys();
            networkID = NetworkID.generateFromPublicKey(this.publicKey);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks whether the NetworkIdentity is correct.
     * @return
     */
    public boolean isCorrect() {
        if (networkID == null) {
            return false;
        }
        return true;
    }

    /**
     * Sets the network id.
     * @param id
     */
    public void setNetworkId(NetworkID id) {
        this.networkID = id;
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
    public void generateKeys() throws NoSuchAlgorithmException {
        KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(NetworkID.networkIdLength*8 - 30*8);
        KeyPair generatedKeyPair = keyGen.genKeyPair();
        this.publicKey = generatedKeyPair.getPublic();
        this.privateKey = generatedKeyPair.getPrivate();
    }

    /**
     * Gets the public key.
     * @return
     */
    public PublicKey getPublicKey() {
        return this.publicKey;
    }
    /**
     * Gets the private key.
     * @return
     */
    public PrivateKey getPrivateKey() { return this.privateKey; }

    @Override
    public String toString() {
        return "NetworkIdentity{" +
                "publicKey='" + getPublicKey() + '\'' +
                ", networkID=" + getNetworkId() +
                '}';
    }
}
