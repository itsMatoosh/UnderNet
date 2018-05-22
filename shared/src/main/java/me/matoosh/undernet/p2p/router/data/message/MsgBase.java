package me.matoosh.undernet.p2p.router.data.message;

import java.io.Serializable;

/**
 * Base of message content.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public abstract class MsgBase implements Serializable {
    /**
     * The network message object that carries this content.
     */
    public NetworkMessage networkMessage;

    /**
     * Gets the type of the message.
     * @return
     */
    public abstract MsgType getType();
}
