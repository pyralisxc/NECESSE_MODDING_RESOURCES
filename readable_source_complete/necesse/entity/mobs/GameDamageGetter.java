/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs;

import necesse.engine.DifficultyBasedGetter;
import necesse.engine.GameDifficulty;
import necesse.entity.mobs.GameDamage;

public class GameDamageGetter
extends DifficultyBasedGetter<GameDamage> {
    public GameDamageGetter() {
    }

    public GameDamageGetter(int casualDamage, int adventureDamage, int normalDamage, int hardDamage, int brutalDamage) {
        this.set(GameDifficulty.CASUAL, new GameDamage(casualDamage));
        this.set(GameDifficulty.ADVENTURE, new GameDamage(adventureDamage));
        this.set(GameDifficulty.CLASSIC, new GameDamage(normalDamage));
        this.set(GameDifficulty.HARD, new GameDamage(hardDamage));
        this.set(GameDifficulty.BRUTAL, new GameDamage(brutalDamage));
    }

    public GameDamageGetter set(GameDifficulty difficulty, GameDamage object) {
        super.set(difficulty, object);
        return this;
    }
}

