package jgd.platformer.gameplay.rendering;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
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
import jgd.platformer.gameplay.logic.score.ScoreComponent;

@RegisterSystem(
        profiles = "gameplay"
)
public class GameplayUiSystem implements LifeCycleSystem {
    @Inject
    private StageProvider stageProvider;
    @Inject
    private TimeManager timeManager;

    private BitmapFont labelFont;
    private Label timeLabel;
    private Label scoreLabel;

    @Override
    public void initialize() {
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

        Table table = new Table();
        table.setFillParent(true);
        table.top();
        table.add(timeLabel).pad(10);
        table.add(scoreLabel).pad(10).right().fillX();

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

    @Override
    public void postDestroy() {
        labelFont.dispose();
    }
}
