package me.matoosh.undernet.p2p.router.data.message.tunnel;

/**
 * Represents the state of a message tunnel.
 */
public enum MessageTunnelState {
    NOT_ESTABLISHED, //The message tunnel hasn't been established yet (just an empty tunnel object)
    ESTABLISHING, //The message tunnel is currently being established.
    ESTABLISHED, //The message tunnel has been established.
    HOSTED //The message tunnel is being hosted. The self node is not the origin nor the destination of the tunnel.
}
