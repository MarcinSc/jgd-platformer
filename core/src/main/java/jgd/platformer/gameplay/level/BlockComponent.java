package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface BlockComponent extends Component {
    String getShape();

    Map<String, String> getTexturesForParts();
}
