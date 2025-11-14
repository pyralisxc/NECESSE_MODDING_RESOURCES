/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.state;

import bagaturchess.bitboard.api.IBoard;

public class PiecesList {
    private int[] data;
    private int size;
    private IBoard board;

    public PiecesList(IBoard _board, int max) {
        this.board = _board;
        this.data = new int[max];
        this.size = 0;
    }

    public boolean contains(int aNumber) {
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] != aNumber) continue;
            return true;
        }
        return false;
    }

    public void set(int from, int to) {
        boolean ok = false;
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] != from) continue;
            this.data[i] = to;
            ok = true;
        }
        if (!ok) {
            // empty if block
        }
    }

    public void add(int aNumber) {
        if (this.size == this.data.length) {
            throw new IllegalStateException(String.valueOf(this.board) + " ADDING " + aNumber);
        }
        this.data[this.size++] = aNumber;
    }

    public int remove(int aNumber) {
        boolean found = false;
        for (int i = 0; i < this.size; ++i) {
            if (this.data[i] != aNumber) continue;
            this.data[i] = this.data[this.size - 1];
            --this.size;
            found = true;
            break;
        }
        if (!found) {
            // empty if block
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
}

