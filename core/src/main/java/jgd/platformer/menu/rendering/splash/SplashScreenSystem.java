package jgd.platformer.menu.rendering.splash;

import com.badlogic.gdx.graphics.Color;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.rendering.backdrop.background.image.BackgroundImageComponent;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = "menu"
)
public class SplashScreenSystem implements LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;
    @Inject
    private TimeManager timeManager;

    private int shownIndex = -1;
    private SplashSeriesComponent splashSeriesComponent;

    @Override
    public void initialize() {
        EntityData splashSeriesPrefab = prefabManager.getPrefabByName("splashSeries");
        EntityRef splashSeries = entityManager.wrapEntityData(splashSeriesPrefab);

        splashSeriesComponent = splashSeries.getComponent(SplashSeriesComponent.class);
    }

    @ReceiveEvent
    public void updateDisplayedSplashScreens(GameLoopUpdate event, EntityRef entityRef) {
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

                        renderingEntity.send(new SplashScreenEnded());
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
        backgroundImage.setBackgroundColor(
                new Color(
                        Integer.parseInt(color[0]) / 255f,
                        Integer.parseInt(color[1]) / 255f,
                        Integer.parseInt(color[2]) / 255f,
                        1f));
    }
}
