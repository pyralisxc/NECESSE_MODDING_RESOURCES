/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public enum GameDeathPenalty {
    NONE(new LocalMessage("ui", "dpnone"), null),
    DROP_MATS(new LocalMessage("ui", "dpdropmats"), new LocalMessage("ui", "dpdropmatstip")),
    DROP_MAIN_INVENTORY(new LocalMessage("ui", "dpdropmain"), new LocalMessage("ui", "dpdropmaintip")),
    DROP_FULL_INVENTORY(new LocalMessage("ui", "dpdropfull"), new LocalMessage("ui", "dpdropfulltip")),
    HARDCORE(new LocalMessage("ui", "dphardcore"), new LocalMessage("ui", "dphardcoretip"));

    public final GameMessage displayName;
    public final GameMessage description;

    private GameDeathPenalty(GameMessage displayName, GameMessage description) {
        this.displayName = displayName;
        this.description = description;
    }
}

