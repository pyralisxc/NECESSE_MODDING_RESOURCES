/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;

public class IntRange {
    public int min;
    public int max;

    public IntRange(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public boolean isValueInRange(int value) {
        return value >= this.min && value <= this.max;
    }

    public int getRandomValueInRange(GameRandom random) {
        if (this.min == this.max) {
            return this.min;
        }
        return random.getIntBetween(this.min, this.max);
    }

    public int limitValueInRange(int value) {
        return GameMath.limit(value, this.min, this.max);
    }
}

