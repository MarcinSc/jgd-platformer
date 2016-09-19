package jgd.platformer.editor;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.event.RenderEnvironment;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.editor.controls.MouseTracked;
import jgd.platformer.editor.controls.PlaceObject;
import jgd.platformer.editor.ui.BlockTypeSelected;
import jgd.platformer.editor.ui.ObjectTypeSelected;
import jgd.platformer.gameplay.level.LevelComponent;
import jgd.platformer.gameplay.logic.spawning.PlatformerEntitySpawner;
import jgd.platformer.gameplay.rendering.platform.RebuildBlockMesh;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class EditorLogicSystem implements LifeCycleSystem {
    @Inject
    private PlatformerEntitySpawner platformerEntitySpawner;

    private Vector3 lastMousePosition = new Vector3();
    private float snapSize = 0.25f;

    private String lastSelectedBlockType;
    private ObjectTypeSelected lastSelectedObjectType;

    private Model model;
    private ModelInstance modelInstance;

    private ModelBatch modelBatch;

    @Override
    public void initialize() {
        float size = 1;

        float[] vertices = {
                0, 0, 0,
                0, 0, size,
                0, size, 0,
                0, size, size,
                size, 0, 0,
                size, 0, size,
                size, size, 0,
                size, size, size};

        short[] indices = {
                0, 1,
                0, 2,
                1, 3,
                2, 3,

                4, 5,
                4, 6,
                5, 7,
                6, 7,

                0, 4,
                1, 5,
                2, 6,
                3, 7};

        Mesh mesh = new Mesh(true, vertices.length / 3, indices.length, VertexAttribute.Position());
        mesh.setVertices(vertices);
        mesh.setIndices(indices);

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        modelBuilder.part("cube", mesh, GL20.GL_LINES, new Material(ColorAttribute.createDiffuse(1, 1, 1, 1)));
        model = modelBuilder.end();
        modelInstance = new ModelInstance(model);
        modelBatch = new ModelBatch();
    }

    @ReceiveEvent
    public void renderCube(RenderEnvironment event, EntityRef entityRef) {
        if (lastMousePosition != null) {
            if (lastSelectedBlockType != null) {
                modelInstance.transform.idt().translate(lastMousePosition);
            } else if (lastSelectedObjectType != null) {
                modelInstance.transform.idt().translate(lastMousePosition)
                        .translate(lastSelectedObjectType.getRenderTranslate()).scl(lastSelectedObjectType.getRenderSize());
            }

            event.getRenderPipeline().getCurrentBuffer().begin();
            modelBatch.begin(event.getCamera());
            modelBatch.render(modelInstance);
            modelBatch.end();
            event.getRenderPipeline().getCurrentBuffer().end();
        }
    }

    @ReceiveEvent
    public void mouseTracked(MouseTracked mouseTracked, EntityRef entityRef) {
        Vector3 position = mouseTracked.getPosition();
        boolean snap = mouseTracked.isSnap();
        if (snap)
            lastMousePosition.set(MathUtils.round((position.x - 0.5f) / snapSize) * snapSize,
                    MathUtils.round((position.y - 0.5f) / snapSize) * snapSize,
                    MathUtils.round(position.z / snapSize) * snapSize);
        else
            lastMousePosition.set(position.x - 0.5f, position.y - 0.5f, position.z);
    }

    @ReceiveEvent
    public void blockTypeSelected(BlockTypeSelected blockTypeSelected, EntityRef entityRef) {
        lastSelectedBlockType = blockTypeSelected.getPrefabName();
    }

    @ReceiveEvent
    public void objectTypeSelected(ObjectTypeSelected objectTypeSelected, EntityRef entityRef) {
        if (objectTypeSelected.getPrefabName() != null) {
            lastSelectedObjectType = objectTypeSelected;
        } else {
            lastSelectedObjectType = null;
        }
    }

    @ReceiveEvent
    public void placeObject(PlaceObject placeObject, EntityRef entityRef, LevelComponent levelComponent) {
        if (lastSelectedBlockType != null) {
            Map<String, String> blockCoordinates = levelComponent.getBlockCoordinates();
            String position = lastMousePosition.x + "," + lastMousePosition.y + "," + lastMousePosition.z;
            blockCoordinates.put(position, lastSelectedBlockType);

            levelComponent.setBlockCoordinates(blockCoordinates);
            entityRef.saveChanges();

            entityRef.send(new RebuildBlockMesh());
        }
        if (lastSelectedObjectType != null) {
            List<Object> objectCoordinates = levelComponent.getLocatedObjects();
            Vector3 placementTranslate = lastSelectedObjectType.getPlacementTranslate();
            float x = lastMousePosition.x + placementTranslate.x;
            float y = lastMousePosition.y + placementTranslate.y;
            float z = lastMousePosition.z + placementTranslate.z;
            String position = x + "," + y + "," + z;
            String prefabName = lastSelectedObjectType.getPrefabName();
            objectCoordinates.add(position + "|" + prefabName);

            levelComponent.setLocatedObjects(objectCoordinates);
            entityRef.saveChanges();

            platformerEntitySpawner.createEntityAt(x, y, z, prefabName, Collections.emptyMap());
        }
    }
}
