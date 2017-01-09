package me.matoosh.undernet.ui.view;

import me.matoosh.undernet.ui.view.section.main.MainSection;
import me.matoosh.undernet.ui.view.section.communities.CommunitiesSection;
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
        sections[0] = new MainSection();
        sections[1] = new CommunitiesSection();
        for (Section s:
             sections) {
            s.setup();
        }
    }
}
