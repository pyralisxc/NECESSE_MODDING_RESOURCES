/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.movelist;

import bagaturchess.bitboard.impl.movelist.IMoveList;

public class BaseMoveList
implements IMoveList {
    private int[] moves;
    private int count;
    private int cur = 0;

    public BaseMoveList(int max) {
        this.moves = new int[max];
    }

    public BaseMoveList() {
        this(100);
    }

    @Override
    public void reserved_clear() {
        this.count = 0;
    }

    @Override
    public final void reserved_add(int move) {
        this.moves[this.count++] = move;
    }

    @Override
    public final void reserved_removeLast() {
        --this.count;
    }

    @Override
    public final int reserved_getCurrentSize() {
        return this.count;
    }

    @Override
    public final int[] reserved_getMovesBuffer() {
        return this.moves;
    }

    @Override
    public void clear() {
        this.reserved_clear();
        this.cur = 0;
    }

    @Override
    public int next() {
        if (this.cur < this.count) {
            return this.moves[this.cur++];
        }
        return 0;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public int getScore() {
        throw new UnsupportedOperationException();
    }
}

