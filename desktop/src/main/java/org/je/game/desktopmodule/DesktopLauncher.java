package org.je.game.desktopmodule;

import org.je.game.game.Game;
import com.jme3.system.AppSettings;

/**
 * Used to launch a jme application in desktop environment
 *
 */
public class DesktopLauncher {
    public static void main(String[] args) {
        final Game game = new Game();

        final AppSettings appSettings = new AppSettings(true);
        appSettings.setFullscreen(true);
        game.setSettings(appSettings);
        game.setDisplayStatView(false);
        game.setDisplayFps(false);
        game.setShowSettings(false);
        game.start();
    }
}
