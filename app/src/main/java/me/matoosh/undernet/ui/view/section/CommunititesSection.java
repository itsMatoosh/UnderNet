package me.matoosh.undernet.ui.view.section;

import android.animation.Animator;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.ui.view.CameraFragment;
import me.matoosh.undernet.ui.view.CommunitiesFragment;
import me.matoosh.undernet.ui.view.FriendsFragment;
import me.matoosh.undernet.ui.view.IView;
import me.matoosh.undernet.ui.view.TabAdapter;
import me.matoosh.undernet.ui.view.ViewManager;

/**
 * The communities section of the app.
 */
public class CommunititesSection extends Section {
    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;

    public void setup() {
        //Creating the ArrayList.
        registeredViews = new ArrayList<IView>();

        //Registering a single view for each type of view.
        registeredViews.add(new CommunitiesFragment());

        //Setting the default view.
        defaultView = registeredViews.get(0);

        //Setting up the main section pager.
        pager = (ViewPager) MainActivity.instance.findViewById(R.id.communities_pager);
        if(pager == null) {
            Log.println(Log.ERROR, "ViewManager", "No communities pager!");
        }
        TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager(), this);
        pager.setAdapter(tabAdapter);

    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void hide() {
        //Making sure no accidental swipes happen.
        if(!isTransitioning) {
            View mainPager = MainActivity.viewManager.sections[0].pager;
            //Creating the animator for this view (the start radius is zero)
            if(transitionAnimator == null) {
                //Calculating the final radius.
                Rect bounds = new Rect();
                mainPager.getDrawingRect(bounds);
                int centerX = bounds.centerX();
                int centerY = bounds.centerY();
                int finalRadius = Math.max(bounds.width(), bounds.height());

                transitionAnimator = ViewAnimationUtils.createCircularReveal(mainPager, centerX, centerY, 0f, finalRadius);
            }

            // make the view visible and start the animation
            transitionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Setting the transitioning flag.
                    pager.setVisibility(View.VISIBLE);
                    isTransitioning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                    pager.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Setting the transitioning flag.
                    pager.setVisibility(View.VISIBLE);
                    isTransitioning = true;
                }
            });
            transitionAnimator.start();
        }
    }
}