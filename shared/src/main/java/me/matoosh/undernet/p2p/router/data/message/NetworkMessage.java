package me.matoosh.undernet.p2p.router.data.message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.ByteBuffer;

/**
 * A message that can be serialized and deserialized using the NetworkMessageSerializer.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage {
    /**
     * Unique id of the message.
     */
    public int msgId;
    /**
     * The message.
     */
    public MsgBase message;

    /**
     * The expiration time of the message.
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
     * The data transported by the message.
     * Can be up to 64 fragments (64KB)
     */
    public ByteBuffer data;

    public NetworkMessage() {}

    /**
     * Creates a network message given its type and content.
     * @param msgType
     * @param msg
     */
    public NetworkMessage(MsgType msgType, MsgBase msg) {
        this.msgId = msgType.ordinal();
    }

    /**
     * Calculates the checksum of the message data.
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
     * Serializes the message's content.
     */
    public void serialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this.message);
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
     * Deserializes the message from its byte[] data.
     * @return
     */
    public void deserializeMessage() {
        ByteArrayInputStream bis = new ByteArrayInputStream(this.data.array());
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            this.message = (MsgBase)in.readObject();
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
