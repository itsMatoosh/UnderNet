package me.matoosh.undernet.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Created by Mateusz Rębacz on 20.12.2016.
 */

public class TabAdapter extends FragmentPagerAdapter {
    public TabAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    /**
     * Returns the requested fragment.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return (Fragment)ViewManager.getView(position);
    }


    /**
     * Returns the total number of views.
     * @return
     */
    @Override
    public int getCount() {
        return ViewManager.registeredViews.size();
    }
}
