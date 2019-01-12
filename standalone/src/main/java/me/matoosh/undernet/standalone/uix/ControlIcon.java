package me.matoosh.undernet.standalone.uix;

import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.router.RouterControlLoopEvent;

import javax.swing.*;
import java.awt.*;

/**
 * Represents the corner control icon.
 */
public class ControlIcon extends JPanel {

    private long greenTill;

    public ControlIcon () {
        this.setSize(this.getHeight(), this.getHeight());
        registerListeners();
    }

    private void registerListeners() {
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                greenTill = System.currentTimeMillis() + 1000;
            }
        }, RouterControlLoopEvent.class);
    }

    @Override
    protected void paintComponent(Graphics g) {
        g.drawRect(0,0,getWidth(),getHeight());
        g.fillOval(0,0, getWidth(), getHeight());
    }
}
