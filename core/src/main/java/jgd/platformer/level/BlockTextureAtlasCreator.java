package jgd.platformer.level;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.asset.texture.TextureAtlasRegistry;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.io.EntityData;

import java.util.HashSet;
import java.util.Set;

@RegisterSystem(profiles = "gameplay")
public class BlockTextureAtlasCreator implements LifeCycleSystem {
    @Inject
    private TextureAtlasRegistry textureAtlasRegistry;
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;

    @Override
    public void initialize() {
        Set<String> textureNames = new HashSet<>();

        for (EntityData entityData : prefabManager.findPrefabsWithComponents(BlockComponent.class)) {
            EntityRef entityRef = entityManager.wrapEntityData(entityData);
            for (String textureName : entityRef.getComponent(BlockComponent.class).getTexturesForParts().values()) {
                textureNames.add(textureName);
            }
        }

        textureAtlasRegistry.registerTextures("platforms", textureNames);
    }
}
