package me.matoosh.undernet.ui.view;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.matoosh.undernet.MainActivity;
import me.matoosh.undernet.ui.view.section.Section;

/**
 * Created by Mateusz Rębacz on 20.12.2016.
 */

public class TabAdapter extends FragmentPagerAdapter {
    /**
     * Section using this TabAdapter.
     */
    private Section section;

    public TabAdapter(FragmentManager fragmentManager, Section section) {
        super(fragmentManager);
        this.section = section;
    }

    /**
     * Returns the requested fragment.
     * @param position
     * @return
     */
    @Override
    public Fragment getItem(int position) {
        return (Fragment) section.getView(position);
    }


    /**
     * Returns the total number of views.
     * @return
     */
    @Override
    public int getCount() {
        return section.registeredViews.size();
    }
}
