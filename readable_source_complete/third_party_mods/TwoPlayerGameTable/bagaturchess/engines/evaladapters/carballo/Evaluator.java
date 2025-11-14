/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.AttacksInfo;
import bagaturchess.engines.evaladapters.carballo.BitboardAttacks;
import bagaturchess.engines.evaladapters.carballo.IBoard;
import bagaturchess.engines.evaladapters.carballo.StringUtils;

public abstract class Evaluator {
    public static final int W = 0;
    public static final int B = 1;
    public static final int NO_VALUE = Short.MAX_VALUE;
    public static final int MATE = 30000;
    public static final int KNOWN_WIN = 20000;
    public static final int DRAW = 0;
    public static final int PAWN_OPENING = 80;
    public static final int PAWN = 100;
    public static final int KNIGHT = 325;
    public static final int BISHOP = 325;
    public static final int ROOK = 500;
    public static final int QUEEN = 975;
    public static final int[] PIECE_VALUES = new int[]{0, 100, 325, 325, 500, 975};
    public static final int[] PIECE_VALUES_OE = new int[]{0, Evaluator.oe(80, 100), Evaluator.oe(325, 325), Evaluator.oe(325, 325), Evaluator.oe(500, 500), Evaluator.oe(975, 975)};
    public static final int BISHOP_PAIR = Evaluator.oe(50, 50);
    public static final int GAME_PHASE_MIDGAME = 1000;
    public static final int GAME_PHASE_ENDGAME = 0;
    public static final int NON_PAWN_MATERIAL_ENDGAME_MIN = 1475;
    public static final int NON_PAWN_MATERIAL_MIDGAME_MAX = 5900;
    public BitboardAttacks bbAttacks = BitboardAttacks.getInstance();

    public abstract int evaluate1(IBoard var1, AttacksInfo var2);

    public abstract int evaluate2(IBoard var1, AttacksInfo var2);

    public static int oe(int opening, int endgame) {
        return (opening << 16) + endgame;
    }

    public static int o(int oe) {
        return oe + 32768 >> 16;
    }

    public static int e(int oe) {
        return (short)(oe & 0xFFFF);
    }

    public static int oeShr(int factor, int oeValue) {
        return Evaluator.oe(Evaluator.o(oeValue) >> factor, Evaluator.e(oeValue) >> factor);
    }

    String formatOE(int value) {
        return StringUtils.padLeft(String.valueOf(Evaluator.o(value)), 8) + " " + StringUtils.padLeft(String.valueOf(Evaluator.e(value)), 8);
    }
}

