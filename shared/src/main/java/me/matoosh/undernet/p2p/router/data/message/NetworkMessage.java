package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.NetworkID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * Network Message is the base for all the network communication.
 * The message will be passed on by the intermediate nodes
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage {
    /**
     * The destination that the message will be forwarded to.
     */
    private NetworkID destination;
    /**
     * Unique id of the content.
     */
    private MsgType msgType;
    /**
     * The content of the message.
     * Known only by the destination node.
     */
    private MsgBase content;

    /**
     * Checksum of the decrypted content.
     */
    public byte checksum;
    /**
     * The lenght of the message content data.
     */
    public short dataLength;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessage.class);

    /**
     * The raw data transported by the content.
     */
    public ByteBuffer data;

    public NetworkMessage() {}

    /**
     * Creates a network content given its type and content.
     * @param msgType
     * @param msg
     */
    public NetworkMessage(MsgType msgType, MsgBase msg) {
        this.msgType = msgType;
        this.content = msg;
    }

    /**
     * Calculates the checksum of the content data.
     * @return
     */
    public byte calcChecksum() {
        byte sum = 0;
        byte[] dataArr = data.array();
        for (int i = 0; i < dataArr.length; i++)
            sum += dataArr[i];
        return sum;
    }

    /**
     * Serializes the content's content.
     */
    public void serialize() {
        logger.info("Serializing and encrypting message {}", msgType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this.content);
            out.flush();
            this.data = ByteBuffer.wrap(bos.toByteArray());

            //Setting the msg info.
            this.dataLength = (short)(Short.MIN_VALUE + this.data.array().length);
            this.checksum = calcChecksum();
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
        logger.info("Deserializing message {}, length: {}", msgType, this.data.array().length);
        ByteArrayInputStream bis = new ByteArrayInputStream(this.data.array());
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            this.content = (MsgBase)in.readObject();
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
}
