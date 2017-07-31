package layout.section.main;

import android.animation.Animator;
import android.app.Fragment;
import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;

import layout.section.main.camera.CameraTab;
import layout.section.main.nodelist.NodeListFragment;
import layout.section.main.status.StatusTab;
import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.R;

/**
 * The main section of the app.
 * Manages the main section tabs.
 */
public class MainSection extends Fragment {
    /**
     * The listener of the fragment.
     */
    //private OnFragmentInteractionListener mListener;

    /**
     * The view pager, that handles the section's tabs.
     */
    public ViewPager pager;
    /**
     * The default tab.
     */
    public TabType defaultTab;

    /**
     * Currently visible tab.
     */
    public Tab currentTab;

    /**
     * List of the registered tabs.
     */
    public ArrayList<Tab> registeredTabs;

    /**
     * Transition animator of this section.
     */
    private Animator transitionAnimator;
    /**
     * Gesture detector of this section.
     */
    private GestureDetector gestureDetector;

    /**
     * The logger of this section.
     */
    public static Logger logger = LoggerFactory.getLogger(MainSection.class);


    public MainSection() {
        // Required empty public constructor
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
     * Called to do initial creation of a fragment.  This is called after
     * {@link #onAttach(Activity)} and before
     * {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}, but is not called if the fragment
     * instance is retained across Activity re-creation (see {@link #setRetainInstance(boolean)}).
     * <p>
     * <p>Note that this can be called while the fragment's activity is
     * still in the process of being created.  As such, you can not rely
     * on things like the activity's content view hierarchy being initialized
     * at this point.  If you want to do work once the activity itself is
     * created, see {@link #onActivityCreated(Bundle)}.
     * <p>
     * <p>If your app's <code>targetSdkVersion</code> is {@link Build.VERSION_CODES#M}
     * or lower, child fragments being restored from the savedInstanceState are restored after
     * <code>onCreate</code> returns. When targeting {@link Build.VERSION_CODES#N} or
     * above and running on an N or newer platform version
     * they are restored by <code>Fragment.onCreate</code>.</p>
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //Registering this section.
        MainActivity.instance.mainSection = this;

        //Inflating the layout.
        View layout = inflater.inflate(R.layout.fragment_main_section, container, false);
        layout.setVisibility(View.VISIBLE);

        //Registering tabs for this section.
        registeredTabs = new ArrayList<Tab>();
        registeredTabs.add(new NodeListFragment());
        registeredTabs.add(new CameraTab());
        registeredTabs.add(new StatusTab());

        //Setting the default tab.
        defaultTab = TabType.CAMERA;

        //Setting up the main section pager.
        pager = (ViewPager) layout.findViewById(R.id.main_pager);
        if(pager == null) {
            logger.error("No view pager!");
        }
        TabAdapter tabAdapter = new TabAdapter(MainActivity.instance.getSupportFragmentManager(), this);
        if(tabAdapter == null) {
            logger.error("No tab adapter!");
        }
        pager.setAdapter(tabAdapter);
        setTab(defaultTab);

        //Setting up the communities pager transition.
        gestureDetector = new GestureDetector(layout.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                //Handling the communities section transition.
                handleCommunitiesRevealAnim(e1, e2, distanceX, distanceY);

                return super.onScroll(e1, e2, distanceX, distanceY);
            }

        });
        //Adding the touch listener to the gesture detector.
        pager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);

                return false;
            }
        });
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                onTabChanged(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        return layout;
    }

    /**
     * Called when the fragment's activity has been created and this
     * fragment's view hierarchy instantiated.  It can be used to do final
     * initialization once these pieces are in place, such as retrieving
     * views or restoring state.  It is also useful for fragments that use
     * {@link #setRetainInstance(boolean)} to retain their instance,
     * as this callback tells the fragment when it is fully associated with
     * the new activity instance.  This is called after {@link #onCreateView}
     * and before {@link #onViewStateRestored(Bundle)}.
     *
     * @param savedInstanceState If the fragment is being re-created from
     *                           a previous saved state, this is the state.
     */
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * Called when the fragment is no longer attached to its activity.  This is called after
     * {@link #onDestroy()}, except in the cases where the fragment instance is retained across
     * Activity re-creation (see {@link #setRetainInstance(boolean)}), in which case it is called
     * after {@link #onStop()}.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * Sets the current tab of the section.
     * @param type
     */
    public void setTab(TabType type) {
        pager.setCurrentItem(type.ordinal());
        onTabChanged(type.ordinal());
    }
    public Tab getTab(int id) {
        return registeredTabs.get(id);
    }

    /**
     * Called when the tab changed.
     * @param position
     */
    private void onTabChanged(int position) {
        if(currentTab != null) {
            currentTab.OnInvisible();
        }
        currentTab = getTab(position);
        currentTab.OnVisible();
    }

    /**
     * Handles the reveal transition to the communities section.
     */
    private void handleCommunitiesRevealAnim(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        //Making sure no accidental swipes happen.
        if(distanceY < -20f && Math.abs(distanceX) < 10f) {
            getView().setVisibility(View.GONE);
            MainActivity.instance.communitiesSection.show(e1.getX(), e1.getY());
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
