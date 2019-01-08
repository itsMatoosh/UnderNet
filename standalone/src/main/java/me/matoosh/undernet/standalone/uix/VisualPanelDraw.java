package me.matoosh.undernet.standalone.uix;

import me.matoosh.undernet.UnderNet;

import javax.swing.*;
import java.awt.*;

public class VisualPanelDraw extends JPanel {

    @Override
    protected void paintComponent(Graphics g) {
        //background
        g.setColor(Color.black);
        g.fillRect(0,0,this.getWidth(),this.getHeight());

        //self
        drawSelfNode(g);
    }

    private void drawSelfNode(Graphics g) {
        if(UnderNet.router != null) {
            switch (UnderNet.router.status) {
                case STOPPED:
                    g.setColor(Color.gray);
                    break;
                case STARTED:
                    g.setColor(Color.green);
                    break;
                default:
                    g.setColor(Color.orange);
                    break;
            }
        }

        int ovalHeight = 50;
        int ovalWidth = 50;
        g.fillOval(getWidth()/2 - ovalWidth/2, getHeight()/2 - ovalHeight/2, ovalWidth, ovalHeight);
    }
}
