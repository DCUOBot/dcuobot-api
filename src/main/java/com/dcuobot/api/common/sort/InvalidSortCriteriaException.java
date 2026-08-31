package com.dcuobot.api.common.sort;

public class InvalidSortCriteriaException extends RuntimeException {
    public InvalidSortCriteriaException() {
        super("Invalid sort criteria.");
    }
}
