package me.matoosh.undernet.identity;

import java.io.Serializable;

import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Represents the network identity used to connect.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkIdentity implements Serializable {
    /**
     * The username.
     */
    public String username;
    /**
     * The network id.
     */
    public NetworkID networkID;
}
