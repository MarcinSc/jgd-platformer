package jgd.platformer.gameplay.audio;

import com.badlogic.gdx.audio.Sound;

public interface AudioManager {
    void setMasterVolume(float volume);

    void setMusicVolume(float volume);

    void setFXVolume(float volume);

    void playSound(Sound sound);
}
