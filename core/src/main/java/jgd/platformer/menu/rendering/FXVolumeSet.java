package jgd.platformer.menu.rendering;

import com.gempukku.secsy.entity.event.Event;

public class FXVolumeSet extends Event {
    private float volume;

    public FXVolumeSet(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }
}
