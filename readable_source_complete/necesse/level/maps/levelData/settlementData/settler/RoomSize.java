/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class RoomSize {
    public final GameMessage displayName;
    public final int minSize;
    public final int happinessIncrease;

    public RoomSize(GameMessage displayName, int minSize, int happinessIncrease) {
        this.displayName = displayName;
        this.minSize = minSize;
        this.happinessIncrease = happinessIncrease;
    }

    public HappinessModifier getModifier() {
        LocalMessage description = new LocalMessage("settlement", "sizemood").addReplacement("size", this.displayName);
        return new HappinessModifier(this.happinessIncrease, description);
    }
}

