package jgd.platformer.gameplay.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.context.system.LifeCycleSystem;

@RegisterSystem(
        profiles = {"gameScreen", "gameplay"}, shared = AudioManager.class
)
public class AudioSystem implements AudioManager, LifeCycleSystem {
    private float masterVolume;
    private float musicVolume;
    private float fxVolume;

    private Music backgroundMusic;

    @Override
    public void setMasterVolume(float volume) {
        masterVolume = volume;

        updateBackgroundMusicVolume();
    }

    @Override
    public void setMusicVolume(float volume) {
        musicVolume = volume;

        updateBackgroundMusicVolume();
    }

    @Override
    public void setFXVolume(float volume) {
        fxVolume = volume;
    }

    @Override
    public void initialize() {
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music_jewels.ogg"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0);
        backgroundMusic.play();
    }

    @Override
    public void playSound(Sound sound) {
        sound.play(fxVolume * masterVolume);
    }

    private void updateBackgroundMusicVolume() {
        backgroundMusic.setVolume(masterVolume * musicVolume);
    }
}
