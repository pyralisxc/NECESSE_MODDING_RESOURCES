/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.numbers;

import bagaturchess.bitboard.impl.datastructs.numbers.NumberSet;

public class IndexNumberSet_Arr
implements NumberSet {
    private int[] data;
    private int size;

    public IndexNumberSet_Arr(int max) {
        this.data = new int[max];
        this.size = 0;
    }

    @Override
    public int getIndex(int aNumber) {
        throw new IllegalStateException();
    }

    @Override
    public boolean contains(int aNumber) {
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] != aNumber) continue;
            return true;
        }
        return false;
    }

    @Override
    public void add(int aNumber) {
        this.data[this.size++] = aNumber;
    }

    @Override
    public int remove(int aNumber) {
        boolean found = false;
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] != aNumber) continue;
            this.data[i] = this.data[this.size - 1];
            --this.size;
            found = true;
            break;
        }
        return 0;
    }

    public int getFirst() {
        throw new IllegalStateException();
    }

    public int getLast() {
        throw new IllegalStateException();
    }

    @Override
    public void clear() {
        this.size = 0;
    }

    @Override
    public int getDataSize() {
        return this.size;
    }

    public int[] getData() {
        return this.data;
    }

    public String toString() {
        Object result = "[";
        for (int i = 0; i < this.size; ++i) {
            result = (String)result + this.data[i] + " ";
        }
        result = (String)result + "]";
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof IndexNumberSet_Arr) {
            IndexNumberSet_Arr numberSet = (IndexNumberSet_Arr)obj;
            result = this.containsAll(numberSet) && numberSet.containsAll(this);
        }
        return result;
    }

    public boolean containsAll(IndexNumberSet_Arr numberSet) {
        boolean result = true;
        int[] data = numberSet.getData();
        int count = numberSet.getDataSize();
        for (int i = 0; i < count; ++i) {
            int number = data[i];
            if (this.contains(number)) continue;
            result = false;
            break;
        }
        return result;
    }

    @Override
    public IndexNumberSet_Arr clone() {
        IndexNumberSet_Arr clone = new IndexNumberSet_Arr(this.data.length);
        for (int i = 0; i < this.size; ++i) {
            clone.add(this.data[i]);
        }
        return clone;
    }

    public void finishIteration() {
    }

    public boolean inIteration() {
        return true;
    }
}

