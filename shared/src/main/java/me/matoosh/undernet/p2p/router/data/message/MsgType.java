package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.router.data.message.tunnel.TunnelCloseRequestMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.TunnelControlMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.TunnelEstablishRequestMessage;
import me.matoosh.undernet.p2p.router.data.message.tunnel.TunnelEstablishResponseMessage;

/**
 * Types of messages the network sends.
 * Can go up to a 1000, since ids above that are reserved for other handlers.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public enum MsgType {
    TUNNEL_ESTABLISH_REQUEST((short)0, TunnelEstablishRequestMessage.class), //Request to establish a message tunnel.
    TUNNEL_ESTABLISH_RESPONSE((short)1, TunnelEstablishResponseMessage.class), //Tunnel response

    NODE_PING((short)2, PingMessage.class), //Message to ping a neighboring node.
    NODE_INFO((short)3, NodeInfoMessage.class), //Contains the node information about the sender.
    NODE_NEIGHBORS((short) 4, NodeNeighborsMessage.class), //Contains information about neighbors of a node.
    NODE_NEIGHBORS_REQUEST((short) 5, NodeNeighborsRequest.class), //Requests information about the neighbors of a node.
    RES_INFO((short) 6, ResourceInfoMessage.class), //Info about a transferred resource.
    RES_PULL((short) 7, ResourcePullMessage.class), //Contains the Network id of the pulled resource.
    RES_DATA((short) 8, ResourceDataMessage.class), //Data of a resource.
    RES_TRANSFER_CONTROL((short) 9, ResourceTransferControlMessage.class), //Request data of a resource.
    TUNNEL_CONTROL((short) 10, TunnelControlMessage.class), //Check if a tunnel is alive.
    TUNNEL_CLOSE_REQUEST((short) 11, TunnelCloseRequestMessage.class), //Request closure of a tunnel.
    RES_PULL_NOT_FOUND((short) 12, ResourcePullNotFoundMessage.class), //Indicates that a pulled resource couldn't be found.
    UNKNOWN((short)-1, null); //Unknown msg type.
    public short id;
    public Class type;

    MsgType(short id, Class type) {
        this.id = id;
        this.type = type;
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

    /**
     * Instantiates a message object corresponding to the message type.
     * If given data, the object will be deserialized.
     * @param data
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public MsgBase getMessageInstance(byte[] data) throws IllegalAccessException, InstantiationException {
        MsgBase msg = (MsgBase) this.type.newInstance();
        if(data != null)
            msg.doDeserialize(data);
        return msg;
    }
}
