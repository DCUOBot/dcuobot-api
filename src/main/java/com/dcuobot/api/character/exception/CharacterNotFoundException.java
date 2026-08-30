package com.dcuobot.api.character.exception;

public class CharacterNotFoundException extends RuntimeException {
    public CharacterNotFoundException() {
        super("Character not found.");
    }
}
