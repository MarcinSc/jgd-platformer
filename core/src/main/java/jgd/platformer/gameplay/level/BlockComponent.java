package jgd.platformer.gameplay.level;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.secsy.entity.Component;

import java.util.Map;

public interface BlockComponent extends Component {
    String getShape();

    Map<String, String> getTexturesForParts();

    default Vector3 getTranslate() {
        return new Vector3();
    }
}
