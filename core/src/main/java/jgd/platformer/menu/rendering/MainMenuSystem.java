package jgd.platformer.menu.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.NinePatchDrawable;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.gaming.rendering.ui.StageProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.menu.rendering.splash.SplashScreenEnded;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem(
        profiles = "menu"
)
public class MainMenuSystem implements LifeCycleSystem {
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;
    @Inject
    private StageProvider stageProvider;
    @Inject
    private RenderingEntityProvider renderingEntityProvider;

    private BitmapFont buttonFont;

    @Override
    public void initialize() {
        Set<String> uiImages = new HashSet<>();
        uiImages.add("ui/Button.png");
        uiImages.add("ui/ButtonOver.png");

        textureAtlasRegistry.registerTextures("ui", uiImages);

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Life is goofy.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 40;
        params.color = Color.WHITE;
        params.borderColor = Color.BLACK;
        params.borderWidth = 3f;
        params.minFilter = Texture.TextureFilter.Linear;
        params.magFilter = Texture.TextureFilter.Linear;

        buttonFont = generator.generateFont(params);

        generator.dispose();
    }

    @Override
    public void postDestroy() {
        buttonFont.dispose();
    }

    @ReceiveEvent
    public void splashSeriesFinished(SplashScreenEnded event, EntityRef entity) {
        NinePatch buttonUp = new NinePatch(textureAtlasProvider.getTexture("ui", "ui/Button.png"), 5, 5, 5, 5);
        NinePatch buttonOver = new NinePatch(textureAtlasProvider.getTexture("ui", "ui/ButtonOver.png"), 5, 5, 5, 5);

        TextButton.TextButtonStyle style = new TextButton.TextButtonStyle();
        style.up = new NinePatchDrawable(buttonUp);
        style.over = new NinePatchDrawable(buttonOver);
        style.font = buttonFont;

        TextButton textButton = new TextButton("New game", style);

        textButton.addListener(
                new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        renderingEntityProvider.getRenderingEntity().send(new NewGameRequested());
                    }
                });

        Table table = new Table();
        table.setFillParent(true);
        table.add(textButton);

        stageProvider.getStage().addActor(table);
    }
}
