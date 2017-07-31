package layout.section.commmunities;

import android.animation.Animator;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import layout.section.SectionType;
import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;

/**
 * The communities section of the app.
 */
public class CommunitiesSection extends Fragment {

    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;
    /**
     * Recycler view listing community cards.
     */
    private RecyclerView recyclerView;

    /**
     * The communities logger.
     */
    private Logger logger = LoggerFactory.getLogger(CommunitiesSection.class);

    //private OnFragmentInteractionListener mListener;

    public CommunitiesSection() {
        // Required empty public constructor
    }

    /**
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}.
     * <p>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>Any restored child fragments will be created before the base
     * <code>Fragment.onCreate</code> method returns.</p>
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Called to have the fragment instantiate its user interface view.
     * This is optional, and non-graphical fragments can return null (which
     * is the default implementation).  This will be called between
     * {@link #onCreate(Bundle)} and {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If you return a View from here, you will later be called in
     * {@link #onDestroyView} when the view is being released.
     *
     * @param inflater           The LayoutInflater object that can be used to inflate
     *                           any views in the fragment,
     * @param container          If non-null, this is the parent view that the fragment's
     *                           UI should be attached to.  The fragment should not add the view itself,
     *                           but this can be used to generate the LayoutParams of the view.
     * @param savedInstanceState If non-null, this fragment is being re-constructed
     *                           from a previous saved state as given here.
     * @return Return the View for the fragment's UI, or null.
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //Registering this section.
        MainActivity.instance.communitiesSection = this;

        //Setting the main view of the section.
        //recyclerView = (RecyclerView) MainActivity.instance.findViewById(R.id.communities_recycler_view);

        // Inflate the layout for this fragment
        View layout = inflater.inflate(R.layout.fragment_communites_section, container, false);
        layout.setVisibility(View.GONE);

        return layout;
    }

    /**
     * Called when a fragment is first attached to its context.
     * {@link #onCreate(Bundle)} will be called after this.
     *
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This
     * is called after {@link #onDestroy()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * Handles the show transition to this section.
     */
    public void show(float touchPosX, float touchPosY) {
        if(MainActivity.instance.isTransitioning) {
            return;
        }

        //Getting the main view of the new section.
        final View toView = getView();
        if(toView == null) {
            logger.error("Transition to section's main view couldn't be found.");
        }

        //Instantiating a new animator.
        Animator transitionAnimator = null;

        //Calculating the final radius.
        float finalRadius = (float) Math.hypot(toView.getHeight(), toView.getWidth());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            transitionAnimator = ViewAnimationUtils.createCircularReveal(toView, (int)touchPosX, (int)touchPosY, 0, finalRadius);
        } else {
            //TODO: Legacy transition.
        }

        //Make the view visible and start the animation
        //toView.setVisibility(View.VISIBLE);
        transitionAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //Setting the transitioning flag.
                MainActivity.instance.isTransitioning = true;
                logger.debug("Transitioning to the Communities section");
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                //Setting the transitioning flag.
                MainActivity.instance.currentSection = SectionType.COMMUNITIES;
                MainActivity.instance.isTransitioning = false;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                //Setting the transitioning flag.
                toView.setVisibility(View.GONE);
                MainActivity.instance.isTransitioning = false;
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                //Setting the transitioning flag.
                MainActivity.instance.isTransitioning = true;
                logger.debug("Transitioning to the Communities section");
            }
        });
        transitionAnimator.start();
    }
    /**
     * Handles the hide transition to the main section.
     */
    public void hide() {
        //Making sure no accidental swipes happen.
        if(!MainActivity.instance.isTransitioning) {
            //Getting the main section view.
            final View commView = this.getView();

            //Creating the animator for this view (the start radius is zero)
            if(transitionAnimator == null) {
                //Calculating the final radius.
                Rect bounds = new Rect();
                commView.getDrawingRect(bounds);
                int centerX = bounds.centerX();
                int centerY = bounds.centerY();
                int finalRadius = Math.max(bounds.width(), bounds.height());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    transitionAnimator = ViewAnimationUtils.createCircularReveal(commView, centerX, centerY, finalRadius, 0f);
                    transitionAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
                } else {
                    //TODO: Other backwards compatible animator.
                }
            }

            // make the view visible and start the animation
            transitionAnimator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                    //Setting the transitioning flag.
                    commView.setVisibility(View.VISIBLE);
                    MainActivity.instance.isTransitioning = true;
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    //Setting the transitioning flag.
                    MainActivity.instance.isTransitioning = false;
                    commView.setVisibility(View.GONE);
                    MainActivity.instance.currentSection = SectionType.MAIN;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    //Setting the transitioning flag.
                    MainActivity.instance.isTransitioning = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                    //Setting the transitioning flag.
                    commView.setVisibility(View.VISIBLE);
                    MainActivity.instance.isTransitioning = true;
                }
            });
            transitionAnimator.start();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    /*public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }*/
}
