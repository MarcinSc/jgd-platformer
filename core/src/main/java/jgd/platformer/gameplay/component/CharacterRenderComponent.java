package jgd.platformer.gameplay.component;

import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface CharacterRenderComponent extends Component {
    String getId();

    String getShape();

    Map<String, String> getTexturesForParts();

    float getTranslateX();

    float getTranslateY();

    float getTranslateZ();
}
