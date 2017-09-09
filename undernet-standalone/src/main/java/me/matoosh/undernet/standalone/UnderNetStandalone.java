package me.matoosh.undernet.standalone;

import java.awt.EventQueue;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.standalone.file.StandaloneFileManager;
import me.matoosh.undernet.standalone.ui.AppFrame;

/**
 * A graphical wrapper for the desktop platforms.
 * Created by Mateusz Rębacz on 09.09.2017.
 */

public class UnderNetStandalone {
    public static void main (String[] args) {
        //Setting up UnderNet.
        UnderNet.setup(new StandaloneFileManager());

        //Starting the ui.
        EventQueue.invokeLater(new Runnable() {
            @Override
            public void run() {
                new AppFrame();
            }
        });
    }
}
