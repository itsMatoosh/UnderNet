package me.matoosh.undernet.ui.view.section.main;

import android.animation.Animator;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.ui.view.ViewType;
import me.matoosh.undernet.ui.view.section.TabbedSection;

/**
 * The main section of the app.
 */
public class MainSection extends TabbedSection {
    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;

    @Override
    public void setup() {
        //Setting the section tag.
        TAG = "MAIN";

        //Registering tabs for this section.
        registeredTabs = new ArrayList<Tab>();
        registeredTabs.add(new CameraTab());
        registeredTabs.add(new StatusTab());

        //Setting the default tab.
        defaultTab = ViewType.CAMERA;

        //Setting up the main section pager.
        pager = (ViewPager) MainActivity.instance.findViewById(R.id.main_pager);
        if(pager == null) {
            Log.println(Log.ERROR, TAG, "No view pager!");
        }
        mainView = pager;
        TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager(), this);
        pager.setAdapter(tabAdapter);
        setView(defaultTab);

        //Setting up the communities pager transition.
        gestureDetector = new GestureDetector(MainActivity.instance.getApplicationContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Handling the communities section transition.
                handleCommunitiesRevealAnim(e1, e2, distanceX, distanceY);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        });
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                return false;
            }
        });

        super.setup();
    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void handleCommunitiesRevealAnim(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Making sure no accidental swipes happen.
        if(distanceY < -20f && Math.abs(distanceX) < 10f) {
            MainActivity.viewManager.transitionTo(MainActivity.viewManager.sections[1] , e1.getX(), e1.getY());
        }
    }
}