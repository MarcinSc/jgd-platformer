package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.Component;

import java.util.List;
import java.util.Map;

public interface LevelComponent extends Component {
    Map<String, String> getBlockCoordinates();

    Map<String, Object> getObjectCoordinates();

    List<String> getAdditionalObjects();
}
