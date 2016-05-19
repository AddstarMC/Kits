package com.dragonphase.kits.util;

import java.util.HashMap;
import java.util.Map;

public enum FlagType {
    OVERWRITE,
    ANNOUNCE,
    DELAY,
    CLEAR;

    private static final Map<String, FlagType> BY_NAME = new HashMap<>();

    public static FlagType match(String name) {
        return BY_NAME.get(name.toLowerCase());
    }

    static {
        for (FlagType type : FlagType.values()) {
            BY_NAME.put(type.name().toLowerCase(), type);
        }
    }
}
