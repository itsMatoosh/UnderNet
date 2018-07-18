package me.matoosh.undernet.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents an Event.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public abstract class Event {

    /**
     * Logger of the event.
     */
    public Logger logger = LoggerFactory.getLogger(this.getClass());

    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    public abstract void onCalled();
}
