package me.matoosh.undernet.p2p.router.data.message;

/**
 * Types of messages the network sends.
 * Can go up to a 1000, since ids above that are reserved for other handlers.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public enum MsgType {
    NODE_PING, //Message to ping a neighboring node.
    NODE_INFO, //Contains the node information about the sender.
    RES_PUSH //Contains information on the resource that needs to be pushed to the receiver.
}
