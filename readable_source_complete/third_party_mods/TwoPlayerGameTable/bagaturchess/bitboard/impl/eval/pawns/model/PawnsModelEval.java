/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.impl.eval.pawns.model.PawnsModel;

public class PawnsModelEval {
    private static final int[] PASSER_RANK_SCALE = new int[]{0, 2, 2, 4, 7, 10, 12, 0};
    private PawnsModel model = new PawnsModel();

    public void rebuild(IBitBoard bitboard) {
        this.model.rebuild(bitboard);
        this.eval();
    }

    public PawnsModel getModel() {
        return this.model;
    }

    protected void eval() {
    }

    public static int getPasserRankBonus(int rank) {
        return PASSER_RANK_SCALE[rank];
    }
}

