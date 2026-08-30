package com.dcuobot.api.census.exception;

public class MissingDataException extends RuntimeException {
    public MissingDataException() {
        super("We are missing some data for this character/league, please report this to an administrator.");
    }
}
