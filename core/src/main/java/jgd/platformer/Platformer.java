package jgd.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputEventQueue;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.FPSLogger;
import com.gempukku.gaming.rendering.RenderingEngine;
import com.gempukku.gaming.rendering.ui.UiProcessor;
import com.gempukku.gaming.time.InternalTimeManager;
import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import jgd.platformer.gameplay.logic.physics.PhysicsEngine;
import jgd.platformer.menu.GameState;
import org.reflections.util.ClasspathHelper;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Platformer extends ApplicationAdapter {
    private FPSLogger fpsLogger;
    private SECSyContext gameplayContext;
    private SECSyContext menuContext;

    private long lastUpdateTime;

    private Collection<String> additionalProfiles;
    private InputEventQueue inputEventQueue;

    public Platformer(Collection<String> additionalProfiles) {
        this.additionalProfiles = additionalProfiles;
    }

    @Override
    public void create() {
        fpsLogger = new FPSLogger();

        Set<URL> urlsToScan = new HashSet<>();
        urlsToScan.addAll(ClasspathHelper.forPackage("com.gempukku", ClasspathHelper.contextClassLoader()));
        urlsToScan.addAll(ClasspathHelper.forPackage("jgd.platformer", ClasspathHelper.contextClassLoader()));

        menuContext = createMenuContext(urlsToScan);
        gameplayContext = createGameplayContext(urlsToScan);

        inputEventQueue = new InputEventQueue();
        Gdx.input.setInputProcessor(inputEventQueue);

        lastUpdateTime = System.currentTimeMillis();
    }

    private SECSyContext createMenuContext(Collection<URL> urlsToScan) {
        Set<String> menuActiveProfiles = new HashSet<>();
        menuActiveProfiles.add("menu");
        menuActiveProfiles.add("gameLoop");
        menuActiveProfiles.add("fivePhaseRenderer");
        menuActiveProfiles.add("simpleEntityManager");
        menuActiveProfiles.add("nameConventionComponents");
        menuActiveProfiles.add("textureAtlas");
        menuActiveProfiles.add("prefabManager");
        menuActiveProfiles.add("annotationEventDispatcher");
        menuActiveProfiles.add("simpleEntityIndexManager");
        menuActiveProfiles.add("time");
        menuActiveProfiles.add("stageUi");
        menuActiveProfiles.addAll(additionalProfiles);

        SECSyContext menuContext = new SECSyContext(menuActiveProfiles, urlsToScan);
        menuContext.startup();

        System.out.println("Systems in menu context");
        for (Object system : menuContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return menuContext;
    }

    private SECSyContext createGameplayContext(Collection<URL> urlsToScan) {
        Set<String> gameplayActiveProfiles = new HashSet<>();
        gameplayActiveProfiles.add("gameplay");
        gameplayActiveProfiles.add("gameLoop");
        gameplayActiveProfiles.add("fivePhaseRenderer");
        gameplayActiveProfiles.add("simpleEntityManager");
        gameplayActiveProfiles.add("nameConventionComponents");
        gameplayActiveProfiles.add("textureAtlas");
        gameplayActiveProfiles.add("shapeProvider");
        gameplayActiveProfiles.add("prefabManager");
        gameplayActiveProfiles.add("annotationEventDispatcher");
        gameplayActiveProfiles.add("simpleEntityIndexManager");
        gameplayActiveProfiles.add("time");
        gameplayActiveProfiles.add("stageUi");
        gameplayActiveProfiles.add("ai");
        gameplayActiveProfiles.add("delayActions");
        gameplayActiveProfiles.add("entitySpawner");
        gameplayActiveProfiles.add("eventInputProcessor");
        gameplayActiveProfiles.addAll(additionalProfiles);

        SECSyContext gameplayContext = new SECSyContext(gameplayActiveProfiles, urlsToScan);
        gameplayContext.startup();

        System.out.println("Systems in gameplay context");
        for (Object system : gameplayContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return gameplayContext;
    }

    @Override
    public void render() {
        fpsLogger.log();

        if (menuContext.getSystem(GameState.class).shouldShowMenu(gameplayContext)) {
            long currentTime = System.currentTimeMillis();
            long timePassed = Math.min(currentTime - lastUpdateTime, 30);
            lastUpdateTime = currentTime;

            menuContext.getSystem(InternalTimeManager.class).updateTime(timePassed);

            menuContext.getSystem(UiProcessor.class).processUi(inputEventQueue);

            menuContext.getSystem(InternalGameLoop.class).processUpdate();

            menuContext.getSystem(RenderingEngine.class).render();
        } else {
            long currentTime = System.currentTimeMillis();
            long timePassed = Math.min(currentTime - lastUpdateTime, 30);
            lastUpdateTime = currentTime;

            gameplayContext.getSystem(InternalTimeManager.class).updateTime(timePassed);

            gameplayContext.getSystem(PhysicsEngine.class).processPhysics();

            inputEventQueue.setProcessor(gameplayContext.getSystem(InputProcessor.class));
            inputEventQueue.drain();

            gameplayContext.getSystem(UiProcessor.class).processUi();

            gameplayContext.getSystem(InternalGameLoop.class).processUpdate();

            gameplayContext.getSystem(RenderingEngine.class).render();
        }
    }

    @Override
    public void resize(int width, int height) {
        menuContext.getSystem(RenderingEngine.class).screenResized(width, height);
        gameplayContext.getSystem(RenderingEngine.class).screenResized(width, height);
    }

    @Override
    public void dispose() {
    }
}