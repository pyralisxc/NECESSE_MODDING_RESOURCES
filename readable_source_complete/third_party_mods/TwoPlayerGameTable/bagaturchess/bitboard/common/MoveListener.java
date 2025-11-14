/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.common;

public interface MoveListener {
    public void addPiece_Special(int var1, int var2);

    public void initially_addPiece(int var1, int var2, long var3);

    public void preForwardMove(int var1, int var2);

    public void postForwardMove(int var1, int var2);

    public void preBackwardMove(int var1, int var2);

    public void postBackwardMove(int var1, int var2);
}

