package jgd.platformer.editor;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.editor.controls.MouseTracked;
import jgd.platformer.editor.controls.PlaceObject;
import jgd.platformer.editor.ui.BlockTypeSelected;
import jgd.platformer.gameplay.level.LevelComponent;
import jgd.platformer.gameplay.rendering.platform.RebuildBlockMesh;

import java.util.Map;

@RegisterSystem(
        profiles = {"gameScreen", "editor"}
)
public class EditorLogicSystem {
    private Vector3 lastMousePosition = new Vector3();
    private String lastSelectedBlockType;

    @ReceiveEvent
    public void mouseTracked(MouseTracked mouseTracked, EntityRef entityRef) {
        Vector3 position = mouseTracked.getPosition();
        boolean snap = mouseTracked.isSnap();
        if (snap)
            lastMousePosition.set(MathUtils.round(position.x - 0.5f), MathUtils.round(position.y - 0.5f), MathUtils.round(position.z));
        else
            lastMousePosition.set(position.x - 0.5f, position.y - 0.5f, position.z);
    }

    @ReceiveEvent
    public void blockTypeSelected(BlockTypeSelected blockTypeSelected, EntityRef entityRef) {
        lastSelectedBlockType = blockTypeSelected.getPrefabName();
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
    }
}
