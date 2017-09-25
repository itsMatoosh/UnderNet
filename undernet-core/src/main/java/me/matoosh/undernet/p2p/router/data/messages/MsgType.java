package me.matoosh.undernet.p2p.router.data.messages;

/**
 * Types of messages the network sends.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public enum MsgType {
    NODE_PING, //Message to ping a neighboring node.
    NODE_INFO //Contains the node information about the sender.
}
