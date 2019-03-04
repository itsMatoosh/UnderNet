package me.matoosh.undernet.p2p.router.data.message;

import java.nio.ByteBuffer;

/**
 * Message containing node info.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public class NodeInfoMessage extends MsgBase {

    /**
     * The connection port of the node.
     */
    private int connectionPort;

    public NodeInfoMessage() {}

    /**
     * Constructs the node info message.
     */
    public NodeInfoMessage (int connectionPort) {
        this.connectionPort = connectionPort;
    }

    @Override
    public MsgType getType() {
        return MsgType.NODE_INFO;
    }

    @Override
    public void doDeserialize(byte[] data) {
        ByteBuffer b = ByteBuffer.wrap(data);
        connectionPort = b.getInt();
    }

    @Override
    public byte[] doSerialize() {
        ByteBuffer b = ByteBuffer.allocate(4);
        b.putInt(connectionPort);
        return b.array();
    }

    public int getConnectionPort() {
        return connectionPort;
    }
}
