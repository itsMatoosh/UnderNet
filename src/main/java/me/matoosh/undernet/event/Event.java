package me.matoosh.undernet.event;

/**
 * Represents an Event.
 * Created by Mateusz Rębacz on 21.02.2017.
 */

public abstract class Event {
    /**
     * Executed when the event is called, but before it is delivered to the handlers.
     */
    public abstract void onCalled();
}
