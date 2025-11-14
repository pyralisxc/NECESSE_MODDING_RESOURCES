/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.state;

public interface FiguresStateListener {
    public void added(int var1);

    public void killed(int var1);

    public void revived(int var1);

    public void produced(int var1);

    public void released(int var1);

    public void moveForward(long[] var1);

    public void moveBackward(long[] var1);

    public long getWhiteMaterial();

    public long getBlackMaterial();

    public void clearSameFigureMoves();

    public int getBlackSameFigureMoves();

    public int getWhiteSameFigureMoves();

    public int getMovesCount(int var1);

    public int getWhiteFactor();

    public int getBlackFactor();
}

