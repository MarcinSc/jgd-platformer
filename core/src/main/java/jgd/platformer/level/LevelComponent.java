package jgd.platformer.level;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface LevelComponent extends Component {
    Map<String, String> getBlockCoordinates();
}
