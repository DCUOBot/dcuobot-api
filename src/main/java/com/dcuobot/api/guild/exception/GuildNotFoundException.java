package com.dcuobot.api.guild.exception;

public class GuildNotFoundException extends RuntimeException {
    public GuildNotFoundException() {
        super("League not found.");
    }
}
