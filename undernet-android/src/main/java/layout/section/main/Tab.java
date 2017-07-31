package layout.section.main;

import android.support.v4.app.Fragment;

/**
 * Represents a tab from a tabbed section.
 * Created by Mateusz Rębacz on 22.02.2017.
 */

public abstract class Tab extends Fragment {
    /**
     * Called when the view becomes visible
     */
    public abstract void OnVisible();

    /**
     * Called when the view becomes invisible.
     */
    public abstract void OnInvisible();

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (isVisibleToUser) {
            OnVisible();
        } else {
            OnInvisible();
        }
    }*/
}
