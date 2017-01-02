package me.matoosh.undernet.ui.view;

/**
 * Created by Mateusz Rębacz on 18.12.2016.
 */

public interface IView {
    /**
     * Called when the view is created.
     */
    public abstract void OnCreate();
    /**
     * Called when the view becomes visible
     */
    public abstract void OnVisible();

    /**
     * Called when the view becomes invisible.
     */
    public abstract void OnInvisible();
}
