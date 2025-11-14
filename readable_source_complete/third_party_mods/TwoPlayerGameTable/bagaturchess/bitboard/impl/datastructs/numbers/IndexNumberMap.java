/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.datastructs.numbers;

public class IndexNumberMap {
    private int[][] mData;
    private int size;
    private long[] mValues;

    public IndexNumberMap(int aMaxNumber) {
        this.mData = new int[2][aMaxNumber];
        this.mValues = new long[aMaxNumber];
        this.size = 0;
    }

    private int getIndex(int aNumber) {
        int index = this.mData[0][aNumber];
        return this.mData[1][index] == aNumber && this.size > index ? index : -1;
    }

    public boolean contains(int aNumber) {
        return this.getIndex(aNumber) != -1;
    }

    public long getValue(int aNumber) {
        int index = this.mData[0][aNumber];
        if (this.mData[1][index] == aNumber && this.size > index) {
            return this.mValues[aNumber];
        }
        throw new IllegalStateException();
    }

    public void add(int aNumber, long value) {
        if (this.contains(aNumber)) {
            throw new IllegalStateException("Number " + aNumber + " already exists!");
        }
        if (value == 0L) {
            throw new IllegalStateException();
        }
        this.mData[0][aNumber] = this.size;
        this.mData[1][this.size] = aNumber;
        this.mValues[aNumber] = value;
        ++this.size;
    }

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

    public void clear() {
        this.size = 0;
    }

    public int getDataSize() {
        return this.size;
    }

    public int[] getData() {
        return this.mData[1];
    }
}

