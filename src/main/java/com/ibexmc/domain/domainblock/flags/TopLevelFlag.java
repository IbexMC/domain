package com.ibexmc.domain.domainblock.flags;

public enum TopLevelFlag {
    ACCESS,
    BAZAAR,
    BREAK,
    BUILD,
    BURN,
    COMMAND,
    EFFECT,
    EPIDEMIC,
    GROW,
    HURT,
    INTERACT,
    MAGNET,
    SPAWN,
    SPAWNER,
    TELEPORT,
    TRADE,
    TRANSPORT,
    WORLD,
    UNEXPECTED_ERROR;

    public static TopLevelFlag getEnumFromString(String text) {
        try {
            return TopLevelFlag.valueOf(text);
        } catch (IllegalArgumentException e) {
            return UNEXPECTED_ERROR;
        }
    }
}
