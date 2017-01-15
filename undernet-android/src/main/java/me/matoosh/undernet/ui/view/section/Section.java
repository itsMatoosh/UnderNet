package me.matoosh.undernet.ui.view.section;

import android.view.GestureDetector;
import android.view.View;

/**
 * Represents a single section of the app.
 */
public class Section {
    /**
     * Main view of the section.
     */
    public View mainView;
    /**
     * Debugging tag of this section.
     */
    public static String TAG = "Unknown Section";

    /**
     * Gesture Detector of this section.
     */
    public GestureDetector gestureDetector;

    /**
     * Whether a transition is taking place to a different section.
     */
    public boolean isTransitioning = false;

    /**
     * Sets up the section.
     */
    public void setup() {}
}