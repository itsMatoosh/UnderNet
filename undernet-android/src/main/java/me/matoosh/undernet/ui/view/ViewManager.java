package me.matoosh.undernet.ui.view;

import android.animation.Animator;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;

import me.matoosh.undernet.ui.view.section.Section;
import me.matoosh.undernet.ui.view.section.communities.CommunitiesSection;
import me.matoosh.undernet.ui.view.section.main.MainSection;

/**
 * Manages the sections of the app.
 * Created by Mateusz Rębacz on 18.12.2016.
 */

public class ViewManager {
    /**
     * Sections of the app.
     */
    public Section[] sections;
    /**
     * Currently open section.
     */
    public Section currentSection;
    /**
     * Whether the app is currently transitioning between sections.
     */
    public boolean isTransitioning = false;

    /**
     * Debug tag of this class.
     */
    public final String TAG = "View Manager";

    /**
     * Initializes the ViewManager.
     */
    public void init() {
        //Initializing the  sections.
        sections = new Section[2];
        sections[0] = new MainSection();
        sections[1] = new CommunitiesSection();
        currentSection = sections[0];
        for (Section s:
             sections) {
            s.setup();
        }
    }

    /**
     * Transitions the current view to the specified section.
     * @param section
     */
    public void transitionTo(final Section section, float posX, float posY) {
        if(isTransitioning || currentSection == section) {
            return;
        }

        //Getting the main view of the new section.
        View toView = section.mainView;
        if(toView == null) {
            Log.e(TAG, "Transition to section's main view couldn't be found.");
        }

        //Instantiating a new animator.
        Animator transitionAnimator = null;

        //Calculating the final radius.
        float finalRadius = (float) Math.hypot(toView.getHeight(), toView.getWidth());
        transitionAnimator = ViewAnimationUtils.createCircularReveal(toView, (int)posX, (int)posY, 0, finalRadius);

        // make the view visible and start the animation
        toView.setVisibility(View.VISIBLE);
        transitionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Setting the transitioning flag.
                isTransitioning = true;
                Log.d(TAG, "Transitioning to " + section.TAG);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Setting the transitioning flag.
                currentSection.mainView.setVisibility(View.INVISIBLE);
                currentSection = section;
                isTransitioning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Setting the transitioning flag.
                currentSection.mainView.setVisibility(View.GONE);
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
