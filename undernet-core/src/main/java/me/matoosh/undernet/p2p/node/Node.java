package me.matoosh.undernet.p2p.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.SocketAddress;

import io.netty.channel.Channel;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

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
    public SocketAddress address;
    /**
     * Connection port of the node.
     */
    public int port = 2017;

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
            for (Node n : UnderNet.router.connectedNodes) {
                if(n.address.equals(this.address)) {
                    connected = true;
                }
            }
        }
        return connected;
    }

    /**
     * Sends a NetworkMessage to the node.
     * @param msg
     */
    public void send(NetworkMessage msg) {
        logger.info("Sending a message to: " + address + " with id: " + msg.msgId);
        if(channel == null) {
            logger.error("Long distance message have not been implemented yet :(", new NotImplementedException());
        } else {
            channel.writeAndFlush(msg);
        }
    }

    /**
     * Sets the node's identity.
     * @param identity
     */
    public void setIdentity(NetworkIdentity identity) {
        if(identity.isCorrect()) {
            this.identity = identity;
            logger.info(toString() + " node identity set to: " + this.identity);
        } else {
            logger.error("Couldn't set " + toString() + " node identity, the identity object is not correct!");
        }
    }

    /**
     * Gets the node's identity.
     * @return
     */
    public NetworkIdentity getIdentity() {
        return identity;
    }
}
