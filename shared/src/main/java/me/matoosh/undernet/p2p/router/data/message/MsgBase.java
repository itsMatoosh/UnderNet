package me.matoosh.undernet.p2p.router.data.message;

import me.matoosh.undernet.p2p.node.Node;

import java.io.Serializable;

/**
 * Base of a message.
 * Created by Mateusz Rębacz on 26.09.2017.
 */

public abstract class MsgBase implements Serializable {
    /**
     * The sender of the message.
     */
    public Node sender;
}
