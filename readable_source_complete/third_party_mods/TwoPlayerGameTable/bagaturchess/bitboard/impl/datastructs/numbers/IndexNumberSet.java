/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.numbers;

import bagaturchess.bitboard.impl.datastructs.numbers.NumberSet;

public class IndexNumberSet
implements NumberSet {
    private int[][] mData;
    private int size;
    private boolean inIteration = false;

    public IndexNumberSet(int aMaxNumber) {
        this.mData = new int[2][aMaxNumber];
        this.size = 0;
    }

    @Override
    public int getIndex(int aNumber) {
        int index = this.mData[0][aNumber];
        return this.mData[1][index] == aNumber && this.size > index ? index : -1;
    }

    @Override
    public boolean contains(int aNumber) {
        return this.getIndex(aNumber) != -1;
    }

    @Override
    public void add(int aNumber) {
        if (this.contains(aNumber)) {
            throw new IllegalStateException("Number " + aNumber + " already exists!");
        }
        this.mData[0][aNumber] = this.size;
        this.mData[1][this.size] = aNumber;
        ++this.size;
    }

    @Override
    public int remove(int aNumber) {
        if (this.contains(aNumber)) {
            int lastNumber;
            int index = this.mData[0][aNumber];
            --this.size;
            this.mData[1][index] = lastNumber = this.mData[1][this.size];
            this.mData[0][lastNumber] = index;
        }
        return 0;
    }

    public int getFirst() {
        if (this.size < 1) {
            throw new IllegalStateException("Empty set!");
        }
        return this.mData[1][0];
    }

    public int getLast() {
        if (this.size < 1) {
            throw new IllegalStateException("Empty set!");
        }
        return this.mData[1][this.size - 1];
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
        this.inIteration = true;
        return this.mData[1];
    }

    public String toString() {
        Object result = "[";
        for (int i = 0; i < this.size; ++i) {
            result = (String)result + this.mData[1][i] + " ";
        }
        result = (String)result + "]";
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof IndexNumberSet) {
            IndexNumberSet numberSet = (IndexNumberSet)obj;
            result = this.containsAll(numberSet) && numberSet.containsAll(this);
        }
        return result;
    }

    public boolean containsAll(IndexNumberSet numberSet) {
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
    public IndexNumberSet clone() {
        IndexNumberSet clone = new IndexNumberSet(this.mData[0].length);
        for (int i = 0; i < this.size; ++i) {
            clone.add(this.mData[1][i]);
        }
        return clone;
    }

    public void finishIteration() {
        this.inIteration = false;
    }

    public boolean inIteration() {
        return this.inIteration;
    }
}

