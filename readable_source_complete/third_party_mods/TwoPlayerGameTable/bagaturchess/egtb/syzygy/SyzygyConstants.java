/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.egtb.syzygy;

public class SyzygyConstants {
    public static final int TB_LOSS = 0;
    public static final int TB_BLESSED_LOSS = 1;
    public static final int TB_DRAW = 2;
    public static final int TB_CURSED_WIN = 3;
    public static final int TB_WIN = 4;
    public static final int TB_PROMOTES_NONE = 0;
    public static final int TB_PROMOTES_QUEEN = 1;
    public static final int TB_PROMOTES_ROOK = 2;
    public static final int TB_PROMOTES_BISHOP = 3;
    public static final int TB_PROMOTES_KNIGHT = 4;
    public static final int TB_RESULT_WDL_MASK = 15;
    public static final int TB_RESULT_TO_MASK = 1008;
    public static final int TB_RESULT_FROM_MASK = 64512;
    public static final int TB_RESULT_PROMOTES_MASK = 458752;
    public static final int TB_RESULT_DTZ_MASK = -1048576;
    public static final int TB_RESULT_WDL_SHIFT = 0;
    public static final int TB_RESULT_TO_SHIFT = 4;
    public static final int TB_RESULT_FROM_SHIFT = 10;
    public static final int TB_RESULT_PROMOTES_SHIFT = 16;
    public static final int TB_RESULT_DTZ_SHIFT = 20;

    public static int fromSquare(int result) {
        return (result & 0xFC00) >> 10;
    }

    public static int toSquare(int result) {
        return (result & 0x3F0) >> 4;
    }

    public static int promoteInto(int result) {
        return (result & 0x70000) >> 16;
    }

    public static int distanceToZero(int result) {
        return (result & 0xFFF00000) >> 20;
    }

    public static int winDrawLoss(int result) {
        return (result & 0xF) >> 0;
    }
}

