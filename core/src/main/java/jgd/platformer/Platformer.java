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
import com.gempukku.secsy.context.SystemContext;
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

    private SystemContext parentContext;
    private SystemContext gameplayContext;
    private SystemContext menuContext;
    private SystemContext editorContext;

    private long lastUpdateTime;

    private Collection<String> additionalProfiles;
    private InputEventQueue inputEventQueue;
    private Set<URL> urlsToScan;

    public Platformer(Collection<String> additionalProfiles) {
        this.additionalProfiles = additionalProfiles;
    }

    @Override
    public void create() {
        fpsLogger = new FPSLogger();

        urlsToScan = new HashSet<>();
        urlsToScan.addAll(ClasspathHelper.forPackage("com.gempukku", ClasspathHelper.contextClassLoader()));
        urlsToScan.addAll(ClasspathHelper.forPackage("jgd.platformer", ClasspathHelper.contextClassLoader()));

        parentContext = createParentContext(urlsToScan);
        menuContext = createMenuContext(urlsToScan);
        gameplayContext = createGameplayContext(urlsToScan);
        editorContext = createEditorContext(urlsToScan);

        inputEventQueue = new InputEventQueue();
        Gdx.input.setInputProcessor(inputEventQueue);

        lastUpdateTime = System.currentTimeMillis();
    }

    // "New game" will use a gameplay context with profiles: "gameScreen", "gameplay"
    // "Level editor" will use a editor context with profiles: "gameScreen", "editor"
    // Systems that want to exist for both will require "gameScreen" profile
    // Systems that should exist only in editor context use "gameScreen" and "editor" profile
    // Systems that should exist only in gameplay context use "gameScreen" and "gameplay"

    private SystemContext createParentContext(Collection<URL> urlsToScan) {
        Set<String> parentActiveProfiles = new HashSet<>();
        parentActiveProfiles.add("parent");
        parentActiveProfiles.add("nameComponentManager");
        parentActiveProfiles.add("nameConventionComponents");
        parentActiveProfiles.add("componentFieldConverter");
        parentActiveProfiles.add("prefabManager");
        parentActiveProfiles.add("textureAtlas");
        parentActiveProfiles.add("shapeProvider");

        SECSyContext parentContext = new SECSyContext(parentActiveProfiles, urlsToScan);
        parentContext.startup();

        System.out.println("Systems in parent context");
        for (Object system : parentContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return parentContext;
    }

    private SystemContext createMenuContext(Collection<URL> urlsToScan) {
        Set<String> menuActiveProfiles = new HashSet<>();
        menuActiveProfiles.add("menu");
        menuActiveProfiles.add("gameLoop");
        menuActiveProfiles.add("fivePhaseRenderer");
        menuActiveProfiles.add("simpleEntityManager");
        menuActiveProfiles.add("annotationEventDispatcher");
        menuActiveProfiles.add("simpleEntityIndexManager");
        menuActiveProfiles.add("time");
        menuActiveProfiles.add("stageUi");
        menuActiveProfiles.add("backgroundImage");
        menuActiveProfiles.addAll(additionalProfiles);

        SECSyContext menuContext = new SECSyContext(parentContext, menuActiveProfiles, urlsToScan);
        menuContext.startup();

        System.out.println("Systems in menu context");
        for (Object system : menuContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return menuContext;
    }

    private SystemContext createGameplayContext(Collection<URL> urlsToScan) {
        Set<String> gameplayActiveProfiles = new HashSet<>();
        gameplayActiveProfiles.add("gameScreen");
        gameplayActiveProfiles.add("gameplay");
        gameplayActiveProfiles.add("gameLoop");
        gameplayActiveProfiles.add("fivePhaseRenderer");
        gameplayActiveProfiles.add("backgroundColor");
        gameplayActiveProfiles.add("colorTint");
        gameplayActiveProfiles.add("simpleEntityManager");
        gameplayActiveProfiles.add("annotationEventDispatcher");
        gameplayActiveProfiles.add("simpleEntityIndexManager");
        gameplayActiveProfiles.add("time");
        gameplayActiveProfiles.add("stageUi");
        gameplayActiveProfiles.add("ai");
        gameplayActiveProfiles.add("delayActions");
        gameplayActiveProfiles.add("entitySpawner");
        gameplayActiveProfiles.add("eventInputProcessor");
        gameplayActiveProfiles.addAll(additionalProfiles);

        SECSyContext gameplayContext = new SECSyContext(parentContext, gameplayActiveProfiles, urlsToScan);
        gameplayContext.startup();

        System.out.println("Systems in gameplay context");
        for (Object system : gameplayContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return gameplayContext;
    }

    private SystemContext createEditorContext(Collection<URL> urlsToScan) {
        Set<String> editorActiveProfiles = new HashSet<>();
        editorActiveProfiles.add("gameScreen");
        editorActiveProfiles.add("editor");
        editorActiveProfiles.add("gameLoop");
        editorActiveProfiles.add("fivePhaseRenderer");
        editorActiveProfiles.add("backgroundColor");
        editorActiveProfiles.add("simpleEntityManager");
        editorActiveProfiles.add("annotationEventDispatcher");
        editorActiveProfiles.add("simpleEntityIndexManager");
        editorActiveProfiles.add("time");
        editorActiveProfiles.add("stageUi");
        editorActiveProfiles.add("entitySpawner");
        editorActiveProfiles.add("eventInputProcessor");
        editorActiveProfiles.addAll(additionalProfiles);

        SECSyContext editorContext = new SECSyContext(parentContext, editorActiveProfiles, urlsToScan);
        editorContext.startup();

        System.out.println("Systems in editor context");
        for (Object system : editorContext.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        return editorContext;
    }

    @Override
    public void render() {
        fpsLogger.log();

        GameState.Screen usedScreen = menuContext.getSystem(GameState.class).getUsedScreen(gameplayContext, editorContext);
        if (usedScreen == GameState.Screen.MAIN_MENU) {
            long currentTime = System.currentTimeMillis();
            long timePassed = Math.min(currentTime - lastUpdateTime, 30);
            lastUpdateTime = currentTime;

            menuContext.getSystem(InternalTimeManager.class).updateTime(timePassed);

            menuContext.getSystem(UiProcessor.class).processUi(inputEventQueue);

            menuContext.getSystem(InternalGameLoop.class).processUpdate();

            menuContext.getSystem(RenderingEngine.class).render();
        } else if (usedScreen == GameState.Screen.GAMEPLAY) {
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
        } else {
            long currentTime = System.currentTimeMillis();
            long timePassed = Math.min(currentTime - lastUpdateTime, 30);
            lastUpdateTime = currentTime;

            editorContext.getSystem(InternalTimeManager.class).updateTime(timePassed);

            inputEventQueue.setProcessor(editorContext.getSystem(InputProcessor.class));
            inputEventQueue.drain();

            editorContext.getSystem(UiProcessor.class).processUi();

            editorContext.getSystem(InternalGameLoop.class).processUpdate();

            editorContext.getSystem(RenderingEngine.class).render();
        }
    }

    @Override
    public void resize(int width, int height) {
        menuContext.getSystem(RenderingEngine.class).screenResized(width, height);
        gameplayContext.getSystem(RenderingEngine.class).screenResized(width, height);
        editorContext.getSystem(RenderingEngine.class).screenResized(width, height);
    }

    @Override
    public void dispose() {
    }
}