package me.matoosh.undernet.p2p.router.data.message;

import java.net.InetSocketAddress;

/**
 * Message with addresses of UnderNet nodes.
 */
public class NodeNeighborsMessage extends MsgBase {

    private String[] addresses;

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
}
