package jgd.platformer.menu.rendering;

import com.gempukku.secsy.entity.event.Event;

public class MusicVolumeSet extends Event {
    private float volume;

    public MusicVolumeSet(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }
}
