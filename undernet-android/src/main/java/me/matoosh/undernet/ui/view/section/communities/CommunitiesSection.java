package me.matoosh.undernet.ui.view.section.communities;

import android.animation.Animator;
import android.graphics.Rect;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import java.util.ArrayList;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;
import me.matoosh.undernet.ui.view.section.main.ITab;
import me.matoosh.undernet.ui.view.section.main.TabAdapter;
import me.matoosh.undernet.ui.view.section.Section;

/**
 * The communities section of the app.
 */
public class CommunitiesSection extends Section {
    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;
    /**
     * Recycler view listing community cards.
     */
    private RecyclerView recyclerView;

    public void setup() {
        //Setting the tag for this section.
        TAG = "Communities";

        //Setting the main view of the section.
        recyclerView = (RecyclerView)MainActivity.instance.findViewById(R.id.communities_recycler_view);
        mainView = (View)MainActivity.instance.findViewById(R.id.communities_layout);
    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void hide() {
        //Making sure no accidental swipes happen.
        if(!isTransitioning) {
            final View mainView = MainActivity.viewManager.sections[0].mainView;
            //Creating the animator for this view (the start radius is zero)
            if(transitionAnimator == null) {
                //Calculating the final radius.
                Rect bounds = new Rect();
                mainView.getDrawingRect(bounds);
                int centerX = bounds.centerX();
                int centerY = bounds.centerY();
                int finalRadius = Math.max(bounds.width(), bounds.height());

                transitionAnimator = ViewAnimationUtils.createCircularReveal(mainView, centerX, centerY, 0f, finalRadius);
            }

            // make the view visible and start the animation
            transitionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Setting the transitioning flag.
                    mainView.setVisibility(View.VISIBLE);
                    isTransitioning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                    mainView.setVisibility(View.INVISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Setting the transitioning flag.
                    isTransitioning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Setting the transitioning flag.
                    mainView.setVisibility(View.VISIBLE);
                    isTransitioning = true;
                }
            });
            transitionAnimator.start();
        }
    }
}