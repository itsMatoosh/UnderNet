package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyAgreement;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Represents a message tunnel.
 * A message tunnel contains information about the other node and its public key.
 * The public key will be used to encrypt the messages so that only the other node can decrypt them.
 */
public class MessageTunnel {
    //This part is present on all the part taking nodes. Identifies the tunnel.
    /**
     * The network id of the origin node.
     */
    private NetworkID origin;
    /**
     * The network id of the other.
     */
    private NetworkID destination;

    /**
     * The next neighboring node to the destination.
     */
    private Node nextNode;
    /**
     * The next neighboring node to the origin.
     */
    private Node previousNode;

    //This part is present only on the receiving ends of the tunnel.
    /**
     * The public key of the other.
     * Used by self to encrypt messages for the other side of the tunnel.
     */
    private PublicKey destinationPublicKey;

    /**
     * The shared secret.
     * Calculated after the public keys are exchanged.
     */
    private byte[] sharedSecret;

    /**
     * The shared symmetric key used to encrypt messages.
     * Derived from the shared secret and self and other public keys.
     */
    private SecretKeySpec derivedSymmetricKey;

    //Messages
    /**
     * The list of messages awaiting sending.
     */
    public ArrayList<NetworkMessage> messageQueue = new ArrayList<NetworkMessage>();

    /**
     * The secure random generator.
     */
    private static SecureRandom secureRandom = new SecureRandom();

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(MessageTunnel.class);

    /**
     * Creates a message tunnel.
     * @param destination
     * @param origin
     */
    public MessageTunnel(NetworkID origin, NetworkID destination) {
        this.destination = destination;
        this.origin = origin;
    }

    public PublicKey getOtherPublicKey() {
        return destinationPublicKey;
    }
    public void setOtherPublicKey(PublicKey publicKey) {
        this.destinationPublicKey = publicKey;
    }
    /**
     * Calculates the shared secret key of the tunnel.
     */
    public void calcSharedSecret() {
        try {
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", KeyTools.KEYGEN_ALGORITHM_PROVIDER);
            keyAgreement.init(Node.self.getIdentity().getPrivateKey()); //Self private key.
            keyAgreement.doPhase(getOtherPublicKey(), true);
            sharedSecret = keyAgreement.generateSecret();

            // Derive a key from the shared secret and both public keys
            MessageDigest hash = MessageDigest.getInstance("SHA-256");
            hash.update(sharedSecret);
            // Simple deterministic ordering
            List<ByteBuffer> keys = Arrays.asList(ByteBuffer.wrap(Node.self.getIdentity().getPublicKey().getEncoded()), ByteBuffer.wrap(getOtherPublicKey().getEncoded()));
            Collections.sort(keys);
            hash.update(keys.get(0));
            hash.update(keys.get(1));

            byte[] derivedKey = hash.digest();

            derivedSymmetricKey = new SecretKeySpec(derivedKey, 0, 16, "AES");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * Returns the shared secret of the tunnel.
     * @return
     */
    public SecretKeySpec getSymmetricKey() {
        return derivedSymmetricKey;
    }

    /**
     * Gets the other.
     * @return
     */
    public NetworkID getDestination() {
        return this.destination;
    }
    /**
     * Gets the origin.
     * @return
     */
    public NetworkID getOrigin() {return this.origin;}

    /**
     * Gets the previous node.
     * @return
     */
    public Node getPreviousNode() {return this.previousNode; }
    /**
     * Gets the next node.
     * @return
     */
    public Node getNextNode() {return this.nextNode; }
    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }
    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Encrypts bytes with the shared symmetric key.
     * @param message
     */
    public void encryptMsgSymmetric(NetworkMessage message) {
        try {
            byte[] clean = message.data.array();

            //Generating random IV.
            int ivSize = 16;
            byte[] iv = new byte[ivSize];
            secureRandom.nextBytes(iv);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            //Encrypting
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, getSymmetricKey(), ivParameterSpec);
            byte[] encrypted = cipher.doFinal(clean);

            //Combine IV and the encrypted part.
            byte[] encryptedIVAndData = new byte[ivSize + encrypted.length];
            System.arraycopy(iv, 0, encryptedIVAndData, 0, ivSize);
            System.arraycopy(encrypted, 0, encryptedIVAndData, ivSize, encrypted.length);

            message.data = ByteBuffer.wrap(encryptedIVAndData);
        } catch (Exception e) {
            logger.error("Failed to encrypt {}", this, e);
        }
    }

    /**
     * Decrypts bytes with the shared symmetric key.
     * @param message
     */
    public void decryptMsgSharedSecret(NetworkMessage message) {
        try {
            byte[] encryptedIvTextBytes = message.data.array();
            int ivSize = 16;
            int keySize = 16;

            //Extracting IV
            byte[] iv = new byte[ivSize];
            System.arraycopy(encryptedIvTextBytes, 0, iv, 0, iv.length);
            IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);

            //Extracting the encrypted part
            int encryptedSize = encryptedIvTextBytes.length - ivSize;
            byte[] encryptedBytes = new byte[encryptedSize];
            System.arraycopy(encryptedIvTextBytes, ivSize, encryptedBytes, 0, encryptedSize);

            //Decrypting
            Cipher cipherDecrypt = Cipher.getInstance("AES/CBC/PKCS5Padding");
            cipherDecrypt.init(Cipher.DECRYPT_MODE, getSymmetricKey(), ivParameterSpec);

            message.data = ByteBuffer.wrap(cipherDecrypt.doFinal(encryptedBytes));
        } catch (Exception e) {
            logger.error("Failed to decrypt {}", this, e);
        }
    }

    /**
     * Gets the current tunnel state.
     * @return
     */
    public MessageTunnelState getTunnelState() {
        if(nextNode == null && previousNode == null) {
            return MessageTunnelState.NOT_ESTABLISHED;
        }
        if(previousNode != Node.self && nextNode != Node.self) {
            return MessageTunnelState.HOSTED;
        }
        if(origin.equals(Node.self.getIdentity().getNetworkId())) {
            if(getSymmetricKey() != null) {
                return MessageTunnelState.ESTABLISHED;
            } else {
                return MessageTunnelState.ESTABLISHING;
            }
        }
        if(getSymmetricKey() != null) {
            return MessageTunnelState.ESTABLISHED;
        }
        return MessageTunnelState.NOT_ESTABLISHED;
    }

    @Override
    public String toString() {
        if(getTunnelState() == MessageTunnelState.HOSTED) {
            return "MT:{(" + origin + ")" + " <-> " + "(" + Node.self + ")" + " <-> " + "(" + destination + ")}";
        }
        if(getTunnelState() == MessageTunnelState.NOT_ESTABLISHED || getTunnelState() == MessageTunnelState.ESTABLISHING) {
            return "MT:{(...) -> (...)}";
        }
        if(getTunnelState() == MessageTunnelState.ESTABLISHED) {
            return "MT:{(" + origin + ")" + " <-> " + "(" + destination + ")}";
        }
        return "MT:{UNKNOWN}";
    }
}
