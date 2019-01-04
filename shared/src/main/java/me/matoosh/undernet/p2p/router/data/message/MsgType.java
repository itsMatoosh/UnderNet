package me.matoosh.undernet.p2p.router.data.message;

/**
 * Types of messages the network sends.
 * Can go up to a 1000, since ids above that are reserved for other handlers.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public enum MsgType {
    TUNNEL_ESTABLISH_REQUEST((short)0), //Request to establish a message tunnel.
    TUNNEL_ESTABLISH_RESPONSE((short)1), //Tunnel response

    NODE_PING((short)2), //Message to ping a neighboring node.
    NODE_INFO((short)3), //Contains the node information about the sender.
    NODE_NEIGHBORS((short) 4), //Contains information about neighbors of a node.
    NODE_NEIGHBORS_REQUEST((short) 5), //Requests information about the neighbors of a node.
    RES_INFO((short) 6), //Info about a transferred resource.
    RES_PULL((short) 7), //Contains the Network id of the pulled resource.
    RES_DATA((short) 8), //Data of a resource.
    RES_DATA_REQUEST((short) 9), //Confirm data of a resource.
    UNKNOWN((short)-1); //Unknown msg type.
    public short id;

    MsgType(short id) {
        this.id = id;
    }

    /**
     * Gets a message type given its id.
     * @param id
     * @return
     */
    public static MsgType getById(short id) {
        for(MsgType e : values()) {
            if(e.id == id) return e;
        }
        return UNKNOWN;
    }
}
