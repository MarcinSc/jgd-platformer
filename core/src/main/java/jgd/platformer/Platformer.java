package jgd.platformer;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.FPSLogger;
import com.gempukku.gaming.rendering.RenderingEngine;
import com.gempukku.secsy.context.SECSyContext;
import com.gempukku.secsy.entity.game.InternalGameLoop;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;

import java.util.HashSet;
import java.util.Set;

public class Platformer extends ApplicationAdapter {
    private FPSLogger fpsLogger;
    private SECSyContext context;

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

        Configuration scanBasedOnAnnotations = new ConfigurationBuilder()
                .setScanners(new TypeAnnotationsScanner())
                .setUrls(ClasspathHelper.forJavaClassPath());

        context = new SECSyContext(activeProfiles, new Reflections(scanBasedOnAnnotations));
        context.startup();

        System.out.println("Systems in context");
        for (Object system : context.getSystems()) {
            System.out.println(system.getClass().getSimpleName());
        }
    }

    @Override
    public void render() {
        fpsLogger.log();

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