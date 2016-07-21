package jgd.platformer.gameplay.component;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface ModelShapeComponent extends Component {
    String getShape();

    Map<String, String> getTexturesForParts();
}
