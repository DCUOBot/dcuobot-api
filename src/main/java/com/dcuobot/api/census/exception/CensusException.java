package com.dcuobot.api.census.exception;

public class CensusException extends RuntimeException {
    public CensusException() {
        super("The Daybreak Games Census API did not respond.");
    }
}
