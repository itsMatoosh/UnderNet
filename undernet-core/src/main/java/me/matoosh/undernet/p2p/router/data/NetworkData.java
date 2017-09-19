package me.matoosh.undernet.p2p.router.data;

/**
 * Represents data sent over the network.
 * Created by Mateusz Rębacz on 19.09.2017.
 */

public class NetworkData {
    /**
     * The network data header.
     */
    public NetworkDataHeader networkDataHeader;

    /**
     * The received data.
     */
    public byte[] data;

    /**
     * Creates a generic network data object.
     * @param dataHeader
     */
    public NetworkData(NetworkDataHeader dataHeader) {
        this.networkDataHeader = dataHeader;
        this.data = new byte[dataHeader.dataLength];
    }
}
