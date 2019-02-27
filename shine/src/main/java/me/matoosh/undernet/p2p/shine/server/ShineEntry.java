package me.matoosh.undernet.p2p.shine.server;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;

/**
 * A single connected shine client.
 */
public class ShineEntry {
    private Channel channel;
    private int shineId;
    private ArrayList<Integer> ignore;

    public ShineEntry(Channel channel) {
        this.channel = channel;
        this.ignore = new ArrayList<>();
    }

    public Channel getChannel() {
        return channel;
    }

    public InetSocketAddress getAddress() { return (InetSocketAddress) getChannel().remoteAddress(); }

    public ArrayList<Integer> getIgnore() {
        return ignore;
    }

    public int getShineId() {
        return shineId;
    }

    public void setShineId(int shineId) {
        this.shineId = shineId;
    }
}
