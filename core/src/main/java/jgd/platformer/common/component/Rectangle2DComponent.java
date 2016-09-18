package jgd.platformer.common.component;

import com.badlogic.gdx.math.Vector2;
import com.gempukku.secsy.entity.Component;

public interface Rectangle2DComponent extends Component {
    default Vector2 getTranslate() {
        return new Vector2();
    }

    default Vector2 getSize() {
        return new Vector2(1, 1);
    }
}
