package jgd.platformer.gameplay.level;

public interface LevelLoader {
    void loadLevel(String levelPrefabName);

    void unloadLevel();

    void createNewLevel();
}
