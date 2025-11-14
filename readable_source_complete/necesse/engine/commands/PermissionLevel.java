/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum PermissionLevel {
    USER(new LocalMessage("misc", "permuser")),
    CREATIVESETTINGS(new LocalMessage("misc", "permcreativesettings")),
    MODERATOR(new LocalMessage("misc", "permmod")),
    ADMIN(new LocalMessage("misc", "permadmin")),
    OWNER(new LocalMessage("misc", "permowner")),
    SERVER(new LocalMessage("misc", "permserver"), true);

    public final GameMessage name;
    public final boolean reserved;

    private PermissionLevel(GameMessage name, boolean reserved) {
        this.name = name;
        this.reserved = reserved;
    }

    private PermissionLevel(GameMessage name) {
        this(name, false);
    }

    public int getLevel() {
        return this.ordinal();
    }

    public static PermissionLevel getLevel(int level) {
        PermissionLevel[] levels = PermissionLevel.values();
        PermissionLevel out = levels[0];
        for (int i = 1; i < levels.length; ++i) {
            if (levels[i].reserved || level < levels[i].getLevel()) continue;
            out = levels[i];
        }
        return out;
    }
}

