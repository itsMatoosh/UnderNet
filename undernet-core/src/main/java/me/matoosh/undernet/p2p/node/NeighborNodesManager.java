package me.matoosh.undernet.p2p.node;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.p2p.router.data.messages.MsgType;

/**
 * Manages neighboring nodes connected to the router.
 * Created by Mateusz Rębacz on 25.09.2017.
 */

public class NeighborNodesManager extends EventHandler {
    /**
     * Sets up the manager.
     */
    public void setup() {
        registerHandlers();
    }

    /**
     * Registers the event handlers.
     */
    private void registerHandlers() {
        //Message received event.
        EventManager.registerHandler(this, ChannelMessageReceivedEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        ChannelMessageReceivedEvent messageReceivedEvent = (ChannelMessageReceivedEvent)e;
        if(messageReceivedEvent.message.msgId == MsgType.NODE_INFO.ordinal()) {
            //TODO: Check the generated id with the database and update.
           //messageReceivedEvent.remoteNode.id = new NetworkID(new BigInteger((messageReceivedEvent.message.data.)))
        }
    }
}
