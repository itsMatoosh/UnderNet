package me.matoosh.undernet.p2p.node;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.SocketAddress;

import io.netty.channel.Channel;
import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.identity.NetworkIdentity;
import me.matoosh.undernet.p2p.router.Router;
import me.matoosh.undernet.p2p.router.data.messages.NetworkMessage;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * A single node within the network.
 * Created by Mateusz Rębacz on 26.01.2017.
 */

public class Node implements Serializable {
    /**
     * Connection address of this node.
     */
    public SocketAddress address;
    /**
     * Connection port of the node.
     */
    public int port = 2017;
    /**
     * Reliability of the node.
     * TODO: Actually make this useful.
     */
    public float reliability;

    /**
     * The channel used for connection to the node.
     * Only available to neighboring nodes.
     */
    public Channel channel;

    /**
     * The network identity of the node.
     * Known only for self.
     */
    public NetworkIdentity networkIdentity;
    /**
     * The router of this node.
     * Known only for self.
     **/
    public Router router;


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
        String displayName = address.toString();
        if(this != Node.self) {
            //Checking if the node is connected.
            boolean connected = false;
            for (Node n : UnderNet.router.connectedNodes) {
                if(n.address.equals(this.address)) {
                    connected = true;
                }
            }

            if(connected) {
                displayName = displayName + " [connected]";
            }
        }

        return displayName;
    }

    /**
     * Sets the address of the node.
     * @param address
     */
    public void setAddress(SocketAddress address) {
        //TODO: Caching integration.
        this.address = address;
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
}
