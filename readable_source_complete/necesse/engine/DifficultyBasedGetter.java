/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import necesse.engine.GameDifficulty;
import necesse.engine.ProtectedDifficultyBasedGetter;
import necesse.engine.WorldSettingsGetter;

public class DifficultyBasedGetter<T>
extends ProtectedDifficultyBasedGetter<T> {
    @Override
    public DifficultyBasedGetter<T> set(GameDifficulty difficulty, T object) {
        super.set(difficulty, object);
        return this;
    }

    @Override
    public T get(GameDifficulty difficulty) {
        return super.get(difficulty);
    }

    @Override
    public T get(WorldSettingsGetter getter) {
        return super.get(getter);
    }
}

