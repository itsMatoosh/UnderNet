package me.matoosh.undernet.p2p.router.data.message;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

/**
 * Message with addresses of UnderNet nodes.
 */
public class NodeNeighborsMessage extends MsgBase {

    private String[] addresses;

    public NodeNeighborsMessage() {}
    public NodeNeighborsMessage(InetSocketAddress[] addresses) {
        this.addresses = new String[addresses.length];
        for (int i = 0; i < addresses.length; i++) {
            this.addresses[i] = addresses[i].getHostString() + ":" + addresses[i].getPort();
        }
    }

    public String[] getAddresses() {
        return addresses;
    }

    @Override
    public MsgType getType() {
        return MsgType.NODE_NEIGHBORS;
    }

    @Override
    public void doDeserialize(byte[] data) {
        ByteArrayInputStream stream = new ByteArrayInputStream(data);
        int count = stream.read();
        this.addresses = new String[count];
        for (int i = 0; i < count; i++) {
            try {
                int strLength = stream.read();
                byte[] strData = new byte[strLength];
                stream.read(strData);

                this.addresses[i] = new String(strData, StandardCharsets.UTF_8);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public byte[] doSerialize() {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        stream.write(addresses.length);
        for (int i = 0; i < addresses.length; i++) {
            try {
                byte[] stringData = addresses[i].getBytes(StandardCharsets.UTF_8);
                stream.write(stringData.length);
                stream.write(stringData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stream.toByteArray();
    }
}
