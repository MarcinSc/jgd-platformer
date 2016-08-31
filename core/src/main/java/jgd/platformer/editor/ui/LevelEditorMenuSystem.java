package jgd.platformer.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Align;
import com.gempukku.gaming.asset.prefab.NamedEntityData;
import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.ui.StageProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.level.AfterLevelLoaded;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class LevelEditorMenuSystem implements LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    @Inject
    private StageProvider stageProvider;

    private TextButton selectedBlockButton;

    private EntityRef levelEntity;

    @Override
    public void initialize() {
        Skin uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        Window blocksWindow = new Window("Blocks", uiSkin);
        blocksWindow.setResizable(true);
        blocksWindow.setMovable(true);

        Table blockTable = new Table(uiSkin);
        blockTable.top();
        blockTable.defaults().height(20).align(Align.left);

        ScrollPane scrollPane = new ScrollPane(blockTable, uiSkin);
        scrollPane.setFadeScrollBars(false);

        for (NamedEntityData namedEntityData : prefabManager.findNamedPrefabsWithComponents(BlockInEditorComponent.class)) {
            String prefabName = namedEntityData.getName();

            String displayName = entityManager.wrapEntityData(namedEntityData).getComponent(BlockInEditorComponent.class).getDisplayName();

            TextButton blockButton = new TextButton(displayName, uiSkin, "toggle");
            blockButton.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (blockButton.isChecked()) {
                                if (selectedBlockButton != null) {
                                    selectedBlockButton.setChecked(false);
                                }
                                selectedBlockButton = blockButton;
                                levelEntity.send(new BlockTypeSelected(prefabName));
                            } else {
                                selectedBlockButton = null;
                                levelEntity.send(new BlockTypeSelected(null));
                            }
                        }
                    });

            blockTable.add(blockButton).fillX().expandX().row();
        }

        blocksWindow.add(scrollPane).fill().expand();

        stageProvider.getStage().addActor(blocksWindow);
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entityRef) {
        levelEntity = entityRef;
    }
}
