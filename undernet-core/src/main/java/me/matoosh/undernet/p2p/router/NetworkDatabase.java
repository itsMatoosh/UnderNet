package me.matoosh.undernet.p2p.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;

/**
 * Stores and manages the network wide data.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public class NetworkDatabase extends EventHandler {
    /**
     * List of the users currently connected to the network.
     */
    public ArrayList<String> connectedUsers;

    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkDatabase.class);

    /**
     * Sets up the network database.
     */
    public void setup() {
        registerHandlers();
    }

    /**
     * Registers handlers.
     */
    private void registerHandlers() {
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        //Message received event.
        if(e instanceof ChannelMessageReceivedEvent) {
            //Checking message type.
            ChannelMessageReceivedEvent messageEvent = (ChannelMessageReceivedEvent)e;
            if(messageEvent.message.msgId == MsgType.NODE_PING.ordinal()) {
                //Sending a ping message back.
                if(messageEvent.message.data.get() == 0x01) {
                    return;
                }
                logger.info("Ping!");
                NetworkMessage msg = new NetworkMessage(MsgType.NODE_PING, new byte[] {
                    0x01
                });
                messageEvent.remoteNode.send(msg);
            }
        }
    }
}
