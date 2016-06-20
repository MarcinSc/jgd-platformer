package jgd.platformer.rendering;

import com.badlogic.gdx.math.Vector3;
import com.gempukku.gaming.rendering.BackgroundColorProvider;
import com.gempukku.secsy.context.annotation.RegisterSystem;

@RegisterSystem(
        shared = BackgroundColorProvider.class
)
public class PlatformerBackgroundColorProvider implements BackgroundColorProvider {
    private Vector3 backgroundColor = new Vector3(145f / 255f, 186f / 255f, 220f / 255f);

    @Override
    public Vector3 getBackgroundColor() {
        return backgroundColor;
    }
}
