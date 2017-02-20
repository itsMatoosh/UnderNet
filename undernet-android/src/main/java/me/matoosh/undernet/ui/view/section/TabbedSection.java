package me.matoosh.undernet.ui.view.section;

import android.support.v4.view.ViewPager;

import java.util.ArrayList;

import me.matoosh.undernet.ui.view.ViewType;
import me.matoosh.undernet.ui.view.section.main.ITab;

/**
 * Section of the app divided into tabs.
 * Created by Mateusz Rębacz on 09.01.2017.
 */

public class TabbedSection extends Section {
    /**
     * Pager of this section.
     */
    public ViewPager pager;
    /**
     * Default tab of the section.
     */
    public ViewType defaultTab;

    /**
     * Currently visible tab.
     */
    public ITab currentView;

    /**
     * List of the registered tabs.
     */
    public ArrayList<ITab> registeredTabs;

    /**
     * Sets the current tab of the section.
     * @param type
     */
    public void setView(ViewType type) {
        if(currentView != null) {
            currentView.OnInvisible();
        }
        currentView = registeredTabs.get(type.ordinal());
        pager.setCurrentItem(type.ordinal());
        currentView.OnVisible();
    }
    public ITab getView(int id) {
        return registeredTabs.get(id);
    }

    @Override
    public void setup() {
        super.setup();
        for (ITab tab:
             registeredTabs) {
            tab.OnCreate();
        }
    }
}
