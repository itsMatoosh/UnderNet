package me.matoosh.undernet.p2p.router.data;

/**
 * The header sent before the data of a message.
 * Created by Mateusz Rębacz on 19.09.2017.
 */

public class NetworkDataHeader {
    /**
     * The id of the type of the data.
     */
    public short dataTypeId;
    /**
     * The length of the data.
     */
    public int dataLength;
}
