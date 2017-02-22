package me.matoosh.undernet.event;

/**
 * Represents an event handler.
 * Created by Mateusz Rębacz on 22.02.2017.
 */

public abstract class EventHandler {
    /**
     * Called when the handled event is called.
     * @param e
     */
    public abstract void onEventCalled(Event e);
}
