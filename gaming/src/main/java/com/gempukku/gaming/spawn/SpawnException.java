package com.gempukku.gaming.spawn;

public class SpawnException extends RuntimeException {
    public SpawnException(String message) {
        super(message);
    }

    public SpawnException(String message, Throwable cause) {
        super(message, cause);
    }
}
