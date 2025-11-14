/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model.pst;

import bagaturchess.bitboard.common.Utils;

public class PSTGen {
    public static final int[] W_TRAPPED_MINOR = Utils.reverseSpecial(new int[]{-50, -40, -35, -35, -35, -35, -40, -50, -40, -35, -30, -30, -30, -30, -35, -40, -35, -30, -25, -25, -25, -25, -30, -35, -30, -25, -20, -20, -20, -20, -25, -30, -25, -20, -15, -10, -10, -15, -20, -25, -25, -20, -15, -10, -10, -15, -20, -25, -25, -20, -15, -15, -15, -15, -20, -25, -30, -25, -20, -20, -20, -20, -25, -30});
    public static final int[] B_TRAPPED_MINOR = Utils.reverseSpecial(new int[]{-30, -25, -20, -20, -20, -20, -25, -30, -25, -20, -15, -15, -15, -15, -20, -25, -25, -20, -15, -10, -10, -15, -20, -25, -25, -20, -15, -10, -10, -15, -20, -25, -30, -25, -20, -20, -20, -20, -25, -30, -35, -30, -25, -25, -25, -25, -30, -35, -40, -35, -30, -30, -30, -30, -35, -40, -50, -40, -35, -35, -35, -35, -40, -50});
    public static int PENALTY_ON_ATTACKED_SQUARE = -20;
    public static int BONUS_ATTACK = 2;
    public static int BONUS_ATTACK_SAFE = 4;
    public static int BONUS_ATTACK_UNDEFENDED_PAWN = 8;
}

