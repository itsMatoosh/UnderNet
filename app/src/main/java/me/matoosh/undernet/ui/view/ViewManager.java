package me.matoosh.undernet.ui.view;

import me.matoosh.undernet.ui.view.section.CenterSection;
import me.matoosh.undernet.ui.view.section.CommunititesSection;
import me.matoosh.undernet.ui.view.section.Section;

/**
 * Created by Mateusz Rębacz on 18.12.2016.
 */

public class ViewManager {
    /**
     * Sections of the app.
     */
    public Section[] sections;

    /**
     * Initializes the ViewManager.
     */
    public void init() {
        //Initializing the  sections.
        sections = new Section[2];
        sections[0] = new CenterSection();
        sections[1] = new CommunititesSection();
        for (Section s:
             sections) {
            s.setup();
        }
    }
}
