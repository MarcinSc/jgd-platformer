package jgd.platformer.gameplay.logic.signal;

import com.gempukku.secsy.entity.event.Event;

public class SignalDeactivated extends Event {
    private String channel;

    public SignalDeactivated(String channel) {
        this.channel = channel;
    }

    public String getChannel() {
        return channel;
    }
}
