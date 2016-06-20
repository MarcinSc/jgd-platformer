package jgd.platformer.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import jgd.platformer.Platformer;

public class DesktopLauncher {
    public static void main(String[] arg) {
        LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
        // Only for debug purposes, remove for production
        config.backgroundFPS = 0;
        config.foregroundFPS = 0;
        config.vSyncEnabled = false;
        new LwjglApplication(new Platformer(), config);
    }
}
