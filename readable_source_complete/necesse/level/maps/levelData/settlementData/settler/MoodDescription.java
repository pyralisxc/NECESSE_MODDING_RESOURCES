/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;

public class MoodDescription {
    public final GameMessage displayName;
    public final int minHappiness;

    public MoodDescription(GameMessage displayName, int minHappiness) {
        this.displayName = displayName;
        this.minHappiness = minHappiness;
    }

    public GameMessage getDescription() {
        return new LocalMessage("settlement", "moodhappiness").addReplacement("happiness", this.displayName);
    }
}

