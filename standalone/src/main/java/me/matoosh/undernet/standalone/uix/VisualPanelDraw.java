package me.matoosh.undernet.standalone.uix;

import me.matoosh.undernet.UnderNet;
import me.matoosh.undernet.event.Event;
import me.matoosh.undernet.event.EventHandler;
import me.matoosh.undernet.event.EventManager;
import me.matoosh.undernet.event.channel.message.MessageReceivedEvent;
import me.matoosh.undernet.p2p.node.Node;
import me.matoosh.undernet.p2p.router.InterfaceStatus;
import me.matoosh.undernet.standalone.UnderNetStandalone;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;

public class VisualPanelDraw extends JPanel {

    /**
     * The nodes that recently sent messages to self.
     */
    public static HashMap<Node, Long> recentlyReceived = new HashMap<>();

    public VisualPanelDraw() {
        registerCallbacks();
    }

    private void registerCallbacks() {
        EventManager.registerHandler(new EventHandler() {
            @Override
            public void onEventCalled(Event e) {
                MessageReceivedEvent messageReceivedEvent = (MessageReceivedEvent) e;

                //coloring the line to the node.
                recentlyReceived.put(messageReceivedEvent.forwarder, System.currentTimeMillis() + 1000);
            }
        }, MessageReceivedEvent.class);
    }

    @Override
    protected void paintComponent(Graphics g) {
        //background
        g.setColor(Color.black);
        g.fillRect(0,0,this.getWidth(),this.getHeight());

        //other
        if(UnderNet.router.status == InterfaceStatus.STARTED) {
            drawOtherNodes(g);
        }

        //self
        drawSelfNode(g);
    }

    private void drawSelfNode(Graphics g) {
        Color fill = Color.GRAY;
        if(UnderNet.router != null) {
            switch (UnderNet.router.status) {
                case STOPPED:
                    fill = Color.gray;
                    break;
                case STARTED:
                    fill = Color.green;
                    break;
                default:
                    fill = Color.orange;
                    break;
            }
        }

        int diam = 40;
        g.setColor(fill);
        g.fillOval(getWidth()/2 - diam/2, getHeight()/2 - diam/2, diam, diam);
        g.setColor(Color.BLACK);
        g.drawOval(getWidth()/2 - diam/2, getHeight()/2 - diam/2, diam, diam);

        if(UnderNetStandalone.networkIdentity != null) {
            g.setColor(Color.WHITE);
            String identity = UnderNetStandalone.networkIdentity.getNetworkId().getStringValue().substring(0, 12) + "...";
            g.drawString(identity, getWidth()/2 - g.getFontMetrics().stringWidth(identity) / 2, getHeight()/2 + diam / 2 + g.getFontMetrics().getHeight() + 5);
        }
    }

    /**
     * Draws representations of other nodes.
     * @param g
     */
    private void drawOtherNodes(Graphics g) {
        double angle = (2 * Math.PI) / UnderNet.router.getRemoteNodes().size();
        int distance = 100;
        int diam = 30;

        double currAngle = ((double)(System.currentTimeMillis() % 5000) / 5000d) * (2 * Math.PI);
        for (int i = 0; i < UnderNet.router.getRemoteNodes().size(); i++) {

            int x = (int) (getWidth()/2 + distance * Math.cos(currAngle));
            int y = (int) (getHeight()/2 + distance * Math.sin(currAngle));

            //line
            Node n = UnderNet.router.getRemoteNodes().get(i);
            if(recentlyReceived.containsKey(n)) {
                g.setColor(Color.CYAN);

                if(System.currentTimeMillis() > recentlyReceived.get(n)) recentlyReceived.remove(n);
            } else {
                g.setColor(Color.GRAY);
            }
            g.drawLine(getWidth()/2, getHeight()/2, x, y);

            //circle
            g.setColor(Color.CYAN);
            g.fillOval( x - diam/2, y - diam/2, diam, diam);
            g.setColor(Color.BLACK);
            g.drawOval(getWidth()/2 + x - diam/2, getHeight()/2 + y - diam/2, diam, diam);

            //net id
            if(n.getIdentity() != null) {
                g.setColor(Color.WHITE);
                String identity = n.getIdentity().getNetworkId().getStringValue().substring(0, 10) + "...";
                g.drawString(identity, x - g.getFontMetrics().stringWidth(identity) / 2, y + diam / 2 + g.getFontMetrics().getHeight() + 5);
            }

            currAngle += angle;
        }
    }
}
