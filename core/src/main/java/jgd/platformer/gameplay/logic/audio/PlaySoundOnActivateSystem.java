package jgd.platformer.gameplay.logic.audio;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.gempukku.secsy.context.annotation.Inject;
import com.gempukku.secsy.context.annotation.RegisterSystem;
import com.gempukku.secsy.entity.EntityRef;
import com.gempukku.secsy.entity.dispatch.ReceiveEvent;
import jgd.platformer.gameplay.audio.AudioManager;
import jgd.platformer.gameplay.logic.activate.ActivateEntity;

import java.util.HashMap;
import java.util.Map;

@RegisterSystem(
        profiles = "gameplay"
)
public class PlaySoundOnActivateSystem {
    @Inject
    private AudioManager audioManager;

    private Map<String, Sound> sounds = new HashMap<>();

    @ReceiveEvent
    public void activateActivate(ActivateEntity activateEntity, EntityRef entityRef, PlaySoundOnActivateComponent playSoundOnActivate) {
        Sound sound = getSound(playSoundOnActivate.getSound());
        audioManager.playSound(sound);
    }

    private Sound getSound(String sound) {
        Sound result = sounds.get(sound);
        if (result == null) {
            result = Gdx.audio.newSound(Gdx.files.internal(sound));
            sounds.put(sound, result);
        }
        return result;
    }
}
