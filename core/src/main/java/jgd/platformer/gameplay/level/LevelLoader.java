package jgd.platformer.gameplay.level;

import com.gempukku.secsy.entity.io.EntityData;

public interface LevelLoader {
    void loadLevel(String levelPrefabName);

    void loadLevel(EntityData entityData);

    void unloadLevel();

    void createNewLevel();
}
