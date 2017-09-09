package me.matoosh.undernet.standalone;

import java.awt.EventQueue;

import me.matoosh.undernet.standalone.ui.AppFrame;

/**
 * A graphical wrapper for the desktop platforms.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class UnderNetStandalone {
    public static void main (String[] args) {
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppFrame();
            }
        });
    }
}
