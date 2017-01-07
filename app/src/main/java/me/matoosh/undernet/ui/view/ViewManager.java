package me.matoosh.undernet.ui.view;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;

/**
 * Created by Mateusz Rębacz on 18.12.2016.
 */

public class ViewManager {
    /**
     * Sections of the app.
     */
    public Section[] sections;

    /**
     * Initializes the ViewManager.
     */
    public void init() {
        //Initializing the  sections.
        sections = new Section[2];
        sections[0] = new CenterSection();
        sections[1] = new CommunitiesSection();
        for (Section s:
             sections) {
            s.setup();
        }
    }

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
    /**
     * The central section of the app.
     */
    public class CenterSection extends Section {
        /**
         * Transition animator of this section.
         */
        private Animator transitionAnimator;
        /**
         * Whether the user is transitioning to the communities section.
         */
        private boolean isTransitioning = false;

        @Override
        public void setup() {
            //Creating the ArrayList.
            registeredViews = new ArrayList<IView>();

            //Registering a single view for each type of view.
            registeredViews.add(new CommunitiesFragment());
            registeredViews.add(new CameraFragment());
            registeredViews.add(new FriendsFragment());

            //Setting the default view.
            defaultView = registeredViews.get(1);

            //Setting up the main section pager.
            pager = (ViewPager)MainActivity.instance.findViewById(R.id.main_pager);
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
                //Setting the transitioning flag.
                isTransitioning = true;
                View communitiesPager = sections[1].pager;
                //Creating the animator for this view (the start radius is zero)
                if(transitionAnimator == null) {
                    //Calculating the final radius.
                    float finalRadius = (float) Math.hypot(communitiesPager.getHeight(), communitiesPager.getWidth());
                    transitionAnimator = ViewAnimationUtils.createCircularReveal(sections[1].pager, (int)e1.getX(), (int)e1.getY(), 0, finalRadius);
                }

                // make the view visible and start the animation
                communitiesPager.setVisibility(View.VISIBLE);
                transitionAnimator.start();
            } else if(isTransitioning) {
                //Change the state of the animation.
                transitionAnimator.pause();
            }
        }

        /**
         * Handles the cancellation of the reveal transition to the communities section.
         */
        private void handleCommunitiesRevealCanceled() {

        }
    }

    /**
     * The communities section of the app.
     */
    public class CommunitiesSection extends Section{
        public void setup() {
            //Creating the ArrayList.
            registeredViews = new ArrayList<IView>();

            //Registering a single view for each type of view.
            registeredViews.add(new CommunitiesFragment());
            registeredViews.add(new CameraFragment());
            registeredViews.add(new FriendsFragment());

            //Setting the default view.
            defaultView = registeredViews.get(1);

            //Setting up the main section pager.
            pager = (ViewPager)MainActivity.instance.findViewById(R.id.communities_pager);
            if(pager == null) {
                Log.println(Log.ERROR, "ViewManager", "No communities pager!");
            }
            TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager(), this);
            pager.setAdapter(tabAdapter);

        }
    }
}
