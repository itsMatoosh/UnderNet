package me.matoosh.undernet.identity;

import java.io.Serializable;
import java.util.Random;

import me.matoosh.undernet.p2p.router.data.NetworkID;

/**
 * Represents the network identity used to connect.
 * Created by Mateusz Rębacz on 22.09.2017.
 */

public class NetworkIdentity implements Serializable {
    /**
     * The username.
     */
    private String username;
    /**
     * The network id.
     */
    private NetworkID networkID;


    /**
     * Checks whether the NetworkIdentity is correct.
     * @return
     */
    public boolean isCorrect() {
        if (networkID == null) {
            return false;
        }
        return true;
    }

    /**
     * Sets the network id.
     * @param id
     */
    public void setNetworkId(NetworkID id) {
        this.networkID = id;
    }
    /**
     * Gets the network id.
     * @return
     */
    public NetworkID getNetworkId() {
        return networkID;
    }
    /**
     * Sets the username.
     * @param username
     */
    public void setUsername(String username) {
        if(username == null || username.isEmpty()) {
            this.username = "Anon-" + new Random().nextInt(9) + "" + new Random().nextInt(9) + "" + new Random().nextInt(9);
            return;
        }
        this.username = username;
    }

    /**
     * Gets the username.
     * @return
     */
    public String getUsername() {
        return this.username;
    }

    @Override
    public String toString() {
        return "NetworkIdentity{" +
                "username='" + username + '\'' +
                ", networkID=" + networkID +
                '}';
    }
}
