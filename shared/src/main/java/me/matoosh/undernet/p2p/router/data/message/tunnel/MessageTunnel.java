package me.matoosh.undernet.p2p.router.data.message.tunnel;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.p2p.crypto.KeyTools;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import me.matoosh.undernet.p2p.router.data.message.MsgBase;
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
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(MessageTunnel.class);
    /**
     * The secure random generator.
     */
    private static SecureRandom secureRandom = new SecureRandom();
    /**
     * The list of messages awaiting sending.
     */
    public ArrayList<NetworkMessage> messageQueue = new ArrayList<NetworkMessage>();
    /**
     * The network id of the origin node.
     */
    private NetworkID origin;

    //This part is present only on the receiving ends of the tunnel.
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
    /**
     * The public key of the other.
     * Used by self to encrypt messages for the other side of the tunnel.
     */
    private PublicKey otherPublicKey;
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
     * The side of the tunnel.
     */
    private MessageTunnelSide side;
    /**
     * The time that the last message was received from the tunnel.
     */
    private long lastMessageTime;
    /**
     * Whether the tunnel should be kept alive.
     */
    private boolean keepAlive = false;

    /**
     * Creates a hosted message tunnel.
     *
     * @param destination
     * @param origin
     */
    public MessageTunnel(NetworkID origin, NetworkID destination) {
        this.destination = destination;
        this.origin = origin;
        this.side = MessageTunnelSide.UNDEFINED;
        this.lastMessageTime = System.currentTimeMillis() + 2 * Router.controlLoopInterval;
    }

    /**
     * Creates a message tunnel.
     *
     * @param destination
     * @param origin
     */
    public MessageTunnel(NetworkID origin, NetworkID destination, MessageTunnelSide side) {
        this.destination = destination;
        this.origin = origin;
        this.side = side;
        this.lastMessageTime = System.currentTimeMillis() + 2 * Router.controlLoopInterval;
    }

    public PublicKey getOtherPublicKey() {
        return otherPublicKey;
    }

    public void setOtherPublicKey(PublicKey publicKey) {
        logger.info("Set the other public key to: {}", publicKey);
        this.otherPublicKey = publicKey;
    }

    /**
     * Calculates the shared secret key of the tunnel.
     */
    public void calcSharedSecret() {
        try {
            logger.info("Calculating the shared secret for tunnel: {}", this);
            KeyAgreement keyAgreement = KeyAgreement.getInstance("ECDH", KeyTools.KEYGEN_ALGORITHM_PROVIDER);
            keyAgreement.init(Node.self.getIdentity().getPrivateKey()); //Self private key.
            keyAgreement.doPhase(getOtherPublicKey(), true);
            sharedSecret = keyAgreement.generateSecret();

            logger.info("Calculating the symmetric key for tunnel: {}", this);
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
            if(derivedSymmetricKey != null && sharedSecret != null)
                logger.info("Shared secret calculated for tunnel: {}", this);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * Closes the tunnel.
     */
    public void close() {
        sendMessage(new TunnelCloseRequestMessage());
        UnderNet.router.messageTunnelManager.closeTunnel(this);
    }

    /**
     * Returns the shared secret of the tunnel.
     *
     * @return
     */
    public SecretKeySpec getSymmetricKey() {
        return derivedSymmetricKey;
    }

    /**
     * Gets the other.
     *
     * @return
     */
    public NetworkID getDestination() {
        return this.destination;
    }

    /**
     * Gets the origin.
     *
     * @return
     */
    public NetworkID getOrigin() {
        return this.origin;
    }

    /**
     * Gets the previous node.
     *
     * @return
     */
    public Node getPreviousNode() {
        return this.previousNode;
    }

    /**
     * Sets the previous node.
     *
     * @param previousNode
     */
    public void setPreviousNode(Node previousNode) {
        this.previousNode = previousNode;
    }

    /**
     * Gets the next node.
     *
     * @return
     */
    public Node getNextNode() {
        return this.nextNode;
    }

    /**
     * Sets the next node.
     *
     * @param nextNode
     */
    public void setNextNode(Node nextNode) {
        this.nextNode = nextNode;
    }

    /**
     * Returns whether the tunnel should be kept alive.
     * @return
     */
    public boolean isKeepAlive() {
        return keepAlive;
    }

    /**
     * Sets whether the tunnel should be kept alive.
     * @param keepAlive
     */
    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    /**
     * Encrypts bytes with the shared symmetric key.
     *
     * @param message
     */
    public void encryptMsgSymmetric(NetworkMessage message) {
        try {
            byte[] clean = message.getData();

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

            message.setData(encryptedIVAndData);
        } catch (Exception e) {
            logger.error("Failed to encrypt {}", this, e);
        }
    }

    /**
     * Decrypts bytes with the shared symmetric key.
     *
     * @param message
     */
    public void decryptMsgSharedSecret(NetworkMessage message) {
        //Checking if the symmetric key exists.
        if (getSymmetricKey() == null) {
            logger.warn("Missing symmetric key for tunnel: {}", this);
            return;
        }

        try {
            byte[] encryptedIvTextBytes = message.getData();
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

            message.setData(cipherDecrypt.doFinal(encryptedBytes));
        } catch (Exception e) {
            logger.error("Failed to decrypt {}", this, e);
        }
    }

    /**
     * Gets the current tunnel state.
     *
     * @return
     */
    public MessageTunnelState getTunnelState() {
        if (getSymmetricKey() != null) {
            return MessageTunnelState.ESTABLISHED;
        } else if (side != MessageTunnelSide.UNDEFINED) {
            return MessageTunnelState.ESTABLISHING;
        } else if (previousNode != null && nextNode != null) {
            return MessageTunnelState.HOSTED;
        } else {
            return MessageTunnelState.NOT_ESTABLISHED;
        }
    }

    /**
     * Gets the current side of the tunnel.
     *
     * @return
     */
    public MessageTunnelSide getSide() {
        return side;
    }

    /**
     * Sets the current side of the tunnel.
     *
     * @param side
     */
    public void setSide(MessageTunnelSide side) {
        this.side = side;
    }

    /**
     * Gets the time the last message was received on the tunnel.
     *
     * @return
     */
    public long getLastMessageTime() {
        return this.lastMessageTime;
    }

    /**
     * Sets the time the last message was received on the tunnel.
     *
     * @param currentTimeMillis
     */
    public void setLastMessageTime(long currentTimeMillis) {
        this.lastMessageTime = currentTimeMillis;
    }

    /**
     * Sends the specified message.
     *
     * @param content
     */
    public void sendMessage(MsgBase content) {
        if (getTunnelState() == MessageTunnelState.HOSTED) {
            logger.warn("Can't send messages through hosted tunnels!");
            return;
        }
        logger.info("Sending message, tunnel side {}", this.side);
        if (side == MessageTunnelSide.ORIGIN || side == MessageTunnelSide.UNDEFINED) {
            //TO_DESTINATION
            UnderNet.router.networkMessageManager.sendMessage(content, this);
        } else if (side == MessageTunnelSide.DESTINATION) {
            //TO_ORIGIN
            UnderNet.router.networkMessageManager.sendResponse(content, this);
        }
    }

    @Override
    public String toString() {
        if (getTunnelState() == MessageTunnelState.HOSTED) {
            return String.format("{(%1$s) <-> (%2$s) <-> (%3$s)}", getOrigin(), Node.self, getDestination());
        }
        if (getSide() == MessageTunnelSide.ORIGIN) {
            if (getTunnelState() == MessageTunnelState.NOT_ESTABLISHED || getTunnelState() == MessageTunnelState.ESTABLISHING) {
                return String.format("{(%1$s) <x> (%2$s)}", Node.self, getDestination());
            }
            if (getTunnelState() == MessageTunnelState.ESTABLISHED) {
                return String.format("{(%1$s) <-> (%2$s)}", Node.self, getDestination());
            }
        } else {
            if (getTunnelState() == MessageTunnelState.NOT_ESTABLISHED || getTunnelState() == MessageTunnelState.ESTABLISHING) {
                return String.format("{(%1$s) <x> (%2$s)}", getOrigin(), Node.self);
            }
            if (getTunnelState() == MessageTunnelState.ESTABLISHED) {
                return String.format("{(%1$s) <-> (%2$s)}", getOrigin(), Node.self);
            }
        }
        return "MT:{UNKNOWN}";
    }
}
