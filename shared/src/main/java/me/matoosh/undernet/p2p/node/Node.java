package me.matoosh.undernet.p2p.node;

import io.netty.channel.Channel;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;

/**
 * A single node within the network.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Node implements Serializable {
    /**
     * The network identity of the node.
     */
    private NetworkIdentity identity;
    /**
     * Connection address of this node.
     */
    private InetSocketAddress address;
    /**
     * Connection port of the node.
     */
    private int port = 2017;
    /**
     * SHINE id of the node.
     */
    private int shineId = 0;

    /**
     * The channel used for connection to the node.
     * Only available to neighboring nodes.
     */
    public transient Channel channel;

    /**
     * The router of this node.
     * Known only for self.
     **/
    public transient Router router;


    /**
     * The self node.
     */
    public static Node self;
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(Node.class);

    /**
     * Returns the address of the node.
     * @return
     */
    @Override
    public String toString() {
        String displayName = "";

        if(this != Node.self) {
            displayName = address.toString();
            if(isConnected()) {
                displayName = displayName + " [connected]";
            }
            return displayName;
        }

        return displayName + "[self]";
    }

    /**
     * Whether we are directly connected to this node.
     * @return
     */
    public boolean isConnected() {
        boolean connected = false;
        if(this != Node.self) {
            //Checking if the node is connected.
            for (Node n : UnderNet.router.getConnectedNodes()) {
                if(n.address.equals(this.address)) {
                    connected = true;
                }
            }
        }
        return connected;
    }

    /**
     * Sends a raw NetworkMessage to the node.
     * @param msg
     */
    public void sendRaw(NetworkMessage msg) {
        if(msg.getContent() != null)
            logger.info("Sending a {} message to: {}", msg.getContent().getType(), address);
        else
            logger.info("Sending a message to: {}", address);

        if(channel == null) {
            logger.error("Node {} is not a neighboring node, can't send the message!", this);
        } else {
            channel.writeAndFlush(msg, channel.voidPromise()).syncUninterruptibly();
        }
    }

    /**
     * Sets the node's identity.
     * @param identity
     */
    public void setIdentity(NetworkIdentity identity) {
        if(identity.isCorrect()) {
            this.identity = identity;
            logger.info("{} node identity set to: {}", toString(), this.identity);
        } else {
            logger.error("Couldn't set {} node identity, the identity object is not correct!", toString());
        }
    }

    /**
     * Gets the node address.
     * @return
     */
    public InetSocketAddress getAddress() {
        return address;
    }

    /**
     * Sets the node address.
     * @param address
     */
    public void setAddress(InetSocketAddress address) {
        this.address = address;
    }

    /**
     * Gets the node port.
     * @return
     */
    public int getPort() {
        return port;
    }

    /**
     * Sets the node port.
     * @param port
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * Gets the SHINE id.
     * @return
     */
    public int getShineId() {
        return shineId;
    }

    /**
     * Sets the SHINE id.
     * @param shineId
     */
    public void setShineId(int shineId) {
        this.shineId = shineId;
    }

    /**
     * Gets the node's identity.
     * @return
     */
    public NetworkIdentity getIdentity() {
        return identity;
    }

    /**
     * Checks if the given address is local.
     * @param addr
     * @return
     */
    public static boolean isLocalAddress(InetSocketAddress addr) {
        // Check if the address is a valid special local or loop back
        if (addr.getAddress().isAnyLocalAddress()|| addr.getAddress().isLoopbackAddress())
            return true;

        // Check if the address is defined on any interface
        try {
            return NetworkInterface.getByInetAddress(addr.getAddress()) != null;
        } catch (SocketException e) {
            return false;
        }
    }
}
