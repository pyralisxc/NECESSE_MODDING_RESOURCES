/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

import bagaturchess.bitboard.impl.state.PiecesList;

public interface IPiecesLists {
    public void rem(int var1, int var2);

    public void add(int var1, int var2);

    public void move(int var1, int var2, int var3);

    public PiecesList getPieces(int var1);
}

