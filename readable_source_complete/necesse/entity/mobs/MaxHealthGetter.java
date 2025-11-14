/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;

public class MaxHealthGetter
extends DifficultyBasedGetter<Integer> {
    public MaxHealthGetter() {
    }

    public MaxHealthGetter(int normalMaxHealth) {
        this.set(GameDifficulty.CLASSIC, normalMaxHealth);
    }

    public MaxHealthGetter(int casualMaxHealth, int adventureMaxHealth, int normalMaxHealth, int hardMaxHealth, int brutalMaxHealth) {
        this.set(GameDifficulty.CASUAL, casualMaxHealth);
        this.set(GameDifficulty.ADVENTURE, adventureMaxHealth);
        this.set(GameDifficulty.CLASSIC, normalMaxHealth);
        this.set(GameDifficulty.HARD, hardMaxHealth);
        this.set(GameDifficulty.BRUTAL, brutalMaxHealth);
    }

    public MaxHealthGetter set(GameDifficulty difficulty, Integer maxHealth) {
        super.set(difficulty, maxHealth);
        return this;
    }
}

