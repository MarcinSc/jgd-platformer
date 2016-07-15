package jgd.platformer.menu.rendering;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.rendering.backdrop.background.image.BackgroundImageComponent;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.game.GameLoop;
import com.gempukku.secsy.entity.game.GameLoopListener;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.menu.SplashSeriesComponent;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem(
        profiles = "menu"
)
public class SplashScreenSystem implements GameLoopListener, LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private GameLoop gameLoop;

    private int shownIndex = -1;
    private SplashSeriesComponent splashSeriesComponent;

    @Override
    public void initialize() {
        gameLoop.addGameLoopListener(this);

        EntityData splashSeriesPrefab = prefabManager.getPrefabByName("splashSeries");
        EntityRef splashSeries = entityManager.wrapEntityData(splashSeriesPrefab);

        Set<String> imagePaths = new HashSet<>();
        splashSeriesComponent = splashSeries.getComponent(SplashSeriesComponent.class);

        for (String splashDescription : splashSeriesComponent.getSplashDescriptions()) {
            String imagePath = splashDescription.split("\\|")[0];
            imagePaths.add(imagePath);
        }

        textureAtlasRegistry.registerTextures(splashSeriesComponent.getTextureAtlasId(), imagePaths);
    }

    @Override
    public void update() {
        if (shownIndex == -1) {
            shownIndex = 0;

            EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
            BackgroundImageComponent backgroundImage = renderingEntity.createComponent(BackgroundImageComponent.class);

            backgroundImage.setTextureAtlasId(splashSeriesComponent.getTextureAtlasId());
            backgroundImage.setPaddingLeft(0.5f);
            backgroundImage.setPaddingRight(0.5f);
            backgroundImage.setPaddingTop(0.5f);
            backgroundImage.setPaddingBottom(0.5f);

            setBasedOnSplashDescription(backgroundImage);
            renderingEntity.saveChanges();
        } else {
            if (shownIndex < splashSeriesComponent.getSplashDescriptions().size()) {
                long time = timeManager.getTime();
                if (time > Long.parseLong(splashSeriesComponent.getSplashDescriptions().get(shownIndex).split("\\|")[1])) {
                    shownIndex++;

                    if (shownIndex >= splashSeriesComponent.getSplashDescriptions().size()) {
                        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
                        renderingEntity.removeComponents(BackgroundImageComponent.class);
                        renderingEntity.saveChanges();
                    } else {
                        EntityRef renderingEntity = renderingEntityProvider.getRenderingEntity();
                        BackgroundImageComponent backgroundImage = renderingEntity.getComponent(BackgroundImageComponent.class);
                        setBasedOnSplashDescription(backgroundImage);
                        renderingEntity.saveChanges();
                    }
                }
            }
        }
    }

    private void setBasedOnSplashDescription(BackgroundImageComponent backgroundImage) {
        String[] splashDescription = splashSeriesComponent.getSplashDescriptions().get(shownIndex).split("\\|");
        backgroundImage.setTextureName(splashDescription[0]);
        String[] color = splashDescription[2].split(",");
        backgroundImage.setBackgroundRed(Integer.parseInt(color[0]));
        backgroundImage.setBackgroundGreen(Integer.parseInt(color[1]));
        backgroundImage.setBackgroundBlue(Integer.parseInt(color[2]));
    }
}
