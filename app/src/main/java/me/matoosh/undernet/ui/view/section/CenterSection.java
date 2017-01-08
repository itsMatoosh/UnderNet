package me.matoosh.undernet.ui.view.section;

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
import me.matoosh.undernet.ui.view.CameraFragment;
import me.matoosh.undernet.ui.view.FriendsFragment;
import me.matoosh.undernet.ui.view.IView;
import me.matoosh.undernet.ui.view.TabAdapter;
import me.matoosh.undernet.ui.view.ViewManager;
import me.matoosh.undernet.ui.view.ViewType;

/**
 * The central section of the app.
 */
public class CenterSection extends Section {
    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;

    @Override
    public void setup() {
        //Creating the ArrayList.
        registeredViews = new ArrayList<IView>();

        //Registering a single view for each type of view.
        registeredViews.add(new CameraFragment());
        registeredViews.add(new FriendsFragment());

        //Setting the default view.
        defaultView = registeredViews.get(0);

        //Setting up the main section pager.
        pager = (ViewPager) MainActivity.instance.findViewById(R.id.main_pager);
        if(pager == null) {
            Log.println(Log.ERROR, "ViewManager", "No view pager!");
        }
        TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager(), this);
        pager.setAdapter(tabAdapter);
        setView(ViewType.CAMERA);

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

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(isTransitioning) {
                        isTransitioning  = false;
                        handleCommunitiesRevealCanceled();
                    };
                }
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
            View communitiesPager = MainActivity.viewManager.sections[1].pager;
            //Creating the animator for this view (the start radius is zero)
            if(transitionAnimator == null) {
                //Calculating the final radius.
                float finalRadius = (float) Math.hypot(communitiesPager.getHeight(), communitiesPager.getWidth());
                transitionAnimator = ViewAnimationUtils.createCircularReveal(communitiesPager, (int)e1.getX(), (int)e1.getY(), 0, finalRadius);
            }

            // make the view visible and start the animation
            communitiesPager.setVisibility(View.VISIBLE);
            transitionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = true;
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
                }
            });
            transitionAnimator.start();
        }
    }

    /**
     * Handles the cancellation of the reveal transition to the communities section.
     */
    private void handleCommunitiesRevealCanceled() {

    }
}