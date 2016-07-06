package jgd.platformer.logic.physics;

import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import com.gempukku.secsy.entity.event.AfterComponentAdded;
import com.gempukku.secsy.entity.event.BeforeComponentRemoved;
import jgd.platformer.level.LevelComponent;

import java.awt.geom.Rectangle2D;
import java.util.LinkedList;
import java.util.List;

@RegisterSystem
public class PlatformCollider {
    private List<Rectangle2D> platformBlocks = new LinkedList<>();

    @ReceiveEvent
    public void levelLoaded(AfterComponentAdded event, EntityRef entity, LevelComponent level) {
        for (String blockCoordinates : level.getBlockCoordinates().keySet()) {
            String[] split = blockCoordinates.split(",");
            float x = Float.parseFloat(split[0]);
            float y = Float.parseFloat(split[1]);

            platformBlocks.add(new Rectangle2D.Float(x, y, 1, 1));
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
