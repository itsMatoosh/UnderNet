package layout.section.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * Manages tabs within the main section.
 * Created by Mateusz Rębacz on 20.12.2016.
 */

public class TabAdapter extends FragmentPagerAdapter {
    /**
     * Section using this TabAdapter.
     */
    private MainSection section;

    public TabAdapter(FragmentManager fragmentManager, MainSection section) {
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
        return section.getTab(position);
    }


    /**
     * Returns the total number of views.
     * @return
     */
    @Override
    public int getCount() {
        return section.registeredTabs.size();
    }
}
