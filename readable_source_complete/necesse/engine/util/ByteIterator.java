/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

public abstract class ByteIterator {
    private int curIndex;
    private int curBitIndex;
    private int curBit;
    private boolean throwIfIndexAboveSize;

    public ByteIterator() {
    }

    public ByteIterator(ByteIterator copy) {
        this.curIndex = copy.curIndex;
        this.curBitIndex = copy.curBitIndex;
        this.curBit = copy.curBit;
    }

    public void throwIfIndexAboveSize() {
        this.throwIfIndexAboveSize = true;
    }

    public abstract int getSizeOfData();

    public boolean hasNext() {
        return this.curIndex < this.getSizeOfData();
    }

    public void resetIndex() {
        this.resetIndex(0);
    }

    public void resetIndex(int startIndex) {
        this.curIndex = startIndex;
        this.curBitIndex = startIndex;
        this.curBit = 0;
    }

    protected void addIndex(int index) {
        this.curIndex += index;
        if (this.curBit == 0) {
            this.curBitIndex = this.curIndex;
        }
        if (this.throwIfIndexAboveSize && this.curIndex > this.getSizeOfData()) {
            throw new IndexOutOfBoundsException();
        }
    }

    protected int getNextIndex() {
        if (this.throwIfIndexAboveSize && this.curIndex > this.getSizeOfData()) {
            throw new IndexOutOfBoundsException();
        }
        return this.curIndex;
    }

    protected int getNextBitIndex() {
        return this.curBitIndex;
    }

    protected int getNextBit() {
        int out = this.curBit++;
        if (this.curBit == 1) {
            this.addIndex(1);
        }
        if (this.curBit > 7) {
            this.curBit = 0;
            this.curBitIndex = this.getNextIndex();
        }
        return out;
    }
}

