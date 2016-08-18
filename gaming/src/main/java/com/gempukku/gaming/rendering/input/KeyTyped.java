package com.gempukku.gaming.rendering.input;

import com.gempukku.secsy.entity.event.Event;

public class KeyTyped extends Event {
    private char character;

    public KeyTyped(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }
}
