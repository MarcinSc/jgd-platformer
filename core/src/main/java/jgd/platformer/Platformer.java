package jgd.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.FPSLogger;

public class Platformer extends ApplicationAdapter {
    private FPSLogger fpsLogger;

    @Override
    public void create() {
        fpsLogger = new FPSLogger();
    }

    @Override
    public void render() {
        fpsLogger.log();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
    }
}