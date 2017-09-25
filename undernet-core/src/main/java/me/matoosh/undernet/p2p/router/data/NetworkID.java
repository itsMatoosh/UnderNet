package me.matoosh.undernet.p2p.router.data;

import java.math.BigInteger;
import java.util.Random;

/**
 * Represents a network id.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NetworkID {
    /**
     * The data of the network id.
     */
    public BigInteger data;

    public NetworkID() {

    }
    public NetworkID(String value) {
        this.data = new BigInteger(value, 512);
    }
    public NetworkID(BigInteger id) {
        this.data = id;
    }

    /**
     * Calculates the distrance between this id and an other id.
     * @param other
     * @return
     */
    public BigInteger distanceTo(NetworkID other) {
        return data.xor(other.data);
    }

    /**
     * Returns a random network id.
     * @return
     */
    public static NetworkID random() {
        NetworkID random = new NetworkID();
        random.data = new BigInteger(512, new Random());
        return random;
    }
}
