/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IInternalMoveList {
    public void reserved_clear();

    public void reserved_add(int var1);

    public void reserved_removeLast();

    public int reserved_getCurrentSize();

    public int[] reserved_getMovesBuffer();
}

