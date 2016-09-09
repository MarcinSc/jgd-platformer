package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.Component;

import java.util.List;
import java.util.Map;

public interface LevelComponent extends Component {
    Map<String, String> getBlockCoordinates();
    void setBlockCoordinates(Map<String, String> blockCoordinates);

    Map<String, Object> getObjectCoordinates();

    void setObjectCoordinates(Map<String, Object> objectCoordinates);

    List<Object> getAdditionalObjects();
}
