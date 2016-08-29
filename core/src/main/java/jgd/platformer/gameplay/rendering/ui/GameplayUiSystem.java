package jgd.platformer.gameplay.rendering.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.gempukku.gaming.asset.texture.TextureAtlasProvider;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.gaming.rendering.ui.StageProvider;
import com.gempukku.gaming.time.TimeManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import com.gempukku.secsy.entity.game.GameLoopUpdate;
import jgd.platformer.gameplay.logic.PlayerComponent;
import jgd.platformer.gameplay.logic.health.LivesComponent;
import jgd.platformer.gameplay.logic.score.ScoreComponent;
import jgd.platformer.gameplay.player.AfterPlayerCreated;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}
)
public class GameplayUiSystem implements LifeCycleSystem {
    @Inject
    private StageProvider stageProvider;
    @Inject
    private TimeManager timeManager;
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private TextureAtlasProvider textureAtlasProvider;

    private BitmapFont labelFont;
    private Label timeLabel;
    private Label scoreLabel;

    private List<Image> heartImages;

    @Override
    public void initialize() {
        textureAtlasRegistry.registerTextures("gameplayUi", Arrays.asList("ui/Heart.png", "ui/Heart-empty.png"));

        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Life is goofy.ttf"));
        FreeTypeFontGenerator.FreeTypeFontParameter params = new FreeTypeFontGenerator.FreeTypeFontParameter();
        params.size = 20;
        params.color = Color.WHITE;
        params.borderColor = Color.BLACK;
        params.borderWidth = 1f;
        params.minFilter = Texture.TextureFilter.Linear;
        params.magFilter = Texture.TextureFilter.Linear;

        labelFont = generator.generateFont(params);

        generator.dispose();

        Label.LabelStyle labelStyle = new Label.LabelStyle(labelFont, Color.WHITE);

        timeLabel = new Label("0:00", labelStyle);
        scoreLabel = new Label("Score: 0", labelStyle);

        Label livesLabel = new Label("Lives: ", labelStyle);

        Table livesTable = new Table();
        livesTable.add(livesLabel);

        heartImages = new ArrayList<>(5);
        for (int i = 0; i < 5; i++) {
            Image heartImage = new Image();
            heartImages.add(heartImage);
            livesTable.add(heartImage).width(18).height(18);
        }

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.add(livesTable).pad(10).left().expandX();
        table.add(timeLabel).pad(10).center();
        table.add(scoreLabel).pad(10).right().expandX();

        stageProvider.getStage().addActor(table);
    }

    @ReceiveEvent
    public void modifyTime(GameLoopUpdate event, EntityRef entityRef) {
        long time = timeManager.getTime();
        int secondsSinceStart = (int) (time / 1000);
        int minutesToDisplay = secondsSinceStart / 60;
        int secondsToDisplay = secondsSinceStart - minutesToDisplay * 60;

        String toDisplay = minutesToDisplay + ":" +
                ((secondsToDisplay < 10) ? ("0" + secondsToDisplay) : secondsToDisplay);
        timeLabel.setText(toDisplay);
    }

    @ReceiveEvent
    public void scoreModified(AfterComponentUpdated event, EntityRef entity, PlayerComponent player, ScoreComponent score) {
        scoreLabel.setText("Score: " + score.getScore());
    }

    @ReceiveEvent
    public void playerLoaded(AfterPlayerCreated event, EntityRef entity, LivesComponent lives) {
        updateLives(lives);
        scoreLabel.setText("Score: " + 0);
    }

    @ReceiveEvent
    public void livesModified(AfterComponentUpdated event, EntityRef entity, PlayerComponent player, LivesComponent lives) {
        updateLives(lives);
    }

    private void updateLives(LivesComponent lives) {
        int livesCount = lives.getLivesCount();
        for (int i = 0; i < heartImages.size(); i++) {
            boolean fullHeart = i < livesCount;
            String heartTexture = fullHeart ? "ui/Heart.png" : "ui/Heart-empty.png";
            heartImages.get(i).setDrawable(new TextureRegionDrawable(textureAtlasProvider.getTexture("gameplayUi", heartTexture)));
        }
    }

    @Override
    public void postDestroy() {
        labelFont.dispose();
    }
}
