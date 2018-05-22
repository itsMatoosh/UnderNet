package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.NetworkID;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.*;
import java.nio.ByteBuffer;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Network Message is the base for all the network communication.
 * The message will be passed on by the intermediate nodes until it reaches the node with net id closest to the destination id of the message.
 * The content of the message is first encrypted with the private key of the origin, which lets the nodes on the way verify it.
 * All messages except tunnel establishing messages are encrypted.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage {
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessage.class);

    /**
     * The cipher used to encypt the messages.
     */
    private static Cipher cipher;
    static {
        try {
            cipher = Cipher.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }
    }

    /**
     * The length of a network message header.
     */
    public static final int NETWORK_MESSAGE_HEADER_LENGTH = NetworkID.NETWORK_ID_LENGTH + 16 /*checksum*/ + 2 /*dataLength*/ + 1 /*direction*/;

    /**
     * The maximum packet size of the network.
     */
    public static final int NETWORK_MTU_SIZE = 1500;

    /**
     * The maximum network content size.
     */
    public static final int MAX_CONTENT_SIZE = NETWORK_MTU_SIZE - NETWORK_MESSAGE_HEADER_LENGTH;

    /**
     * The network id of the origin node.
     */
    private NetworkID origin;
    /**
     * The destination that the message will be forwarded to.
     */
    private NetworkID destination;
    /**
     * Checksum of the decrypted content.
     * Used to see whether the message is unencrypted.
     */
    private byte[] checksum;
    /**
     * The length of the message content data.
     */
    private short contentLength;

    /**
     * From origin to destination = 0
     * From destination to origin = 1
     */
    private byte direction = 0;

    /**
     * The deserialized content of the message.
     * Known only by the destination node.
     */
    public MsgBase content;
    /**
     * The raw data.
     * The serialized content.
     */
    public ByteBuffer data;

    public NetworkMessage() {}

    /**
     * Creates a network message.
     * @param origin the origin of the network message.
     * @param destination the destination of the network message.
     * @param content the content of the message.
     */
    public NetworkMessage(NetworkID origin, NetworkID destination, MsgBase content, MessageDirection direction) {
        this.origin = origin;
        this.destination = destination;
        this.content = content;
        this.direction = direction.value;
    }
    /**
     * Creates a network message.
     * @param origin
     * @param destination
     * @param checksum
     */
    public NetworkMessage(NetworkID origin, NetworkID destination, byte[] checksum, MessageDirection direction) {
        this.origin = origin;
        this.destination = destination;
        this.checksum = checksum;
        this.direction = direction.value;
    }

    /**
     * Serializes the message's content.
     */
    public void serialize() {
        logger.info("Serializing a message of type {}", content.getType());
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this.content);
            out.flush();
            this.data = ByteBuffer.wrap(bos.toByteArray());
        } catch (IOException e) {
            logger.error("Error while serializing a network message: " + this.toString(), e);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Deserializes the content from its byte[] data.
     * @return
     */
    public void deserialize() {
        logger.info("Deserializing message of length: {}", this.data.array().length);
        ByteArrayInputStream bis = new ByteArrayInputStream(this.data.array());
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            this.content = (MsgBase)in.readObject();
            this.content.networkMessage = this;
        } catch (ClassNotFoundException e) {
            logger.error("Error occured while deserializing a network message!", e);
        } catch (IOException e) {
            logger.error("Error occured while deserializing a network message!", e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                // ignore close exception
            }
        }
    }

    /**
     * Calculates the checksum of the content data.
     * @return
     */
    public void calcChecksum() {
        this.checksum = DigestUtils.md5(this.data.array());
    }
    /**
     * Checks the message integrity with its checksum.
     * @return
     */
    public boolean checkIntegrity() {
        byte[] currentSum = DigestUtils.md5(this.data.array());

        if(Arrays.equals(currentSum, this.checksum)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Encrypts the message.
     * @param key the key used to encrypt the message.
     */
    public void encrypt(Key key) {
        try {
            //Encrypt the bytes using the secret key
            NetworkMessage.cipher.init(Cipher.ENCRYPT_MODE, key);
            this.data = ByteBuffer.wrap(cipher.doFinal(this.data.array()));
        } catch (Exception e) {
            logger.error("Failed to encrypt {}", this, e);
        }
    }

    /**
     * Decrypts the message.
     * @param key the key used to decrypt the message.
     */
    public void decrypt(Key key) {
        try {
            //Do the decryption
            NetworkMessage.cipher.init(Cipher.DECRYPT_MODE, key);
            this.data = ByteBuffer.wrap(cipher.doFinal(this.data.array()));
        } catch (Exception e) {
            logger.error("Failed to decrypt {}", this, e);
        }
    }

    /**
     * Gets the destination of the message.
     * @return
     */
    public NetworkID getDestination() {
        return this.destination;
    }
    /**
     * Gets the origin of the message.
     * @return
     */
    public NetworkID getOrigin() {
        return this.origin;
    }
    /**
     * Gets the checksum of the message.
     * @return
     */
    public byte[] getChecksum() {
        return this.checksum;
    }
    /**
     * Gets the content length of the message.
     * @return
     */
    public int getContentLength() {
        return this.contentLength;
    }
    /**
     * Gets the raw data of the message.
     * @return
     */
    public byte[] getData() {
        return this.data.array();
    }
    /**
     * Sets the message data.
     * @param data
     */
    public void setData(byte[] data) {
        this.data = ByteBuffer.wrap(data);
        this.contentLength = (short) (Short.MIN_VALUE + (short)data.length);
    }

    /**
     * Checks whether the network message is valid.
     * @return
     */
    public boolean isValid() {
        if(origin == null || !origin.isValid()) {
            logger.warn("Origin of message {} missing or invalid!", this);
            return false;
        }
        if(destination == null || !destination.isValid()) {
            logger.warn("Destination of message {} missing or invalid!", this);
            return false;
        }
        if(checksum == null) {
            logger.warn("Checksum of message {} missing!", this);
            return false;
        }
        if(checksum.length != 16) {
            logger.warn("Length of the message {} chesum incorrect, {}!", this, checksum.length);
            return false;
        }
        if(contentLength <= 0) {
            logger.warn("Content length of message {} less or equal to 0", this);
            return false;
        }
        if(!checkLength()) {
            logger.warn("Message {} is bigger than the network MTU, {}", this, getTotalLength());
            return false;
        }

        return true;
    }

    /**
     * Checks if the length of the message fits in the MTU of the network.
     * @return
     */
    public boolean checkLength() {
        int messageLength = getTotalLength();
        if(messageLength > NETWORK_MTU_SIZE) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Gets the total length of the message.
     * @return
     */
    public int getTotalLength() {
        return NETWORK_MESSAGE_HEADER_LENGTH + contentLength;
    }

    /**
     * Gets the current message direction.
     * @return
     */
    public MessageDirection getDirection() {
        return MessageDirection.getByValue(this.direction);
    }

    @Override
    public String toString() {
        return "netMsg{to=" + destination + "}";
    }

    /**
     * Updates the message details.
     */
    public void updateDetails() {
        this.contentLength = (short)data.array().length;
    }

    /**
     * Direction of a network message.
     */
    public enum MessageDirection {
        TO_DESTINATION((byte)0),
        TO_ORIGIN((byte)1);

        public byte value;

        MessageDirection(byte value) {
            this.value = value;
        }

        /**
         * Gets a message direction given its value.
         * @param value
         * @return
         */
        public static MessageDirection getByValue(byte value) {
            for(MessageDirection direction : values()) {
                if(direction.value == value) return direction;
            }
            return TO_DESTINATION;
        }
    }
}
