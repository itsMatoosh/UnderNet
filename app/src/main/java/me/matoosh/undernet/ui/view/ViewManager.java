package me.matoosh.undernet.ui.view;

import android.support.v4.view.ViewPager;
import android.util.Log;
import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;

/**
 * Created by Mateusz Rębacz on 18.12.2016.
 */

public class ViewManager {

    /**
     * Default view of the app.
     */
    public static IView defaultView;

    /**
     * Currently visible view.
     */
    public static IView currentView;

    /**
     * List of the registered views.
     */
    public static ArrayList<IView> registeredViews;

    /**
     * View pager of the main activity.
     */
    private static ViewPager viewPager;

    /**
     * Initializes the ViewManager.
     */
    public static void init() {
        //Creating the ArrayList.
        registeredViews = new ArrayList<IView>();

        //Registering a single view for each type of view.
        registeredViews.add(new CommunitiesFragment());
        registeredViews.add(new CameraFragment());
        registeredViews.add(new FriendsFragment());

        //Setting the default view.
        defaultView = registeredViews.get(1);

        //Setting up the ViewPager.
        viewPager = (ViewPager)MainActivity.instance.findViewById(R.id.main_pager);
        if(viewPager == null) {
            Log.println(Log.ERROR, "ViewManager", "No view pager!");
        }
        TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        viewPager.setCurrentItem(1);
    }

    /**
     * Sets the current view of the app.
     * @param type
     */
    public static void setView(ViewType type) {
        if(currentView != null) {
            currentView.OnInvisible();
        }
        currentView = registeredViews.get(type.ordinal());
        currentView.OnVisible();
    }
    public static IView getView(int id) {
        return registeredViews.get(id);
    }
}
