package me.matoosh.undernet.p2p.router.data.messages;

import me.matoosh.undernet.p2p.router.data.NetworkData;
import me.matoosh.undernet.p2p.router.data.NetworkDataHeader;

/**
 * A message that can be serialized and deserialized using the NetworkMessageSerializer.
 * Created by Mateusz Rębacz on 29.04.2017.
 */

public class NetworkMessage extends NetworkData {
    /**
     * The network serializer of this message.
     */
    NetworkMessageSerializer networkMessageSerializer = new NetworkMessageSerializer();

    /**
     * Creates a generic network message object.
     *
     * @param dataHeader
     */
    public NetworkMessage(NetworkDataHeader dataHeader) {
        super(dataHeader);
    }

    /**
     * Reads the message.
     * @return
     */
    public MessageBase readMessage() {
        return networkMessageSerializer.read(networkDataHeader.dataTypeId, data);
    }

    /**
     * Writes the data from a message base to this NetworkMessage instance.
     */
    public void writeMessage(MessageBase msgBase) {
        data = networkMessageSerializer.write(msgBase);
    }
}
