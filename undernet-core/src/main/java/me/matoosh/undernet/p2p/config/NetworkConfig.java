package me.matoosh.undernet.p2p.config;

/**
 * Represents the network config of the app.
 * Created by Mateusz Rębacz on 17.09.2017.
 */

public interface NetworkConfig {
    /**
     * The listening port to be used by the app.
     * Default: 2017
     * @return
     */
    Integer listeningPort();
}
