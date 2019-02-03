package me.matoosh.undernet.p2p.punchthrough;

import java.net.InetAddress;

/**
 * Manages the NAT and firewall punch through.
 */
public class PunchthroughUtils {
    /**
     * Attempts to punch a hole through NAT/Firewall.
     * When a hole is successfully punched, the client on the specified address should be able to connect.
     * @param address
     */
    public static void punchHole(InetAddress address, int port) {

    }
}
