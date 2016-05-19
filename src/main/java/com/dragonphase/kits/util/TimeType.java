package com.dragonphase.kits.util;

import java.util.HashMap;
import java.util.Map;

public enum TimeType {
    YEARS("y"),
    MONTHS("mo"),
    DAYS("d"),
    HOURS("h"),
    MINUTES("m"),
    SECONDS("s"),
    MILLISECONDS("ms");

    private final String value;

    TimeType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    private static final Map<String, TimeType> BY_VALUE = new HashMap<>();

    public static TimeType match(String value) {
        return BY_VALUE.containsKey(value.toLowerCase()) ? BY_VALUE.get(value.toLowerCase()) : TimeType.valueOf((value + (value.toLowerCase().endsWith("s") ? "" : "s")).toUpperCase());
    }

    static {
        for (TimeType type : TimeType.values()) {
            BY_VALUE.put(type.getValue(), type);
        }
    }
}
