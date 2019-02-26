package me.matoosh.undernet.p2p.shine.server;

import io.netty.channel.Channel;

import java.net.InetSocketAddress;
import java.util.ArrayList;

public class ShineEntry {
    private Channel channel;
    private InetSocketAddress address;
    private ArrayList<InetSocketAddress> ignore;

    public ShineEntry(Channel channel) {
        this.channel = channel;
        this.address = (InetSocketAddress) channel.remoteAddress();
        this.ignore = new ArrayList<>();
    }

    public Channel getChannel() {
        return channel;
    }

    public InetSocketAddress getAddress() {
        return address;
    }

    public ArrayList<InetSocketAddress> getIgnore() {
        return ignore;
    }
}
