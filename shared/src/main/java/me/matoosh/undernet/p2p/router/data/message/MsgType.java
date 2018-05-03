package me.matoosh.undernet.p2p.router.data.message;

/**
 * Types of messages the network sends.
 * Can go up to a 1000, since ids above that are reserved for other handlers.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public enum MsgType {
    NODE_PING((short)0), //Message to ping a neighboring node.
    NODE_INFO((short)1), //Contains the node information about the sender.
    RES_PUSH((short)2), //Contains information on the resource that needs to be pushed to the receiver.
    RES_PULL((short)3), //Contains information on the resource that needs to be pulled to the sender.
    RES_RETRIEVE((short)4), //Contains information on the retrieved resrouce that needs to be pushed to the sender.
    FILE_REQ((short)5), //Requests a file from a neighboring node.
    FILE_CHUNK((short)6), //A chunk of a file.
    UNKNOWN((short)-1);

    public short id;

    MsgType(short id) {
        this.id = id;
    }

    public static MsgType getById(short id) {
        for(MsgType e : values()) {
            if(e.id == id) return e;
        }
        return UNKNOWN;
    }
}
