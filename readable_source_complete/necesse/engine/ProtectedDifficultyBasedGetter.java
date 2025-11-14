/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.GameDifficulty;
import necesse.engine.WorldSettingsGetter;
import necesse.engine.world.WorldSettings;

public class ProtectedDifficultyBasedGetter<T> {
    protected Object[] array = new Object[GameDifficulty.values().length];
    protected boolean[] set = new boolean[GameDifficulty.values().length];

    protected ProtectedDifficultyBasedGetter<T> set(GameDifficulty difficulty, T object) {
        int difficultyIndex = difficulty.ordinal();
        this.array[difficultyIndex] = object;
        this.set[difficultyIndex] = true;
        int midIndex = GameDifficulty.CLASSIC.ordinal();
        for (int i = 0; i < this.array.length; ++i) {
            if (this.set[i]) continue;
            if (this.array[i] == null) {
                this.array[i] = object;
                continue;
            }
            if (difficultyIndex < midIndex && i < difficultyIndex) {
                this.array[i] = object;
                continue;
            }
            if (difficultyIndex <= midIndex || i <= difficultyIndex) continue;
            this.array[i] = object;
        }
        return this;
    }

    protected T get(GameDifficulty difficulty) {
        return (T)this.array[difficulty.ordinal()];
    }

    protected T get(WorldSettingsGetter getter) {
        if (getter == null) {
            return this.get(GameDifficulty.CLASSIC);
        }
        WorldSettings worldSettings = getter.getWorldSettings();
        if (worldSettings != null) {
            return this.get(worldSettings.difficulty);
        }
        return this.get(GameDifficulty.CLASSIC);
    }
}

