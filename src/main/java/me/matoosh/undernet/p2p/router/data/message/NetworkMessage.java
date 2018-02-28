package me.matoosh.undernet.p2p.router.data.message;

import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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
     * Creates a network message given its type and raw data.
     * @param msgType
     * @param data
     */
    public NetworkMessage(MsgType msgType, byte[] data) {
        this.msgId = msgType.ordinal();
        this.data = ByteBuffer.wrap(data);
        this.dataLength = (short)(Short.MIN_VALUE + data.length);
        this.checksum = calcChecksum();
        this.expiration = 0;
    }

    /**
     * Creates a network message given its type and content.
     * @param msgType
     * @param msg
     */
    public NetworkMessage(MsgType msgType, MsgBase msg) {
        this.msgId = msgType.ordinal();
        this.data = ByteBuffer.wrap(serializeMessage(msg));
        this.dataLength = (short)(Short.MIN_VALUE + this.data.array().length);
        this.checksum = calcChecksum();
        this.expiration = 0;
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
     * Serializes an object to a byte[].
     */
    public static byte[] serializeMessage(MsgBase msg) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(msg);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            logger.error("Error while serializing a network message: " + msg.toString(), e);
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return null;
    }

    /**
     * Deserializes an object from a byte array.
     * @param bytes
     * @return
     */
    public static MsgBase deserializeMessage(byte[] bytes) {
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            return (MsgBase)in.readObject();
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
        return null;
    }
}
