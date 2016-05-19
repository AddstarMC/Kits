package com.dragonphase.kits.util;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;

public final class Utils {

    public static final DateFormat timeFormat = new SimpleDateFormat("yyyy:MM:dd:kk:mm:ss:SSSS");
    public static final Date initialDate = new Date(0);

    private Utils() {}

    public static <T> T[] trim(T[] args) {
        return Arrays.copyOfRange(args, 1, args.length);
    }

    @SuppressWarnings({"unchecked", "SuspiciousMethodCalls"})
    public static <T> T[] clean(T[] args, Class<T> clazz) {
        List<T> list = new ArrayList<>(Arrays.asList(args));
        list.removeAll(Arrays.asList(null, "", 0, 0.0, 0.0f, 0L));
        return list.toArray((T[]) Array.newInstance(clazz, list.size()));
    }

    public static String[] clean(String[] args) {
        return clean(args, String.class);
    }

    public static String capitalize(String string) {
        return WordUtils.capitalizeFully(string);
    }

    public static String getLocationAsString(Location location) {
        return "world: " + location.getWorld().getName() + ", x: " + location.getBlockX() + ", y: " + location.getBlockY() + ", z: " + location.getBlockZ();
    }
}
