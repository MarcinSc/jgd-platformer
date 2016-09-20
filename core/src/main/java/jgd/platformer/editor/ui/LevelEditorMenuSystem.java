package jgd.platformer.editor.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Selection;
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
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.AfterComponentUpdated;
import com.gempukku.secsy.entity.io.EntityData;
import jgd.platformer.editor.RequestTestingLevel;
import jgd.platformer.gameplay.level.AfterLevelLoaded;
import jgd.platformer.gameplay.level.LevelComponent;
import jgd.platformer.gameplay.rendering.platform.RebuildBlockMesh;

import java.util.HashMap;
import java.util.Map;

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

    private TextButton selectedButton;

    private EntityRef levelEntity;

    private Tree.Node blocksNode;
    private Tree.Node entitiesNode;

    private Map<EntityRef, Tree.Node> entitiesInLevel = new HashMap<>();
    private Skin uiSkin;

    private TextField minXField;
    private TextField maxXField;
    private TextField minYField;
    private Tree levelTree;

    @Override
    public void initialize() {
        uiSkin = new Skin(Gdx.files.internal("uiskin.json"));

        Window blocksWindow = createBlocksWindow(uiSkin);
        stageProvider.getStage().addActor(blocksWindow);

        Window objectsWindow = createObjectsWindow(uiSkin);
        stageProvider.getStage().addActor(objectsWindow);

        Window selectWindow = createSelectWindow(uiSkin);
        stageProvider.getStage().addActor(selectWindow);
    }

    @ReceiveEvent
    public void entityAdded(AfterComponentAdded event, EntityRef entityRef, ObjectInEditorComponent objectInEditor) {
        Actor nodeActor = createEntityRepresentation(entityRef, objectInEditor);
        Tree.Node entityNode = new Tree.Node(nodeActor);
        entityNode.setObject(entityRef);
        entitiesInLevel.put(entityRef, entityNode);
        entitiesNode.add(entityNode);
    }

    @ReceiveEvent
    public void levelModified(AfterComponentUpdated event, EntityRef entityRef, LevelComponent level) {
        blocksNode.removeAll();
        Map<String, Tree.Node> blockTypes = new HashMap<>();

        for (Map.Entry<String, String> block : level.getBlockCoordinates().entrySet()) {
            String location = block.getKey();
            String blockPrefabName = block.getValue();

            EntityData prefabByName = prefabManager.getPrefabByName(blockPrefabName);
            EntityRef prefabEntity = entityManager.wrapEntityData(prefabByName);
            String displayName = prefabEntity.getComponent(BlockInEditorComponent.class).getDisplayName();

            Tree.Node blockTypeNode = blockTypes.get(displayName);
            if (blockTypeNode == null) {
                Label blockTypeActor = new Label(displayName, uiSkin);
                blockTypeNode = new Tree.Node(blockTypeActor);
                blocksNode.add(blockTypeNode);
                blockTypes.put(displayName, blockTypeNode);
            }

            Actor blockActor = createBlockRepresentation(location);
            Tree.Node blockNode = new Tree.Node(blockActor);
            blockNode.setObject(location);

            blockTypeNode.add(blockNode);
        }
    }

    private Actor createEntityRepresentation(EntityRef entityRef, ObjectInEditorComponent objectInEditor) {
        Table table = new Table();

        String displayName = objectInEditor.getDisplayName();
        table.add(new Label(displayName, uiSkin));
        TextButton deleteButton = new TextButton("x", uiSkin);
        deleteButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        Tree.Node node = entitiesInLevel.get(entityRef);
                        if (levelTree.getSelection().getLastSelected() == node) {
                            levelEntity.send(new SelectionCleared());
                        }
                        entityManager.destroyEntity(entityRef);
                        node.getParent().remove(node);
                    }
                });
        table.add(deleteButton).align(Align.right);

        return table;
    }

    private Actor createBlockRepresentation(String location) {
        Table table = new Table();
        table.add(new Label(location, uiSkin));
        TextButton deleteButton = new TextButton("x", uiSkin);
        deleteButton.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        LevelComponent level = levelEntity.getComponent(LevelComponent.class);
                        Map<String, String> blockCoordinates = level.getBlockCoordinates();
                        blockCoordinates.remove(location);
                        level.setBlockCoordinates(blockCoordinates);
                        levelEntity.saveChanges();
                        levelEntity.send(new RebuildBlockMesh());
                    }
                });
        table.add(deleteButton).align(Align.right);
        return table;
    }

    private Window createSelectWindow(Skin uiSkin) {
        Window objectsWindow = new Window("Edit level", uiSkin);
        objectsWindow.setResizable(true);
        objectsWindow.setMovable(true);

        TextButton testLevel = new TextButton("Test level", uiSkin);
        testLevel.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        EntityData levelEntityData = entityManager.exposeEntityData(levelEntity);
                        levelEntity.send(new RequestTestingLevel(levelEntityData));
                    }
                });
        objectsWindow.add(testLevel).colspan(2);
        objectsWindow.row();

        Label minXLabel = new Label("Min X", uiSkin);
        Label maxXLabel = new Label("Max X", uiSkin);
        Label minYLabel = new Label("Min Y", uiSkin);

        minXField = new TextField("0", uiSkin);
        maxXField = new TextField("0", uiSkin);
        minYField = new TextField("0", uiSkin);
        TextField.TextFieldFilter integerOnly = new TextField.TextFieldFilter() {
            @Override
            public boolean acceptChar(TextField textField, char c) {
                return (c >= '0' && c <= '9') || c == '-';
            }
        };
        minXField.setTextFieldFilter(integerOnly);
        maxXField.setTextFieldFilter(integerOnly);
        minYField.setTextFieldFilter(integerOnly);

        objectsWindow.add(minXLabel, minXField);
        objectsWindow.row();
        objectsWindow.add(maxXLabel, maxXField);
        objectsWindow.row();
        objectsWindow.add(minYLabel, minYField);
        objectsWindow.row();

        levelTree = new Tree(uiSkin);
        levelTree.getSelection().setMultiple(false);
        levelTree.addListener(
                new ChangeListener() {
                    @Override
                    public void changed(ChangeEvent event, Actor actor) {
                        if (actor == levelTree) {
                            Selection<Tree.Node> selection = levelTree.getSelection();
                            Tree.Node lastSelected = selection.getLastSelected();
                            if (lastSelected != null) {
                                Object selectedObject = lastSelected.getObject();
                                if (selectedObject instanceof EntityRef) {
                                    ((EntityRef) selectedObject).send(new EntitySelected());
                                } else if (selectedObject instanceof String) {
                                    levelEntity.send(new BlockSelected((String) selectedObject));
                                }
                            } else {
                                levelEntity.send(new SelectionCleared());
                            }
                        }
                    }
                });
        Label blocksLabel = new Label("Blocks", uiSkin);
        Label entitiesLabel = new Label("Entities", uiSkin);

        blocksNode = new Tree.Node(blocksLabel);
        entitiesNode = new Tree.Node(entitiesLabel);

        levelTree.add(blocksNode);
        levelTree.add(entitiesNode);

        ScrollPane selectObject = new ScrollPane(levelTree, uiSkin);
        selectObject.setFadeScrollBars(false);

        objectsWindow.add(selectObject).colspan(2).fill().expand();
        return objectsWindow;
    }

    private Window createObjectsWindow(Skin uiSkin) {
        Window objectsWindow = new Window("Objects", uiSkin);
        objectsWindow.setResizable(true);
        objectsWindow.setMovable(true);

        Table objectTable = new Table(uiSkin);
        objectTable.top();
        objectTable.defaults().height(20).align(Align.left);

        ScrollPane scrollPane = new ScrollPane(objectTable, uiSkin);
        scrollPane.setFadeScrollBars(false);

        for (NamedEntityData namedEntityData : prefabManager.findNamedPrefabsWithComponents(ObjectInEditorComponent.class)) {
            String prefabName = namedEntityData.getName();

            ObjectInEditorComponent objectInEditor = entityManager.wrapEntityData(namedEntityData).getComponent(ObjectInEditorComponent.class);
            String displayName = objectInEditor.getDisplayName();
            Vector3 renderSize = objectInEditor.getRenderSize();
            Vector3 renderTranslate = objectInEditor.getRenderTranslate();
            Vector3 placementTranslate = objectInEditor.getPlacementTranslate();

            TextButton objectButton = new TextButton(displayName, uiSkin, "toggle");
            objectButton.addListener(
                    new ChangeListener() {
                        @Override
                        public void changed(ChangeEvent event, Actor actor) {
                            if (objectButton.isChecked()) {
                                if (selectedButton != null) {
                                    selectedButton.setChecked(false);
                                }
                                selectedButton = objectButton;
                                levelEntity.send(new ObjectTypeSelected(prefabName, renderSize, renderTranslate, placementTranslate));
                            } else {
                                selectedButton = null;
                                levelEntity.send(new ObjectTypeSelected(null, null, null, null));
                            }
                        }
                    });

            objectTable.add(objectButton).fillX().expandX().row();
        }

        objectsWindow.add(scrollPane).fill().expand();
        return objectsWindow;
    }

    private Window createBlocksWindow(Skin uiSkin) {
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
                                if (selectedButton != null) {
                                    selectedButton.setChecked(false);
                                }
                                selectedButton = blockButton;
                                levelEntity.send(new BlockTypeSelected(prefabName));
                            } else {
                                selectedButton = null;
                                levelEntity.send(new BlockTypeSelected(null));
                            }
                        }
                    });

            blockTable.add(blockButton).fillX().expandX().row();
        }

        blocksWindow.add(scrollPane).fill().expand();
        return blocksWindow;
    }

    @ReceiveEvent
    public void levelLoaded(AfterLevelLoaded event, EntityRef entityRef) {
        levelEntity = entityRef;
    }
}
