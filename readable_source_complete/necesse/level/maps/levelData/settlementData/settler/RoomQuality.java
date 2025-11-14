/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class RoomQuality {
    public final GameMessage displayName;
    public final int minScore;
    public final int happinessIncrease;

    public RoomQuality(GameMessage displayName, int minScore, int happinessIncrease) {
        this.displayName = displayName;
        this.minScore = minScore;
        this.happinessIncrease = happinessIncrease;
    }

    public HappinessModifier getModifier() {
        LocalMessage description = new LocalMessage("settlement", "roommood").addReplacement("quality", this.displayName);
        return new HappinessModifier(this.happinessIncrease, description);
    }
}

