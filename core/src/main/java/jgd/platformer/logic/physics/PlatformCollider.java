package jgd.platformer.logic.physics;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.level.LevelComponent;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@RegisterSystem
public class PlatformCollider {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    private List<Rectangle2D> platformBlocks = new LinkedList<>();

    @ReceiveEvent
    public void levelLoaded(AfterComponentAdded event, EntityRef entity, LevelComponent level) {
        Map<String, String> blockCoordinates = level.getBlockCoordinates();
        for (Map.Entry<String, String> blockCoordinate : blockCoordinates.entrySet()) {
            String location = blockCoordinate.getKey();
            String prefab = blockCoordinate.getValue();
            EntityRef prefabData = entityManager.wrapEntityData(prefabManager.getPrefabByName(prefab));
            CollidingObjectComponent collidingObject = prefabData.getComponent(CollidingObjectComponent.class);
            if (collidingObject != null) {
                String[] locationSplit = location.split(",");
                float x = Float.parseFloat(locationSplit[0]);
                float y = Float.parseFloat(locationSplit[1]);
                platformBlocks.add(new Rectangle2D.Float(
                        x + collidingObject.getTranslateX(), y + collidingObject.getTranslateY(),
                        collidingObject.getWidth(), collidingObject.getHeight()));
            }
        }
    }

    @ReceiveEvent
    public void levelUnloaded(BeforeComponentRemoved event, EntityRef entity, LevelComponent level) {
        platformBlocks.clear();
    }

    @ReceiveEvent
    public void getCollisionPoint(GetCollisionPoint event, EntityRef entity) {
        for (Rectangle2D platformBlock : platformBlocks) {
            if (platformBlock.intersects(event.getObjectBounds())) {
                switch (event.getDirection()) {
                    case LEFT:
                        event.registerCollision((float) platformBlock.getMaxX());
                        break;
                    case RIGHT:
                        event.registerCollision((float) platformBlock.getMinX());
                        break;
                    case DOWN:
                        event.registerCollision((float) platformBlock.getMaxY());
                        break;
                    case UP:
                        event.registerCollision((float) platformBlock.getMinY());
                        break;
                }
            }
        }
    }
}