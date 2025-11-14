/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs;

public class LongToInt {
    public static int DUMMY_VALUE = -1;
    private static int DEFAULT_SIZE = 1024;
    int size = DEFAULT_SIZE;
    boolean[] used = new boolean[this.size];
    long[] keys = new long[this.size];
    int[] values = new int[this.size];

    public void put(long key, int value) {
        if (value == DUMMY_VALUE) {
            throw new IllegalStateException("Value " + value + " is special value used by the structure");
        }
        int hash = LongToInt.hash(key);
        if (this.used[hash]) {
            long oldKey = this.keys[hash];
            if (oldKey != key) {
                for (int i = hash; i < this.size; ++i) {
                    if (!this.used[i]) {
                        this.used[i] = true;
                        this.keys[i] = key;
                        this.values[i] = value;
                        break;
                    }
                    if (i != this.size - 1) continue;
                    throw new IllegalStateException("Hash " + hash + " used from key " + oldKey);
                }
            }
            this.values[hash] = value;
        } else {
            this.used[hash] = true;
            this.keys[hash] = key;
            this.values[hash] = value;
        }
    }

    public void remove(long key) {
        int hash = LongToInt.hash(key);
        if (this.keys[hash] == key) {
            this.used[hash] = false;
        } else {
            for (int i = hash; i < this.size; ++i) {
                if (this.used[i] && this.keys[i] == key) {
                    this.used[i] = false;
                    break;
                }
                if (this.used[i] && i != this.size - 1) continue;
                throw new IllegalStateException("Key " + key + " not found");
            }
        }
    }

    public int get(long key) {
        int hash = LongToInt.hash(key);
        if (this.used[hash]) {
            if (this.keys[hash] == key) {
                return this.values[hash];
            }
            for (int i = hash; i < this.size; ++i) {
                if (this.used[i] && this.keys[i] == key) {
                    return this.values[i];
                }
                if (this.used[i] && i != this.size - 1) continue;
                return DUMMY_VALUE;
            }
            throw new IllegalStateException();
        }
        return DUMMY_VALUE;
    }

    private static final int hash(long key) {
        int mod = (int)(key % (long)(DEFAULT_SIZE - 1));
        if (mod < 0) {
            mod = -mod;
        }
        return mod;
    }
}

