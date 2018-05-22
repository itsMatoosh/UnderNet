package me.matoosh.undernet.p2p.router;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.ChannelMessageReceivedEvent;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.p2p.Manager;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.data.message.MsgType;
import me.matoosh.undernet.p2p.router.data.message.NetworkMessage;
import me.matoosh.undernet.p2p.router.data.message.PingMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stores and manages the network wide data.
 * Created by Mateusz Rębacz on 24.09.2017.
 */

public class NetworkDatabase extends Manager {
    /**
     * The logger of the class.
     */
    public static Logger logger = LoggerFactory.getLogger(NetworkDatabase.class);

    /**
     * Router specification is mandatory.
     *
     * @param router
     */
    public NetworkDatabase(Router router) {
        super(router);
    }

    /**
     * Registers the events of the manager.
     */
    @Override
    protected void registerEvents() {}

    /**
     * Registers the handlers of the manager.
     */
    @Override
    protected void registerHandlers() {
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
        if(e instanceof MessageReceivedEvent) {
            //Checking message type.
            ChannelMessageReceivedEvent messageEvent = (ChannelMessageReceivedEvent)e;
            if(messageEvent.message.content.getType() == MsgType.NODE_PING) {
                PingMessage pingMessage = (PingMessage)messageEvent.message.content;

                //Sending a ping message back.
                if(pingMessage.pong == true) {
                    return;
                }
                logger.info("Ping!");
                NetworkMessage msg = new NetworkMessage(Node.self.getIdentity().getNetworkId(), messageEvent.remoteNode.getIdentity().getNetworkId(), new PingMessage(true), NetworkMessage.MessageDirection.TO_DESTINATION);
                messageEvent.remoteNode.sendRaw(msg);
            }
        }
    }
}
