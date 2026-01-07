package org.je.game.android;

import com.jme3.app.AndroidHarness;
import org.je.game.game.Game;


public class AndroidLauncher extends AndroidHarness {

    public AndroidLauncher() {
        appClass = Game.class.getCanonicalName();
    }
}
