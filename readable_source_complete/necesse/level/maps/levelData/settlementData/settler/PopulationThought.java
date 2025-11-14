/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.mobs.friendly.human.HappinessModifier;

public class PopulationThought {
    public final GameMessage displayName;
    public final int population;
    public final int happinessIncrease;

    public PopulationThought(GameMessage displayName, int population, int happinessIncrease) {
        this.displayName = displayName;
        this.population = population;
        this.happinessIncrease = happinessIncrease;
    }

    public HappinessModifier getModifier() {
        return new HappinessModifier(this.happinessIncrease, this.displayName);
    }
}

