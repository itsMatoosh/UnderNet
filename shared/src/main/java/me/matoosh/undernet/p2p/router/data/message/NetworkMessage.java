package me.matoosh.undernet.p2p.router.data.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * A content that can be serialized and deserialized using the NetworkMessageSerializer.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage {
    /**
     * Unique id of the content.
     */
    public MsgType msgType;
    /**
     * The content.
     */
    public MsgBase content;

    /**
     * The expiration time of the content.
     */
    public long expiration;
    /**
     * Ensures data integrity on the receivers side.
     */
    public byte checksum;
    /**
     * The lenght of the sent data.
     */
    public short dataLength;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkMessage.class);

    /**
     * The data transported by the content.
     * Can be up to 64 fragments (64KB)
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
        logger.info("Serializing message {}", msgType);
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
            this.expiration = 0;
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
