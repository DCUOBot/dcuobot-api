package com.dcuobot.api.common.worldid;

public class InvalidWorldIdException extends RuntimeException {
    public InvalidWorldIdException() {
        super("Invalid world id.");
    }
}
