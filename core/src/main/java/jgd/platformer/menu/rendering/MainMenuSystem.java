package jgd.platformer.menu.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Slider;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
        uiImages.add("ui/sliderBackground.png");

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
        NinePatch sliderBackground = new NinePatch(textureAtlasProvider.getTexture("ui", "ui/sliderBackground.png"), 5, 5, 5, 5);

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
        table.add(textButton).colspan(2);

        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.background = new NinePatchDrawable(sliderBackground);
        sliderStyle.knob = new NinePatchDrawable(buttonUp);
        sliderStyle.knobOver = new NinePatchDrawable(buttonOver);

        Slider masterSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        masterSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        renderingEntityProvider.getRenderingEntity().send(new MasterVolumeSet(masterSlider.getValue()));
                    }
                });
        masterSlider.setValue(0.5f);

        Slider musicSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        musicSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        renderingEntityProvider.getRenderingEntity().send(new MusicVolumeSet(musicSlider.getValue()));
                    }
                });
        musicSlider.setValue(0.01f);

        Slider fxSlider = new Slider(0, 1, 0.01f, false, sliderStyle);
        fxSlider.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        renderingEntityProvider.getRenderingEntity().send(new FXVolumeSet(fxSlider.getValue()));
                    }
                });
        fxSlider.setValue(1f);

        table.row();

        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = buttonFont;

        table.add(new Label("Master Volume", labelStyle));
        table.add(masterSlider);

        table.row();

        table.add(new Label("Music Volume", labelStyle));
        table.add(musicSlider);

        table.row();

        table.add(new Label("FX Volume", labelStyle));
        table.add(fxSlider);

        stageProvider.getStage().addActor(table);
    }
}
