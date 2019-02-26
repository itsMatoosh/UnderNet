package me.matoosh.undernet.p2p.shine;

import me.matoosh.undernet.p2p.shine.client.ShineMediatorClient;
import me.matoosh.undernet.p2p.shine.server.ShineMediatorServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShineDriver {

    public static Logger logger = LoggerFactory.getLogger(ShineDriver.class);

    public static void main(String[] args) {
        if(args.length < 1) {
            logger.warn("Usage: shine server/client (...)");
            return;
        }

        if(args[0].equalsIgnoreCase("server")) {
            //start server
            ShineMediatorServer.main(args);
        } else if(args[0].equalsIgnoreCase("client")){
            //start client
            ShineMediatorClient.main(args);
        } else {
            logger.warn("Usage: shine server/client (...)");
        }
    }
}
