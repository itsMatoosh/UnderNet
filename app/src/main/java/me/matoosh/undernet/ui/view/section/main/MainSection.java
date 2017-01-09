package me.matoosh.undernet.ui.view.section.main;

import android.animation.Animator;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.ui.view.ViewType;
import me.matoosh.undernet.ui.view.section.TabbedSection;
import me.matoosh.undernet.ui.view.section.communities.CommunitiesSection;

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
        registeredTabs = new ArrayList<ITab>();
        registeredTabs.add(new CameraTab());
        registeredTabs.add(new FriendsTab());

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
    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void handleCommunitiesRevealAnim(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Making sure no accidental swipes happen.
        if(!isTransitioning && distanceY < -20f && Math.abs(distanceX) < 10f) {
            //Getting the main view of the communities section.
            View communitiesView = MainActivity.viewManager.sections[1].mainView;
            if(communitiesView == null) {
                Log.e(TAG, "Communities main view couldn't be found.");
            }

            //Creating the animator for this view (the start radius is zero)
            if(transitionAnimator == null) {
                //Calculating the final radius.
                float finalRadius = (float) Math.hypot(communitiesView.getHeight(), communitiesView.getWidth());
                transitionAnimator = ViewAnimationUtils.createCircularReveal(communitiesView, (int)e1.getX(), (int)e1.getY(), 0, finalRadius);
            }

            // make the view visible and start the animation
            communitiesView.setVisibility(View.VISIBLE);
            transitionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = true;
                    Log.d(TAG, "Transitioning to " + CommunitiesSection.TAG);
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = true;
                    Log.d(TAG, "Transitioning to " + CommunitiesSection.TAG);
                }
            });

            transitionAnimator.start();
        }
    }
}