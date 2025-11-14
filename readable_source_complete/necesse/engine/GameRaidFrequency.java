/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum GameRaidFrequency {
    OFTEN(new LocalMessage("ui", "raidsoften"), null),
    OCCASIONALLY(new LocalMessage("ui", "raidsoccasionally"), null),
    RARELY(new LocalMessage("ui", "raidsrarely"), null),
    NEVER(new LocalMessage("ui", "raidsnever"), null);

    public final GameMessage displayName;
    public final GameMessage description;

    private GameRaidFrequency(GameMessage displayName, GameMessage description) {
        this.displayName = displayName;
        this.description = description;
    }
}

