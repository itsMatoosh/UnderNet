package me.matoosh.undernet.ui.view.section;

import android.support.v4.view.ViewPager;
import android.view.GestureDetector;

import java.util.ArrayList;

import me.matoosh.undernet.ui.view.IView;
import me.matoosh.undernet.ui.view.ViewType;

/**
 * Represents a single section of the app.
 */
public class Section {
    /**
     * Pager of this section.
     */
    public ViewPager pager;
    /**
     * Gesture Detector of this section.
     */
    public GestureDetector gestureDetector;

    /**
     * Default view of the section.
     */
    public IView defaultView;

    /**
     * Currently visible view.
     */
    public IView currentView;

    /**
     * List of the registered views.
     */
    public ArrayList<IView> registeredViews;

    /**
     * Whether a transition is taking place to a different section.
     */
    public boolean isTransitioning = false;

    /**
     * Sets up the section.
     */
    public void setup() {}

    /**
     * Sets the current view of the app.
     * @param type
     */
    public void setView(ViewType type) {
        if(currentView != null) {
            currentView.OnInvisible();
        }
        currentView = registeredViews.get(type.ordinal());
        pager.setCurrentItem(type.ordinal());
        currentView.OnVisible();
    }
    public IView getView(int id) {
        return registeredViews.get(id);
    }

}