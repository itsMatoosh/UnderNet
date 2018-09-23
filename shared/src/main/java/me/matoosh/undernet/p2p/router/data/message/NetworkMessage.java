package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.NetworkID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;
import java.security.*;

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
     * The length of a network message header.
     */
    public static final int NETWORK_MESSAGE_HEADER_LENGTH = NetworkID.NETWORK_ID_LENGTH * 2 + 1 /* signatureLength */ + 1 /*direction*/;

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
    private byte[] signature;
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
     * @param signature
     */
    public NetworkMessage(NetworkID origin, NetworkID destination, byte[] signature, MessageDirection direction) {
        this.origin = origin;
        this.destination = destination;
        this.signature = signature;
        this.direction = direction.value;
    }

    /**
     * Serializes the message's content.
     */
    public void serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this.content);
            out.flush();
            this.data = ByteBuffer.wrap(bos.toByteArray());
            logger.debug("Serialized a message of type {}, data length: {}", content.getType(), this.data.array().length);
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
        ByteArrayInputStream bis = new ByteArrayInputStream(this.data.array());
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            this.content = (MsgBase)in.readObject();
            this.content.networkMessage = this;
            logger.debug("Deserialized message of length: {}", this.data.array().length);
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
    public byte[] getSignature() {
        return this.signature;
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
        if(signature == null) {
            logger.warn("Signature of message {} missing!", this);
            return false;
        }
        if(signature.length <= 0) {
            logger.warn("Length of the message {} signature incorrect, {}!", this, signature.length);
            return false;
        }
        if(contentLength <= 0) {
            logger.warn("Content length of message {} less or equal to 0", this);
            return false;
        }
        /*if(!checkLength()) {
            logger.warn("Message {} is bigger than the network MTU, {}", this, getTotalLength());
            return false;
        }*/

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
     * Signs bytes with the self private key.
     */
    public void sign() {
        try {
            Signature sig = Signature.getInstance("SHA1withECDSA","SunEC");
            sig.initSign(Node.self.getIdentity().getPrivateKey());
            sig.update(data.array());
            this.signature = sig.sign();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
    }

    /**
     * Verifies the data with the origin's public key.
     */
    public boolean verify() {
        try {
            Signature sig = Signature.getInstance("SHA1withECDSA","SunEC");
            sig.initVerify(getOrigin().getPublicKey());
            sig.update(data.array());

            return sig.verify(signature);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (SignatureException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets the total length of the message.
     * @return
     */
    public int getTotalLength() {
        if(signature != null) {
            return NETWORK_MESSAGE_HEADER_LENGTH + contentLength + signature.length;
        } else {
            return NETWORK_MESSAGE_HEADER_LENGTH + contentLength;
        }
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
