package me.matoosh.undernet.p2p.config;

/**
 * Network Config implementation for serialization purposes.
 * Created by Mateusz Rębacz on 18.09.2017.
 */

public class NetworkConfigImpl implements NetworkConfig {
    /**
     * The default listening port.
     */
    public int listeningPort = 2017;

    /**
     * The listening port to be used by the app.
     *
     * @return
     */
    @Override
    public Integer listeningPort() {
        return listeningPort;
    }
}
