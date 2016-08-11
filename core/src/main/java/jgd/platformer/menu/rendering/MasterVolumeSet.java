package jgd.platformer.menu.rendering;

import com.gempukku.secsy.entity.event.Event;

public class MasterVolumeSet extends Event {
    private float volume;

    public MasterVolumeSet(float volume) {
        this.volume = volume;
    }

    public float getVolume() {
        return volume;
    }
}
