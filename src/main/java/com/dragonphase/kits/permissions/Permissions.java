package com.dragonphase.kits.permissions;

import com.dragonphase.kits.util.Message;
import com.dragonphase.kits.util.Message.MessageType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class Permissions {

    public static final String KITS_BASE = "kits";

    public static final String KITS_LIST = KITS_BASE + ".list";

    public static final String KITS_SPAWN = KITS_BASE + ".spawn";
    public static final String KITS_SPAWN_OTHERS = KITS_SPAWN + ".others";

    public static final String KITS_SIGN = KITS_BASE + ".sign";

    public static final String KITS_FLAGS = KITS_BASE + ".flags";

    public static final String KITS_NODELAY = KITS_BASE + ".nodelay";

    public static final String KITS_ADMIN = KITS_BASE + ".admin";

    public static boolean checkPermission(Player player, String permission) {
        if (!player.hasPermission(permission.toLowerCase())) {
            Message.showMessage(player, Message.show("",
                  TextComponent.of("You do not have permission to perform that action."), MessageType.WARNING),
                  TextComponent.of("Required Permission node: ").color(NamedTextColor.DARK_AQUA)
                        .append(TextComponent.of(permission).color(NamedTextColor.GRAY)));
            return false;
        }
        return true;
    }

    public static boolean checkPermission(Player player, String permission, String... subPerms) {
        return checkPermission(player, permission + "." + StringUtils.join(subPerms, "."));
    }

    public static boolean hasPermission(Player player, String permission, String... subPerms) {
        return player.hasPermission((permission + "." + StringUtils.join(subPerms, ".")).toLowerCase());
    }
}
