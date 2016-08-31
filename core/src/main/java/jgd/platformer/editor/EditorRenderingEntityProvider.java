package jgd.platformer.editor;

import com.gempukku.gaming.asset.prefab.PrefabManager;
import com.gempukku.gaming.rendering.RenderingEntityProvider;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;
import com.gempukku.secsy.entity.EntityManager;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.index.EntityIndexManager;
import com.gempukku.secsy.entity.io.EntityData;

@RegisterSystem(
        profiles = {"gameScreen", "editor"},
        shared = RenderingEntityProvider.class
)
public class EditorRenderingEntityProvider implements RenderingEntityProvider, LifeCycleSystem {
    @Inject
    private PrefabManager prefabManager;
    @Inject
    private EntityManager entityManager;
    @Inject
    private EntityIndexManager entityIndexManager;

    private EntityRef cameraEntity;

    @Override
    public EntityRef getRenderingEntity() {
        if (cameraEntity == null || !cameraEntity.exists()) {
            EntityData renderingEntity = prefabManager.getPrefabByName("editorRenderingEntity");
            cameraEntity = entityManager.createEntity(renderingEntity);
        }
        return cameraEntity;
    }
}
