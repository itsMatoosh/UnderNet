package me.matoosh.undernet.p2p.router.data.message;

import java.io.*;

/**
 * A message base with standard serialization.
 */
public abstract class StandardSerializedMsgBase extends MsgBase {
    @Override
    public void doDeserialize(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        ObjectInput in = null;
        try {
            in = new ObjectInputStream(bis);
            System.out.println("SSDASDADSD");
            restoreValues((StandardSerializedMsgBase)in.readObject());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
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

    abstract void restoreValues(StandardSerializedMsgBase serializedMsgBase);

    @Override
    public byte[] doSerialize() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(this);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                // ignore close exception
            }
        }
        return new byte[0];
    }
}
