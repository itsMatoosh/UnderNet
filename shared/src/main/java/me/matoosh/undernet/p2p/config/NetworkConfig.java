package me.matoosh.undernet.p2p.config;

/**
 * Represents the network config of the app.
 * Created by Mateusz Rębacz on 17.09.2017.
 */

public interface NetworkConfig {
    /**
     * The listening port to be used by the app.
     * @return
     */
    Integer listeningPort();

    /**
     * Amount of incoming connections kept in the backlog.
     * @return
     */
    Integer backlogCapacity();

    /**
     * Whether to keep the router working despite a fatal exception.
     * @return
     */
    Boolean ignoreExceptions();

    /**
     * The maximum amount of neighbors the node can have.
     *
     * @return
     */
    Integer maxNeighbors();

    /**
     * The optimal amount of neighbors the node can have.
     *
     * @return
     */
    Integer optNeighbors();

    /**
     * The maximum amount of reconnections before error.
     *
     * @return
     */
    Integer maxReconnectCount();

    /**
     * Whether UnderNet should use SHINE.
     * @return
     */
    Boolean useShine();

    /**
     * The used SHINE address.
     * @return
     */
    String shineAddress();

    /**
     * The used SHINE port.
     * @return
     */
    Integer shinePort();
}
