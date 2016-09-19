package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.Component;

import java.util.List;
import java.util.Map;

public interface LevelComponent extends Component {
    Map<String, String> getBlockCoordinates();
    void setBlockCoordinates(Map<String, String> blockCoordinates);

    List<Object> getLocatedObjects();

    void setLocatedObjects(List<Object> locatedObjects);

    List<Object> getAdditionalObjects();

    float getMinX();

    void setMinX(float minX);

    float getMaxX();

    void setMaxX(float maxX);

    float getMinY();

    void setMinY(float minY);
}
