/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class DietThought {
    public final GameMessage displayName;
    public final int variety;
    public final int happinessIncrease;

    public DietThought(GameMessage displayName, int variety, int happinessIncrease) {
        this.displayName = displayName;
        this.variety = variety;
        this.happinessIncrease = happinessIncrease;
    }

    public HappinessModifier getModifier() {
        return new HappinessModifier(this.happinessIncrease, this.displayName);
    }
}

