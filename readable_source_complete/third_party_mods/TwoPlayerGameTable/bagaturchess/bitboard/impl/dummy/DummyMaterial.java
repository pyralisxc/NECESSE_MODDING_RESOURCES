/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.dummy;

import bagaturchess.bitboard.impl.state.FiguresStateListener;

public class DummyMaterial
implements FiguresStateListener {
    @Override
    public void added(int figureID) {
    }

    @Override
    public void clearSameFigureMoves() {
    }

    @Override
    public int getBlackFactor() {
        return 0;
    }

    @Override
    public long getBlackMaterial() {
        return (long)(100.0 * Math.random());
    }

    @Override
    public int getBlackSameFigureMoves() {
        return 0;
    }

    public int getMaxFactor() {
        return 0;
    }

    @Override
    public int getWhiteFactor() {
        return 0;
    }

    @Override
    public long getWhiteMaterial() {
        return (long)(100.0 * Math.random());
    }

    @Override
    public int getWhiteSameFigureMoves() {
        return 0;
    }

    public boolean inEndGame() {
        return false;
    }

    public boolean inMiddleEndGame() {
        return false;
    }

    public boolean inMiddleGame() {
        return false;
    }

    @Override
    public void killed(int figureID) {
    }

    @Override
    public void moveBackward(long[] move) {
    }

    @Override
    public void moveForward(long[] move) {
    }

    @Override
    public void produced(int figureID) {
    }

    @Override
    public void released(int figureID) {
    }

    @Override
    public void revived(int figureID) {
    }

    @Override
    public int getMovesCount(int figureID) {
        return 0;
    }
}

