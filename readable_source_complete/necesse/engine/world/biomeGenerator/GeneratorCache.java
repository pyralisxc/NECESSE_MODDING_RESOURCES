/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.biomeGenerator;

import java.util.Arrays;
import necesse.engine.util.GameMath;

public class GeneratorCache {
    private final long[] keys;
    private final int[] values;
    private final int indexMask;

    public GeneratorCache(int capacity) {
        if (capacity < 1 || !GameMath.isPowerOf2(capacity)) {
            throw new IllegalArgumentException("Capacity must be a power of 2");
        }
        this.keys = new long[capacity];
        Arrays.fill(this.keys, -1L);
        this.values = new int[capacity];
        this.indexMask = (int)GameMath.getBitMask(Integer.numberOfTrailingZeros(capacity));
    }

    public synchronized int get(int x, int y, Getter getter) {
        long key = GameMath.getUniqueLongKey(x, y);
        if (x == -1 && y == -1) {
            int value = getter.get(x, y);
            int index = this.getKeyIndex(key) & this.indexMask;
            this.keys[index] = key;
            this.values[index] = value;
            return value;
        }
        int index = this.getKeyIndex(key) & this.indexMask;
        if (this.keys[index] == key) {
            return this.values[index];
        }
        int value = getter.get(x, y);
        this.keys[index] = key;
        this.values[index] = value;
        return value;
    }

    public synchronized int getKeyIndex(long key) {
        key ^= key >>> 33;
        key *= -49064778989728563L;
        key ^= key >>> 33;
        key *= -4265267296055464877L;
        key ^= key >>> 33;
        return (int)key;
    }

    @FunctionalInterface
    public static interface Getter {
        public int get(int var1, int var2);
    }
}

