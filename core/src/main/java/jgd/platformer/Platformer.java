package jgd.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.gempukku.gaming.rendering.RenderingEngine;
import com.gempukku.gaming.time.InternalTimeManager;
import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import jgd.platformer.level.LevelLoader;
import jgd.platformer.player.PlayerManager;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class Platformer extends ApplicationAdapter {
    private FPSLogger fpsLogger;
    private SECSyContext context;

    private long lastUpdateTime;

    private Collection<String> additionalProfiles;

    public Platformer(Collection<String> additionalProfiles) {
        this.additionalProfiles = additionalProfiles;
    }

    @Override
    public void create() {
        fpsLogger = new FPSLogger();

        Set<String> activeProfiles = new HashSet<>();
        activeProfiles.add("fivePhaseRenderer");
        activeProfiles.add("simpleEntityManager");
        activeProfiles.add("nameConventionComponents");
        activeProfiles.add("backgroundRenderer");
        activeProfiles.add("textureAtlas");
        activeProfiles.add("shapeProvider");
        activeProfiles.add("prefabManager");
        activeProfiles.add("annotationEventDispatcher");
        activeProfiles.add("simpleEntityIndexManager");
        activeProfiles.add("time");
        activeProfiles.addAll(additionalProfiles);

        Configuration scanBasedOnAnnotations = new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forJavaClassPath());

        context = new SECSyContext(activeProfiles, new Reflections(scanBasedOnAnnotations));
        context.startup();

        PlayerManager playerManager = context.getSystem(PlayerManager.class);
        playerManager.createPlayer();

        LevelLoader levelLoader = context.getSystem(LevelLoader.class);
        levelLoader.loadLevel("level-sample");
        levelLoader.loadLevel("level-sample2");

        System.out.println("Systems in context");
        for (Object system : context.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }

        lastUpdateTime = System.currentTimeMillis();
    }

    @Override
    public void render() {
        fpsLogger.log();

        long timePassed = System.currentTimeMillis() - lastUpdateTime;
        lastUpdateTime += timePassed;

        context.getSystem(InternalTimeManager.class).updateTime(timePassed);

        context.getSystem(InternalGameLoop.class).processUpdate();

        context.getSystem(RenderingEngine.class).render();
    }

    @Override
    public void resize(int width, int height) {
    }

    @Override
    public void dispose() {
    }
}