package me.matoosh.undernet.p2p.router;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.router.RouterErrorEvent;
import me.matoosh.undernet.event.router.RouterStatusEvent;

import static me.matoosh.undernet.UnderNet.logger;

/**
 * Handles the router. ;)
 * Created by Mateusz Rębacz on 28.06.2017.
 */

public class RouterHandler extends EventHandler {
    /**
     * The number of reconnect attempts, the router attempted.
     */
    public int reconnectNum = 0;

    /**
     * Sets up the handler.
     */
    public void setup() {
        EventManager.registerHandler(this, RouterStatusEvent.class);
        EventManager.registerHandler(this, RouterErrorEvent.class);
    }

    /**
     * Called when the handled event is called.
     *
     * @param e
     */
    @Override
    public void onEventCalled(Event e) {
        if(e.getClass() == RouterStatusEvent.class) {
            RouterStatusEvent statusEvent = (RouterStatusEvent)e;
            switch(statusEvent.newStatus) {
                case STOPPED:
                    onConnectionEnded();
                    break;
                case STARTING:
                    break;
                case CONNECTING:
                    break;
                case NETWORK_CONNECTED:
                    break;
                case STOPPING:
                    break;
            }
            //TODO: Handle the status change.
        } else if(e.getClass() == RouterErrorEvent.class) {
            onRouterError((RouterErrorEvent) e);
        }
    }

    /**
     * Called when the connection to the network ends.
     */
    public void onConnectionEnded() {
        //Resetting the reconn num.
        reconnectNum = 0;
    }

    /**
     * Called when a router error occurs.
     * This means we can't continue and have to restart the connection.
     * @param e
     */
    public void onRouterError(RouterErrorEvent e) {
        //Printing the error.
        if(e.exception.getMessage() != null) {
            logger.error("There was a problem with the UnderNet router: " + e.exception.getMessage());
        }
        e.exception.printStackTrace();

        //Resetting the network devices.
        e.router.stop();

        //Reconnecting if possible.
        if(e.router.status != RouterStatus.STOPPED && e.shouldReconnect) {
            reconnectNum++;
            //Checking if we should reconnect.
            if(reconnectNum >= 5) {
                logger.error("Exceeded the maximum number of reconnect attempts!");
                onConnectionEnded();
            }

            logger.info("Attempting to reconnect for: " + reconnectNum + " time...");
        }
    }
}
