/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.endgame;

import bagaturchess.bitboard.api.IMaterialState;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class MaterialState
implements MoveListener,
IMaterialState {
    private int piecesCount;
    private int[] pidsCounts;

    public MaterialState() {
        this.init();
    }

    private void init() {
        this.piecesCount = 0;
        this.pidsCounts = new int[13];
    }

    @Override
    public int getPiecesCount() {
        return this.piecesCount;
    }

    @Override
    public int[] getPIDsCounts() {
        return this.pidsCounts;
    }

    @Override
    public void addPiece_Special(int pid, int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initially_addPiece(int pid, int fieldID, long bb_pieces) {
        this.added(pid);
    }

    @Override
    public void postBackwardMove(int color, int move) {
        if (MoveInt.isCapture(move)) {
            int cap_pid = MoveInt.getCapturedFigurePID(move);
            this.added(cap_pid);
        }
        if (MoveInt.isPromotion(move)) {
            int prom_pid = MoveInt.getPromotionFigurePID(move);
            this.removed(prom_pid);
            int pid = MoveInt.getFigurePID(move);
            this.added(pid);
        }
    }

    @Override
    public void postForwardMove(int color, int move) {
        if (MoveInt.isCapture(move)) {
            int cap_pid = MoveInt.getCapturedFigurePID(move);
            this.removed(cap_pid);
        }
        if (MoveInt.isPromotion(move)) {
            int prom_pid = MoveInt.getPromotionFigurePID(move);
            this.added(prom_pid);
            int pid = MoveInt.getFigurePID(move);
            this.removed(pid);
        }
    }

    @Override
    public void preBackwardMove(int color, int move) {
    }

    @Override
    public void preForwardMove(int color, int move) {
    }

    protected void added(int figurePID) {
        this.inc(figurePID);
    }

    protected void removed(int figurePID) {
        this.dec(figurePID);
    }

    private void inc(int pid) {
        ++this.piecesCount;
        int n = pid;
        this.pidsCounts[n] = this.pidsCounts[n] + 1;
    }

    protected void dec(int pid) {
        --this.piecesCount;
        int n = pid;
        this.pidsCounts[n] = this.pidsCounts[n] - 1;
    }
}

