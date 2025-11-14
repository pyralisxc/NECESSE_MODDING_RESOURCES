/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

import bagaturchess.engines.evaladapters.carballo.AttacksInfo;
import bagaturchess.engines.evaladapters.carballo.BitboardUtils;
import bagaturchess.engines.evaladapters.carballo.Endgame;
import bagaturchess.engines.evaladapters.carballo.Evaluator;
import bagaturchess.engines.evaladapters.carballo.IBoard;

public class CompleteEvaluator
extends Evaluator {
    public static final int SCALE_FACTOR_DEFAULT = 1000;
    private static final int[][] MOBILITY = new int[][]{new int[0], new int[0], {CompleteEvaluator.oe(-12, -16), CompleteEvaluator.oe(2, 2), CompleteEvaluator.oe(5, 7), CompleteEvaluator.oe(7, 9), CompleteEvaluator.oe(8, 11), CompleteEvaluator.oe(10, 13), CompleteEvaluator.oe(11, 14), CompleteEvaluator.oe(11, 15), CompleteEvaluator.oe(12, 16)}, {CompleteEvaluator.oe(-16, -16), CompleteEvaluator.oe(-1, -1), CompleteEvaluator.oe(3, 3), CompleteEvaluator.oe(6, 6), CompleteEvaluator.oe(8, 8), CompleteEvaluator.oe(9, 9), CompleteEvaluator.oe(11, 11), CompleteEvaluator.oe(12, 12), CompleteEvaluator.oe(13, 13), CompleteEvaluator.oe(13, 13), CompleteEvaluator.oe(14, 14), CompleteEvaluator.oe(15, 15), CompleteEvaluator.oe(15, 15), CompleteEvaluator.oe(16, 16)}, {CompleteEvaluator.oe(-14, -21), CompleteEvaluator.oe(-1, -2), CompleteEvaluator.oe(3, 4), CompleteEvaluator.oe(5, 7), CompleteEvaluator.oe(7, 10), CompleteEvaluator.oe(8, 12), CompleteEvaluator.oe(9, 13), CompleteEvaluator.oe(10, 15), CompleteEvaluator.oe(11, 16), CompleteEvaluator.oe(11, 17), CompleteEvaluator.oe(12, 18), CompleteEvaluator.oe(13, 19), CompleteEvaluator.oe(13, 20), CompleteEvaluator.oe(14, 20), CompleteEvaluator.oe(14, 21)}, {CompleteEvaluator.oe(-27, -27), CompleteEvaluator.oe(-9, -9), CompleteEvaluator.oe(-2, -2), CompleteEvaluator.oe(2, 2), CompleteEvaluator.oe(5, 5), CompleteEvaluator.oe(8, 8), CompleteEvaluator.oe(10, 10), CompleteEvaluator.oe(12, 12), CompleteEvaluator.oe(13, 13), CompleteEvaluator.oe(14, 14), CompleteEvaluator.oe(16, 16), CompleteEvaluator.oe(17, 17), CompleteEvaluator.oe(18, 18), CompleteEvaluator.oe(19, 19), CompleteEvaluator.oe(19, 19), CompleteEvaluator.oe(20, 20), CompleteEvaluator.oe(21, 21), CompleteEvaluator.oe(22, 22), CompleteEvaluator.oe(22, 22), CompleteEvaluator.oe(23, 23), CompleteEvaluator.oe(24, 24), CompleteEvaluator.oe(24, 24), CompleteEvaluator.oe(25, 25), CompleteEvaluator.oe(25, 25), CompleteEvaluator.oe(26, 26), CompleteEvaluator.oe(26, 26), CompleteEvaluator.oe(27, 27), CompleteEvaluator.oe(27, 27)}};
    private static final long WHITE_SPACE_ZONE = 0x3C3C3C00L;
    private static final long BLACK_SPACE_ZONE = 0x3C3C3C00000000L;
    private static final int SPACE = CompleteEvaluator.oe(2, 0);
    private static final int[] PAWN_ATTACKS = new int[]{0, 0, CompleteEvaluator.oe(11, 15), CompleteEvaluator.oe(12, 16), CompleteEvaluator.oe(17, 23), CompleteEvaluator.oe(19, 25), 0};
    private static final int[] MINOR_ATTACKS = new int[]{0, CompleteEvaluator.oe(3, 5), CompleteEvaluator.oe(7, 9), CompleteEvaluator.oe(7, 9), CompleteEvaluator.oe(10, 14), CompleteEvaluator.oe(11, 15), 0};
    private static final int[] MAJOR_ATTACKS = new int[]{0, CompleteEvaluator.oe(2, 2), CompleteEvaluator.oe(3, 4), CompleteEvaluator.oe(3, 4), CompleteEvaluator.oe(5, 6), CompleteEvaluator.oe(5, 7), 0};
    private static final int HUNG_PIECES = CompleteEvaluator.oe(16, 25);
    private static final int PINNED_PIECE = CompleteEvaluator.oe(7, 15);
    private static final int[] PAWN_BACKWARDS = new int[]{CompleteEvaluator.oe(20, 15), CompleteEvaluator.oe(10, 15)};
    private static final int[] PAWN_ISOLATED = new int[]{CompleteEvaluator.oe(20, 20), CompleteEvaluator.oe(10, 20)};
    private static final int[] PAWN_DOUBLED = new int[]{CompleteEvaluator.oe(8, 16), CompleteEvaluator.oe(10, 20)};
    private static final int PAWN_UNSUPPORTED = CompleteEvaluator.oe(2, 4);
    private static final int[] PAWN_CANDIDATE = new int[]{0, CompleteEvaluator.oe(10, 13), CompleteEvaluator.oe(10, 13), CompleteEvaluator.oe(14, 18), CompleteEvaluator.oe(22, 28), CompleteEvaluator.oe(34, 43), CompleteEvaluator.oe(50, 63), 0};
    private static final int[] PAWN_PASSER = new int[]{0, CompleteEvaluator.oe(20, 25), CompleteEvaluator.oe(20, 25), CompleteEvaluator.oe(28, 35), CompleteEvaluator.oe(44, 55), CompleteEvaluator.oe(68, 85), CompleteEvaluator.oe(100, 125), 0};
    private static final int[] PAWN_PASSER_OUTSIDE = new int[]{0, 0, 0, CompleteEvaluator.oe(2, 3), CompleteEvaluator.oe(7, 9), CompleteEvaluator.oe(14, 18), CompleteEvaluator.oe(24, 30), 0};
    private static final int[] PAWN_PASSER_CONNECTED = new int[]{0, 0, 0, CompleteEvaluator.oe(3, 3), CompleteEvaluator.oe(8, 8), CompleteEvaluator.oe(15, 15), CompleteEvaluator.oe(25, 25), 0};
    private static final int[] PAWN_PASSER_SUPPORTED = new int[]{0, 0, 0, CompleteEvaluator.oe(6, 6), CompleteEvaluator.oe(17, 17), CompleteEvaluator.oe(33, 33), CompleteEvaluator.oe(55, 55), 0};
    private static final int[] PAWN_PASSER_MOBILE = new int[]{0, 0, 0, CompleteEvaluator.oe(2, 2), CompleteEvaluator.oe(6, 6), CompleteEvaluator.oe(12, 12), CompleteEvaluator.oe(20, 20), 0};
    private static final int[] PAWN_PASSER_RUNNER = new int[]{0, 0, 0, CompleteEvaluator.oe(6, 6), CompleteEvaluator.oe(18, 18), CompleteEvaluator.oe(36, 36), CompleteEvaluator.oe(60, 60), 0};
    private static final int[] PAWN_PASSER_OTHER_KING_DISTANCE = new int[]{0, 0, 0, CompleteEvaluator.oe(0, 2), CompleteEvaluator.oe(0, 6), CompleteEvaluator.oe(0, 12), CompleteEvaluator.oe(0, 20), 0};
    private static final int[] PAWN_PASSER_MY_KING_DISTANCE = new int[]{0, 0, 0, CompleteEvaluator.oe(0, 1), CompleteEvaluator.oe(0, 3), CompleteEvaluator.oe(0, 6), CompleteEvaluator.oe(0, 10), 0};
    private static final int[] PAWN_SHIELD_CENTER = new int[]{0, CompleteEvaluator.oe(55, 0), CompleteEvaluator.oe(41, 0), CompleteEvaluator.oe(28, 0), CompleteEvaluator.oe(14, 0), 0, 0, 0};
    private static final int[] PAWN_SHIELD = new int[]{0, CompleteEvaluator.oe(35, 0), CompleteEvaluator.oe(26, 0), CompleteEvaluator.oe(18, 0), CompleteEvaluator.oe(9, 0), 0, 0, 0};
    private static final int[] PAWN_STORM_CENTER = new int[]{0, 0, 0, CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(15, 0), CompleteEvaluator.oe(30, 0), 0, 0};
    private static final int[] PAWN_STORM = new int[]{0, 0, 0, CompleteEvaluator.oe(5, 0), CompleteEvaluator.oe(10, 0), CompleteEvaluator.oe(20, 0), 0, 0};
    private static final int PAWN_BLOCKADE = CompleteEvaluator.oe(5, 0);
    private static final int[] KNIGHT_OUTPOST = new int[]{CompleteEvaluator.oe(15, 10), CompleteEvaluator.oe(22, 15)};
    private static final int[] BISHOP_OUTPOST = new int[]{CompleteEvaluator.oe(7, 4), CompleteEvaluator.oe(10, 7)};
    private static final int BISHOP_MY_PAWNS_IN_COLOR_PENALTY = CompleteEvaluator.oe(2, 4);
    private static final int[] BISHOP_TRAPPED_PENALTY = new int[]{CompleteEvaluator.oe(40, 40), CompleteEvaluator.oe(80, 80)};
    private static final long[] BISHOP_TRAPPING = new long[]{0L, 1024L, 0L, 0L, 0L, 0L, 8192L, 0L, 131072L, 0L, 0L, 0L, 0L, 0L, 0L, 0x400000L, 0x2000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x40000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x200000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4000000000L, 0x20000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x400000000000L, 0L, 0x4000000000000L, 0L, 0L, 0L, 0L, 0x20000000000000L, 0L};
    private static final long[] BISHOP_TRAPPING_GUARD = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 1024L, 0L, 0L, 0L, 0L, 0L, 0L, 8192L, 262144L, 0L, 0L, 0L, 0L, 0L, 0L, 0x200000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x40000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x200000000000L, 0x4000000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x20000000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
    private static final int[] ROOK_OUTPOST = new int[]{CompleteEvaluator.oe(2, 1), CompleteEvaluator.oe(3, 2)};
    private static final int[] ROOK_FILE = new int[]{CompleteEvaluator.oe(15, 10), CompleteEvaluator.oe(7, 5)};
    private static final int ROOK_7 = CompleteEvaluator.oe(7, 10);
    private static final int[] ROOK_TRAPPED_PENALTY = new int[]{CompleteEvaluator.oe(40, 0), CompleteEvaluator.oe(30, 0), CompleteEvaluator.oe(20, 0), CompleteEvaluator.oe(10, 0)};
    private static final long[] ROOK_TRAPPING = new long[]{0L, 257L, 771L, 0L, 0L, 49344L, 32896L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x101000000000000L, 0x303000000000000L, 0L, 0L, -4557642822898941952L, -9187343239835811840L, 0L};
    private static final int[] PIECE_ATTACKS_KING = new int[]{0, 0, CompleteEvaluator.oe(30, 0), CompleteEvaluator.oe(20, 0), CompleteEvaluator.oe(40, 0), CompleteEvaluator.oe(80, 0)};
    private static final int[] KING_SAFETY_PONDER = new int[]{0, 0, 32, 48, 56, 60, 62, 63, 64, 64, 64, 64, 64, 64, 64, 64};
    public static final int TEMPO = CompleteEvaluator.oe(15, 5);
    private static final long[] OUTPOST_MASK = new long[]{0x7E7E7E000000L, 0x7E7E7E0000L};
    private static final int[] pawnPcsq = new int[]{CompleteEvaluator.oe(-18, 4), CompleteEvaluator.oe(-6, 2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(6, -2), CompleteEvaluator.oe(6, -2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-6, 2), CompleteEvaluator.oe(-18, 4), CompleteEvaluator.oe(-21, 1), CompleteEvaluator.oe(-9, -1), CompleteEvaluator.oe(-3, -3), CompleteEvaluator.oe(3, -5), CompleteEvaluator.oe(3, -5), CompleteEvaluator.oe(-3, -3), CompleteEvaluator.oe(-9, -1), CompleteEvaluator.oe(-21, 1), CompleteEvaluator.oe(-20, 1), CompleteEvaluator.oe(-8, -1), CompleteEvaluator.oe(-2, -3), CompleteEvaluator.oe(4, -5), CompleteEvaluator.oe(4, -5), CompleteEvaluator.oe(-2, -3), CompleteEvaluator.oe(-8, -1), CompleteEvaluator.oe(-20, 1), CompleteEvaluator.oe(-19, 2), CompleteEvaluator.oe(-7, 0), CompleteEvaluator.oe(-1, -2), CompleteEvaluator.oe(12, -4), CompleteEvaluator.oe(12, -4), CompleteEvaluator.oe(-1, -2), CompleteEvaluator.oe(-7, 0), CompleteEvaluator.oe(-19, 2), CompleteEvaluator.oe(-17, 3), CompleteEvaluator.oe(-5, 1), CompleteEvaluator.oe(1, -1), CompleteEvaluator.oe(10, -3), CompleteEvaluator.oe(10, -3), CompleteEvaluator.oe(1, -1), CompleteEvaluator.oe(-5, 1), CompleteEvaluator.oe(-17, 3), CompleteEvaluator.oe(-16, 4), CompleteEvaluator.oe(-4, 2), CompleteEvaluator.oe(2, 0), CompleteEvaluator.oe(8, -2), CompleteEvaluator.oe(8, -2), CompleteEvaluator.oe(2, 0), CompleteEvaluator.oe(-4, 2), CompleteEvaluator.oe(-16, 4), CompleteEvaluator.oe(-15, 6), CompleteEvaluator.oe(-3, 4), CompleteEvaluator.oe(3, 2), CompleteEvaluator.oe(9, 0), CompleteEvaluator.oe(9, 0), CompleteEvaluator.oe(3, 2), CompleteEvaluator.oe(-3, 4), CompleteEvaluator.oe(-15, 6), CompleteEvaluator.oe(-18, 4), CompleteEvaluator.oe(-6, 2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(6, -2), CompleteEvaluator.oe(6, -2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-6, 2), CompleteEvaluator.oe(-18, 4)};
    private static final int[] knightPcsq = new int[]{CompleteEvaluator.oe(-27, -22), CompleteEvaluator.oe(-17, -17), CompleteEvaluator.oe(-9, -12), CompleteEvaluator.oe(-4, -9), CompleteEvaluator.oe(-4, -9), CompleteEvaluator.oe(-9, -12), CompleteEvaluator.oe(-17, -17), CompleteEvaluator.oe(-27, -22), CompleteEvaluator.oe(-21, -15), CompleteEvaluator.oe(-11, -8), CompleteEvaluator.oe(-3, -4), CompleteEvaluator.oe(2, -2), CompleteEvaluator.oe(2, -2), CompleteEvaluator.oe(-3, -4), CompleteEvaluator.oe(-11, -8), CompleteEvaluator.oe(-21, -15), CompleteEvaluator.oe(-15, -10), CompleteEvaluator.oe(-5, -4), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(-5, -4), CompleteEvaluator.oe(-15, -10), CompleteEvaluator.oe(-9, -6), CompleteEvaluator.oe(1, -1), CompleteEvaluator.oe(9, 4), CompleteEvaluator.oe(14, 8), CompleteEvaluator.oe(14, 8), CompleteEvaluator.oe(9, 4), CompleteEvaluator.oe(1, -1), CompleteEvaluator.oe(-9, -6), CompleteEvaluator.oe(-5, -4), CompleteEvaluator.oe(5, 1), CompleteEvaluator.oe(13, 6), CompleteEvaluator.oe(18, 10), CompleteEvaluator.oe(18, 10), CompleteEvaluator.oe(13, 6), CompleteEvaluator.oe(5, 1), CompleteEvaluator.oe(-5, -4), CompleteEvaluator.oe(-6, -4), CompleteEvaluator.oe(4, 2), CompleteEvaluator.oe(12, 7), CompleteEvaluator.oe(17, 9), CompleteEvaluator.oe(17, 9), CompleteEvaluator.oe(12, 7), CompleteEvaluator.oe(4, 2), CompleteEvaluator.oe(-6, -4), CompleteEvaluator.oe(-10, -8), CompleteEvaluator.oe(0, -1), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(13, 5), CompleteEvaluator.oe(13, 5), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(0, -1), CompleteEvaluator.oe(-10, -8), CompleteEvaluator.oe(-20, -15), CompleteEvaluator.oe(-10, -10), CompleteEvaluator.oe(-2, -5), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(-2, -5), CompleteEvaluator.oe(-10, -10), CompleteEvaluator.oe(-20, -15)};
    private static final int[] bishopPcsq = new int[]{CompleteEvaluator.oe(-7, 0), CompleteEvaluator.oe(-8, -1), CompleteEvaluator.oe(-11, -2), CompleteEvaluator.oe(-13, -2), CompleteEvaluator.oe(-13, -2), CompleteEvaluator.oe(-11, -2), CompleteEvaluator.oe(-8, -1), CompleteEvaluator.oe(-7, 0), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(7, 3), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(7, 3), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(15, 5), CompleteEvaluator.oe(15, 5), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(15, 5), CompleteEvaluator.oe(15, 5), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(7, 3), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(7, 3), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(3, 1), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(-2, 0), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-8, -2), CompleteEvaluator.oe(-6, -2), CompleteEvaluator.oe(-3, -1), CompleteEvaluator.oe(-2, 0)};
    private static final int[] rookPcsq = new int[]{CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(8, 0), CompleteEvaluator.oe(4, 0), CompleteEvaluator.oe(0, 0), CompleteEvaluator.oe(-4, 0), CompleteEvaluator.oe(-4, 1), CompleteEvaluator.oe(0, 1), CompleteEvaluator.oe(4, 1), CompleteEvaluator.oe(8, 1), CompleteEvaluator.oe(8, 1), CompleteEvaluator.oe(4, 1), CompleteEvaluator.oe(0, 1), CompleteEvaluator.oe(-4, 1), CompleteEvaluator.oe(-4, 3), CompleteEvaluator.oe(0, 3), CompleteEvaluator.oe(4, 3), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(8, 3), CompleteEvaluator.oe(4, 3), CompleteEvaluator.oe(0, 3), CompleteEvaluator.oe(-4, 3), CompleteEvaluator.oe(-4, 5), CompleteEvaluator.oe(0, 5), CompleteEvaluator.oe(4, 5), CompleteEvaluator.oe(8, 5), CompleteEvaluator.oe(8, 5), CompleteEvaluator.oe(4, 5), CompleteEvaluator.oe(0, 5), CompleteEvaluator.oe(-4, 5), CompleteEvaluator.oe(-4, -2), CompleteEvaluator.oe(0, -2), CompleteEvaluator.oe(4, -2), CompleteEvaluator.oe(8, -2), CompleteEvaluator.oe(8, -2), CompleteEvaluator.oe(4, -2), CompleteEvaluator.oe(0, -2), CompleteEvaluator.oe(-4, -2)};
    private static final int[] queenPcsq = new int[]{CompleteEvaluator.oe(-9, -15), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-9, -15), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-1, -5), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(-1, -5), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(5, 0), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(5, 0), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(9, 5), CompleteEvaluator.oe(9, 5), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(9, 5), CompleteEvaluator.oe(9, 5), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(5, 0), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(6, 2), CompleteEvaluator.oe(5, 0), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-1, -5), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(3, -2), CompleteEvaluator.oe(1, -3), CompleteEvaluator.oe(-1, -5), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-9, -15), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-2, -7), CompleteEvaluator.oe(-4, -8), CompleteEvaluator.oe(-6, -10), CompleteEvaluator.oe(-9, -15)};
    private static final int[] kingPcsq = new int[]{CompleteEvaluator.oe(34, -58), CompleteEvaluator.oe(39, -35), CompleteEvaluator.oe(14, -19), CompleteEvaluator.oe(-6, -13), CompleteEvaluator.oe(-6, -13), CompleteEvaluator.oe(14, -19), CompleteEvaluator.oe(39, -35), CompleteEvaluator.oe(34, -58), CompleteEvaluator.oe(31, -35), CompleteEvaluator.oe(36, -10), CompleteEvaluator.oe(11, 2), CompleteEvaluator.oe(-9, 8), CompleteEvaluator.oe(-9, 8), CompleteEvaluator.oe(11, 2), CompleteEvaluator.oe(36, -10), CompleteEvaluator.oe(31, -35), CompleteEvaluator.oe(28, -19), CompleteEvaluator.oe(33, 2), CompleteEvaluator.oe(8, 17), CompleteEvaluator.oe(-12, 23), CompleteEvaluator.oe(-12, 23), CompleteEvaluator.oe(8, 17), CompleteEvaluator.oe(33, 2), CompleteEvaluator.oe(28, -19), CompleteEvaluator.oe(25, -13), CompleteEvaluator.oe(30, 8), CompleteEvaluator.oe(5, 23), CompleteEvaluator.oe(-15, 32), CompleteEvaluator.oe(-15, 32), CompleteEvaluator.oe(5, 23), CompleteEvaluator.oe(30, 8), CompleteEvaluator.oe(25, -13), CompleteEvaluator.oe(20, -13), CompleteEvaluator.oe(25, 8), CompleteEvaluator.oe(0, 23), CompleteEvaluator.oe(-20, 32), CompleteEvaluator.oe(-20, 32), CompleteEvaluator.oe(0, 23), CompleteEvaluator.oe(25, 8), CompleteEvaluator.oe(20, -13), CompleteEvaluator.oe(15, -19), CompleteEvaluator.oe(20, 2), CompleteEvaluator.oe(-5, 17), CompleteEvaluator.oe(-25, 23), CompleteEvaluator.oe(-25, 23), CompleteEvaluator.oe(-5, 17), CompleteEvaluator.oe(20, 2), CompleteEvaluator.oe(15, -19), CompleteEvaluator.oe(5, -35), CompleteEvaluator.oe(10, -10), CompleteEvaluator.oe(-15, 2), CompleteEvaluator.oe(-35, 8), CompleteEvaluator.oe(-35, 8), CompleteEvaluator.oe(-15, 2), CompleteEvaluator.oe(10, -10), CompleteEvaluator.oe(5, -35), CompleteEvaluator.oe(-5, -58), CompleteEvaluator.oe(0, -35), CompleteEvaluator.oe(-25, -19), CompleteEvaluator.oe(-45, -13), CompleteEvaluator.oe(-45, -13), CompleteEvaluator.oe(-25, -19), CompleteEvaluator.oe(0, -35), CompleteEvaluator.oe(-5, -58)};
    public StringBuffer debugSB;
    private int[] scaleFactor = new int[]{0};
    private int[] pawnMaterial = new int[]{0, 0};
    private int[] nonPawnMaterial = new int[]{0, 0};
    private int[] pcsq = new int[]{0, 0};
    private int[] space = new int[]{0, 0};
    private int[] positional = new int[]{0, 0};
    private int[] mobility = new int[]{0, 0};
    private int[] attacks = new int[]{0, 0};
    private int[] kingAttackersCount = new int[]{0, 0};
    private int[] kingSafety = new int[]{0, 0};
    private int[] pawnStructure = new int[]{0, 0};
    private int[] passedPawns = new int[]{0, 0};
    private long[] pawnCanAttack = new long[]{0L, 0L};
    private long[] mobilitySquares = new long[]{0L, 0L};
    private long[] kingZone = new long[]{0L, 0L};

    @Override
    public int evaluate1(IBoard board, AttacksInfo ai) {
        int whitePawns = BitboardUtils.popCount(board.getPawns() & board.getWhites());
        int blackPawns = BitboardUtils.popCount(board.getPawns() & board.getBlacks());
        int whiteKnights = BitboardUtils.popCount(board.getKnights() & board.getWhites());
        int blackKnights = BitboardUtils.popCount(board.getKnights() & board.getBlacks());
        int whiteBishops = BitboardUtils.popCount(board.getBishops() & board.getWhites());
        int blackBishops = BitboardUtils.popCount(board.getBishops() & board.getBlacks());
        int whiteRooks = BitboardUtils.popCount(board.getRooks() & board.getWhites());
        int blackRooks = BitboardUtils.popCount(board.getRooks() & board.getBlacks());
        int whiteQueens = BitboardUtils.popCount(board.getQueens() & board.getWhites());
        int blackQueens = BitboardUtils.popCount(board.getQueens() & board.getBlacks());
        this.scaleFactor[0] = 1000;
        int endgameValue = Endgame.evaluateEndgame(board, this.scaleFactor, whitePawns, blackPawns, whiteKnights, blackKnights, whiteBishops, blackBishops, whiteRooks, blackRooks, whiteQueens, blackQueens);
        if (endgameValue != Short.MAX_VALUE) {
            return endgameValue;
        }
        this.pawnMaterial[0] = whitePawns * PIECE_VALUES_OE[1];
        this.nonPawnMaterial[0] = whiteKnights * PIECE_VALUES_OE[2] + whiteBishops * PIECE_VALUES_OE[3] + whiteRooks * PIECE_VALUES_OE[4] + whiteQueens * PIECE_VALUES_OE[5] + ((board.getWhites() & board.getBishops() & 0xAA55AA55AA55AA55L) != 0L && (board.getWhites() & board.getBishops() & 0x55AA55AA55AA55AAL) != 0L ? BISHOP_PAIR : 0);
        this.pawnMaterial[1] = blackPawns * PIECE_VALUES_OE[1];
        this.nonPawnMaterial[1] = blackKnights * PIECE_VALUES_OE[2] + blackBishops * PIECE_VALUES_OE[3] + blackRooks * PIECE_VALUES_OE[4] + blackQueens * PIECE_VALUES_OE[5] + ((board.getBlacks() & board.getBishops() & 0xAA55AA55AA55AA55L) != 0L && (board.getBlacks() & board.getBishops() & 0x55AA55AA55AA55AAL) != 0L ? BISHOP_PAIR : 0);
        int nonPawnMat = CompleteEvaluator.e(this.nonPawnMaterial[0] + this.nonPawnMaterial[1]);
        int gamePhase = nonPawnMat >= 5900 ? 1000 : (nonPawnMat <= 1475 ? 0 : (nonPawnMat - 1475) * 1000 / 4425);
        int oe = (board.getColourToMove() == 0 ? TEMPO : -TEMPO) + this.pawnMaterial[0] - this.pawnMaterial[1] + this.nonPawnMaterial[0] - this.nonPawnMaterial[1];
        int value = (gamePhase * CompleteEvaluator.o(oe) + (1000 - gamePhase) * CompleteEvaluator.e(oe) * this.scaleFactor[0] / 1000) / 1000;
        assert (Math.abs(value) < 20000) : "Eval is outside limits";
        return value;
    }

    @Override
    public int evaluate2(IBoard board, AttacksInfo ai) {
        int blackQueens;
        int whiteQueens;
        int blackRooks;
        int whiteRooks;
        int blackBishops;
        int whiteBishops;
        int blackKnights;
        int whiteKnights;
        int blackPawns;
        int whitePawns = BitboardUtils.popCount(board.getPawns() & board.getWhites());
        int endgameValue = Endgame.evaluateEndgame(board, this.scaleFactor, whitePawns, blackPawns = BitboardUtils.popCount(board.getPawns() & board.getBlacks()), whiteKnights = BitboardUtils.popCount(board.getKnights() & board.getWhites()), blackKnights = BitboardUtils.popCount(board.getKnights() & board.getBlacks()), whiteBishops = BitboardUtils.popCount(board.getBishops() & board.getWhites()), blackBishops = BitboardUtils.popCount(board.getBishops() & board.getBlacks()), whiteRooks = BitboardUtils.popCount(board.getRooks() & board.getWhites()), blackRooks = BitboardUtils.popCount(board.getRooks() & board.getBlacks()), whiteQueens = BitboardUtils.popCount(board.getQueens() & board.getWhites()), blackQueens = BitboardUtils.popCount(board.getQueens() & board.getBlacks()));
        if (endgameValue != Short.MAX_VALUE) {
            return 0;
        }
        int nonPawnMat = CompleteEvaluator.e(this.nonPawnMaterial[0] + this.nonPawnMaterial[1]);
        int gamePhase = nonPawnMat >= 5900 ? 1000 : (nonPawnMat <= 1475 ? 0 : (nonPawnMat - 1475) * 1000 / 4425);
        this.pcsq[0] = 0;
        this.pcsq[1] = 0;
        this.positional[0] = 0;
        this.positional[1] = 0;
        this.mobility[0] = 0;
        this.mobility[1] = 0;
        this.kingAttackersCount[0] = 0;
        this.kingAttackersCount[1] = 0;
        this.kingSafety[0] = 0;
        this.kingSafety[1] = 0;
        this.pawnStructure[0] = 0;
        this.pawnStructure[1] = 0;
        this.passedPawns[0] = 0;
        this.passedPawns[1] = 0;
        this.mobilitySquares[0] = board.getWhites() ^ 0xFFFFFFFFFFFFFFFFL;
        this.mobilitySquares[1] = board.getBlacks() ^ 0xFFFFFFFFFFFFFFFFL;
        ai.build(board);
        long whitePawnsAux = board.getPawns() & board.getWhites();
        long blackPawnsAux = board.getPawns() & board.getBlacks();
        if (gamePhase > 0) {
            long whiteSafe = 0x3C3C3C00L & (ai.pawnAttacks[1] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.attackedSquares[1] ^ 0xFFFFFFFFFFFFFFFFL | ai.attackedSquares[0]);
            long blackSafe = 0x3C3C3C00000000L & (ai.pawnAttacks[0] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.attackedSquares[0] ^ 0xFFFFFFFFFFFFFFFFL | ai.attackedSquares[1]);
            long whiteBehindPawn = whitePawnsAux >>> 8 | whitePawnsAux >>> 16 | whitePawnsAux >>> 24;
            long blackBehindPawn = blackPawnsAux << 8 | blackPawnsAux << 16 | blackPawnsAux << 24;
            this.space[0] = SPACE * ((BitboardUtils.popCount(whiteSafe) + BitboardUtils.popCount(whiteSafe & whiteBehindPawn)) * (whiteKnights + whiteBishops) / 4);
            this.space[1] = SPACE * ((BitboardUtils.popCount(blackSafe) + BitboardUtils.popCount(blackSafe & blackBehindPawn)) * (blackKnights + blackBishops) / 4);
        } else {
            this.space[0] = 0;
            this.space[1] = 0;
        }
        this.pawnCanAttack[0] = ai.pawnAttacks[0];
        this.pawnCanAttack[1] = ai.pawnAttacks[1];
        for (int i = 0; i < 5; ++i) {
            whitePawnsAux <<= 8;
            blackPawnsAux >>>= 8;
            if ((whitePawnsAux &= (board.getPawns() & board.getBlacks() | ai.pawnAttacks[1]) ^ 0xFFFFFFFFFFFFFFFFL) == 0L && (blackPawnsAux &= (board.getPawns() & board.getWhites() | ai.pawnAttacks[0]) ^ 0xFFFFFFFFFFFFFFFFL) == 0L) break;
            this.pawnCanAttack[0] = this.pawnCanAttack[0] | ((whitePawnsAux & 0x7F7F7F7F7F7F7F7FL) << 9 | (whitePawnsAux & 0xFEFEFEFEFEFEFEFEL) << 7);
            this.pawnCanAttack[1] = this.pawnCanAttack[1] | ((blackPawnsAux & 0xFEFEFEFEFEFEFEFEL) >>> 9 | (blackPawnsAux & 0x7F7F7F7F7F7F7F7FL) >>> 7);
        }
        this.attacks[0] = this.evalAttacks(board, ai, 0, board.getBlacks());
        this.attacks[1] = this.evalAttacks(board, ai, 1, board.getWhites());
        this.kingZone[0] = this.bbAttacks.king[ai.kingIndex[0]];
        this.kingZone[0] = this.kingZone[0] | this.kingZone[0] << 8;
        this.kingZone[1] = this.bbAttacks.king[ai.kingIndex[1]];
        this.kingZone[1] = this.kingZone[1] | this.kingZone[1] >>> 8;
        long all = board.getAll();
        long square = 1L;
        for (int index = 0; index < 64; ++index) {
            if ((square & all) != 0L) {
                boolean isWhite = (board.getWhites() & square) != 0L;
                int us = isWhite ? 0 : 1;
                int them = isWhite ? 1 : 0;
                long mines = isWhite ? board.getWhites() : board.getBlacks();
                long others = isWhite ? board.getBlacks() : board.getWhites();
                int pcsqIndex = isWhite ? index : 63 - index;
                int rank = index >> 3;
                int relativeRank = isWhite ? rank : 7 - rank;
                int file = 7 - index & 7;
                long pieceAttacks = ai.attacksFromSquare[index];
                if ((square & board.getPawns()) != 0L) {
                    boolean passed;
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + pawnPcsq[pcsqIndex];
                    long myPawns = board.getPawns() & mines;
                    long otherPawns = board.getPawns() & others;
                    long adjacentFiles = BitboardUtils.FILES_ADJACENT[file];
                    long ranksForward = BitboardUtils.RANKS_FORWARD[us][rank];
                    long pawnFile = BitboardUtils.FILE[file];
                    long routeToPromotion = pawnFile & ranksForward;
                    long otherPawnsAheadAdjacent = ranksForward & adjacentFiles & otherPawns;
                    long pushSquare = isWhite ? square << 8 : square >>> 8;
                    boolean supported = (square & ai.pawnAttacks[us]) != 0L;
                    boolean doubled = (myPawns & routeToPromotion) != 0L;
                    boolean opposed = (otherPawns & routeToPromotion) != 0L;
                    boolean bl = passed = !doubled && !opposed && otherPawnsAheadAdjacent == 0L;
                    if (!passed) {
                        long stormedPawns;
                        boolean backward;
                        boolean candidate;
                        long myPawnsAheadAdjacent = ranksForward & adjacentFiles & myPawns;
                        long myPawnsBesideAndBehindAdjacent = BitboardUtils.RANK_AND_BACKWARD[us][rank] & adjacentFiles & myPawns;
                        boolean isolated = (myPawns & adjacentFiles) == 0L;
                        boolean bl2 = candidate = !doubled && !opposed && ((otherPawnsAheadAdjacent & (pieceAttacks ^ 0xFFFFFFFFFFFFFFFFL)) == 0L || BitboardUtils.popCount(myPawnsBesideAndBehindAdjacent) >= BitboardUtils.popCount(otherPawnsAheadAdjacent & (pieceAttacks ^ 0xFFFFFFFFFFFFFFFFL)));
                        boolean bl3 = !isolated && !candidate && myPawnsBesideAndBehindAdjacent == 0L && (pieceAttacks & otherPawns) == 0L && (BitboardUtils.RANK_AND_BACKWARD[us][isWhite ? BitboardUtils.getRankLsb(myPawnsAheadAdjacent) : BitboardUtils.getRankMsb(myPawnsAheadAdjacent)] & routeToPromotion & (board.getPawns() | ai.pawnAttacks[them])) != 0L ? true : (backward = false);
                        if (backward) {
                            int n2 = us;
                            this.pawnStructure[n2] = this.pawnStructure[n2] - PAWN_BACKWARDS[opposed ? 1 : 0];
                        }
                        if (isolated) {
                            int n3 = us;
                            this.pawnStructure[n3] = this.pawnStructure[n3] - PAWN_ISOLATED[opposed ? 1 : 0];
                        }
                        if (doubled) {
                            int n4 = us;
                            this.pawnStructure[n4] = this.pawnStructure[n4] - PAWN_DOUBLED[opposed ? 1 : 0];
                        }
                        if (!(supported || isolated || backward)) {
                            int n5 = us;
                            this.pawnStructure[n5] = this.pawnStructure[n5] - PAWN_UNSUPPORTED;
                        }
                        if (candidate) {
                            int n6 = us;
                            this.passedPawns[n6] = this.passedPawns[n6] + PAWN_CANDIDATE[relativeRank];
                        }
                        if ((square & 0x1818181818181818L) != 0L && relativeRank == 1 && (pushSquare & mines & (board.getPawns() ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                            int n7 = us;
                            this.pawnStructure[n7] = this.pawnStructure[n7] - PAWN_BLOCKADE;
                        }
                        if (gamePhase > 0 && relativeRank > 2 && (stormedPawns = otherPawnsAheadAdjacent & 0xEFEFEFEFEFEFEFEFL & 0xF7F7F7F7F7F7F7F7L) != 0L) {
                            int otherKingFile = 7 - ai.kingIndex[them] & 7;
                            if ((stormedPawns & BitboardUtils.FILE[otherKingFile]) != 0L) {
                                int n8 = us;
                                this.pawnStructure[n8] = this.pawnStructure[n8] + PAWN_STORM_CENTER[relativeRank];
                            } else if ((stormedPawns & BitboardUtils.FILES_ADJACENT[otherKingFile]) != 0L) {
                                int n9 = us;
                                this.pawnStructure[n9] = this.pawnStructure[n9] + PAWN_STORM[relativeRank];
                            }
                        }
                    } else {
                        long backFile = this.bbAttacks.getRookAttacks(index, all) & pawnFile & BitboardUtils.RANKS_BACKWARD[us][rank];
                        long attackedAndNotDefendedRoute = (routeToPromotion & ai.attackedSquares[them] | ((backFile & (board.getRooks() | board.getQueens()) & others) != 0L ? routeToPromotion : 0L)) & ((routeToPromotion & ai.attackedSquares[us] | ((backFile & (board.getRooks() | board.getQueens()) & mines) != 0L ? routeToPromotion : 0L)) ^ 0xFFFFFFFFFFFFFFFFL);
                        boolean connected = (this.bbAttacks.king[index] & adjacentFiles & myPawns) != 0L;
                        boolean outside = otherPawns != 0L && ((square & BitboardUtils.FILES_LEFT[3]) != 0L && (board.getPawns() & BitboardUtils.FILES_LEFT[file]) == 0L || (square & BitboardUtils.FILES_RIGHT[4]) != 0L && (board.getPawns() & BitboardUtils.FILES_RIGHT[file]) == 0L);
                        boolean mobile = (pushSquare & (all | attackedAndNotDefendedRoute)) == 0L;
                        boolean runner = mobile && (routeToPromotion & all) == 0L && attackedAndNotDefendedRoute == 0L;
                        int n10 = us;
                        this.passedPawns[n10] = this.passedPawns[n10] + PAWN_PASSER[relativeRank];
                        if (relativeRank >= 2) {
                            int pushIndex = isWhite ? index + 8 : index - 8;
                            int n11 = us;
                            this.passedPawns[n11] = this.passedPawns[n11] + (BitboardUtils.distance(pushIndex, ai.kingIndex[them]) * PAWN_PASSER_OTHER_KING_DISTANCE[relativeRank] - BitboardUtils.distance(pushIndex, ai.kingIndex[us]) * PAWN_PASSER_MY_KING_DISTANCE[relativeRank]);
                        }
                        if (outside) {
                            int n12 = us;
                            this.passedPawns[n12] = this.passedPawns[n12] + PAWN_PASSER_OUTSIDE[relativeRank];
                        }
                        if (supported) {
                            int n13 = us;
                            this.passedPawns[n13] = this.passedPawns[n13] + PAWN_PASSER_SUPPORTED[relativeRank];
                        } else if (connected) {
                            int n14 = us;
                            this.passedPawns[n14] = this.passedPawns[n14] + PAWN_PASSER_CONNECTED[relativeRank];
                        }
                        if (runner) {
                            int n15 = us;
                            this.passedPawns[n15] = this.passedPawns[n15] + PAWN_PASSER_RUNNER[relativeRank];
                        } else if (mobile) {
                            int n16 = us;
                            this.passedPawns[n16] = this.passedPawns[n16] + PAWN_PASSER_MOBILE[relativeRank];
                        }
                    }
                    if (gamePhase > 0 && (pawnFile & (ranksForward ^ 0xFFFFFFFFFFFFFFFFL) & this.kingZone[us] & 0xEFEFEFEFEFEFEFEFL & 0xF7F7F7F7F7F7F7F7L) != 0L) {
                        int n17 = us;
                        this.pawnStructure[n17] = this.pawnStructure[n17] + ((pawnFile & board.getKings() & mines) != 0L ? PAWN_SHIELD_CENTER[relativeRank] : PAWN_SHIELD[relativeRank]);
                    }
                } else if ((square & board.getKnights()) != 0L) {
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + knightPcsq[pcsqIndex];
                    safeAttacks = pieceAttacks & (ai.pawnAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL);
                    int n18 = us;
                    this.mobility[n18] = this.mobility[n18] + MOBILITY[2][BitboardUtils.popCount(safeAttacks & this.mobilitySquares[us])];
                    kingAttacks = safeAttacks & this.kingZone[them];
                    if (kingAttacks != 0L) {
                        int n19 = us;
                        this.kingSafety[n19] = this.kingSafety[n19] + PIECE_ATTACKS_KING[2] * BitboardUtils.popCount(kingAttacks);
                        int n20 = us;
                        this.kingAttackersCount[n20] = this.kingAttackersCount[n20] + 1;
                    }
                    if ((square & OUTPOST_MASK[us] & (this.pawnCanAttack[them] ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        int n21 = us;
                        this.positional[n21] = this.positional[n21] + KNIGHT_OUTPOST[(square & ai.pawnAttacks[us]) != 0L ? 1 : 0];
                    }
                } else if ((square & board.getBishops()) != 0L) {
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + bishopPcsq[pcsqIndex];
                    safeAttacks = pieceAttacks & (ai.pawnAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL);
                    int n22 = us;
                    this.mobility[n22] = this.mobility[n22] + MOBILITY[3][BitboardUtils.popCount(safeAttacks & this.mobilitySquares[us])];
                    kingAttacks = safeAttacks & this.kingZone[them];
                    if (kingAttacks != 0L) {
                        int n23 = us;
                        this.kingSafety[n23] = this.kingSafety[n23] + PIECE_ATTACKS_KING[3] * BitboardUtils.popCount(kingAttacks);
                        int n24 = us;
                        this.kingAttackersCount[n24] = this.kingAttackersCount[n24] + 1;
                    }
                    if ((square & OUTPOST_MASK[us] & (this.pawnCanAttack[them] ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        int n25 = us;
                        this.positional[n25] = this.positional[n25] + BISHOP_OUTPOST[(square & ai.pawnAttacks[us]) != 0L ? 1 : 0];
                    }
                    int n26 = us;
                    this.positional[n26] = this.positional[n26] - BISHOP_MY_PAWNS_IN_COLOR_PENALTY * BitboardUtils.popCount(board.getPawns() & mines & BitboardUtils.getSameColorSquares(square));
                    if ((BISHOP_TRAPPING[index] & board.getPawns() & others) != 0L) {
                        int n27 = us;
                        this.mobility[n27] = this.mobility[n27] - BISHOP_TRAPPED_PENALTY[(BISHOP_TRAPPING_GUARD[index] & board.getPawns() & others) != 0L ? 1 : 0];
                    }
                } else if ((square & board.getRooks()) != 0L) {
                    long pawnsAligned;
                    long rookFile;
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + rookPcsq[pcsqIndex];
                    safeAttacks = pieceAttacks & (ai.pawnAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.knightAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.bishopAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL);
                    int mobilityCount = BitboardUtils.popCount(safeAttacks & this.mobilitySquares[us]);
                    int n28 = us;
                    this.mobility[n28] = this.mobility[n28] + MOBILITY[4][mobilityCount];
                    kingAttacks = safeAttacks & this.kingZone[them];
                    if (kingAttacks != 0L) {
                        int n29 = us;
                        this.kingSafety[n29] = this.kingSafety[n29] + PIECE_ATTACKS_KING[4] * BitboardUtils.popCount(kingAttacks);
                        int n30 = us;
                        this.kingAttackersCount[n30] = this.kingAttackersCount[n30] + 1;
                    }
                    if ((square & OUTPOST_MASK[us] & (this.pawnCanAttack[them] ^ 0xFFFFFFFFFFFFFFFFL)) != 0L) {
                        int n31 = us;
                        this.positional[n31] = this.positional[n31] + ROOK_OUTPOST[(square & ai.pawnAttacks[us]) != 0L ? 1 : 0];
                    }
                    if (((rookFile = BitboardUtils.FILE[file]) & board.getPawns() & mines) == 0L) {
                        int n32 = us;
                        this.positional[n32] = this.positional[n32] + ROOK_FILE[(rookFile & board.getPawns()) == 0L ? 0 : 1];
                    }
                    if (relativeRank >= 4 && (pawnsAligned = BitboardUtils.RANK[rank] & board.getPawns() & others) != 0L) {
                        int n33 = us;
                        this.positional[n33] = this.positional[n33] + ROOK_7 * BitboardUtils.popCount(pawnsAligned);
                    }
                    if ((square & ROOK_TRAPPING[ai.kingIndex[us]]) != 0L && mobilityCount < ROOK_TRAPPED_PENALTY.length) {
                        int n34 = us;
                        this.positional[n34] = this.positional[n34] - ROOK_TRAPPED_PENALTY[mobilityCount];
                    }
                } else if ((square & board.getQueens()) != 0L) {
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + queenPcsq[pcsqIndex];
                    safeAttacks = pieceAttacks & (ai.pawnAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.knightAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.bishopAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL) & (ai.rookAttacks[them] ^ 0xFFFFFFFFFFFFFFFFL);
                    int n35 = us;
                    this.mobility[n35] = this.mobility[n35] + MOBILITY[5][BitboardUtils.popCount(safeAttacks & this.mobilitySquares[us])];
                    kingAttacks = safeAttacks & this.kingZone[them];
                    if (kingAttacks != 0L) {
                        int n36 = us;
                        this.kingSafety[n36] = this.kingSafety[n36] + PIECE_ATTACKS_KING[5] * BitboardUtils.popCount(kingAttacks);
                        int n37 = us;
                        this.kingAttackersCount[n37] = this.kingAttackersCount[n37] + 1;
                    }
                } else if ((square & board.getKings()) != 0L) {
                    int n = us;
                    this.pcsq[n] = this.pcsq[n] + kingPcsq[pcsqIndex];
                }
            }
            square <<= 1;
        }
        int oe = this.pcsq[0] - this.pcsq[1] + this.space[0] - this.space[1] + this.positional[0] - this.positional[1] + this.attacks[0] - this.attacks[1] + this.mobility[0] - this.mobility[1] + this.pawnStructure[0] - this.pawnStructure[1] + this.passedPawns[0] - this.passedPawns[1] + CompleteEvaluator.oeShr(6, KING_SAFETY_PONDER[this.kingAttackersCount[0]] * this.kingSafety[0] - KING_SAFETY_PONDER[this.kingAttackersCount[1]] * this.kingSafety[1]);
        int value = (gamePhase * CompleteEvaluator.o(oe) + (1000 - gamePhase) * CompleteEvaluator.e(oe) * this.scaleFactor[0] / 1000) / 1000;
        assert (Math.abs(value) < 20000) : "Eval is outside limits";
        return value;
    }

    private int evalAttacks(IBoard board, AttacksInfo ai, int us, long others) {
        long pinnedNotPawn;
        long superiorAttacks;
        int superiorAttacksCount;
        long lsb;
        int attacks = 0;
        for (long attackedByPawn = ai.pawnAttacks[us] & others & (board.getPawns() ^ 0xFFFFFFFFFFFFFFFFL); attackedByPawn != 0L; attackedByPawn &= lsb ^ 0xFFFFFFFFFFFFFFFFL) {
            lsb = BitboardUtils.lsb(attackedByPawn);
            attacks += PAWN_ATTACKS[this.getPieceType(board, lsb)];
        }
        long otherWeak = ai.attackedSquares[us] & others & (ai.pawnAttacks[1 - us] ^ 0xFFFFFFFFFFFFFFFFL);
        if (otherWeak != 0L) {
            long lsb2;
            long lsb3;
            for (long attackedByMinor = (ai.knightAttacks[us] | ai.bishopAttacks[us]) & otherWeak; attackedByMinor != 0L; attackedByMinor &= lsb3 ^ 0xFFFFFFFFFFFFFFFFL) {
                lsb3 = BitboardUtils.lsb(attackedByMinor);
                attacks += MINOR_ATTACKS[this.getPieceType(board, lsb3)];
            }
            for (long attackedByMajor = (ai.rookAttacks[us] | ai.queenAttacks[us]) & otherWeak; attackedByMajor != 0L; attackedByMajor &= lsb2 ^ 0xFFFFFFFFFFFFFFFFL) {
                lsb2 = BitboardUtils.lsb(attackedByMajor);
                attacks += MAJOR_ATTACKS[this.getPieceType(board, lsb2)];
            }
        }
        if ((superiorAttacksCount = BitboardUtils.popCount(superiorAttacks = ai.pawnAttacks[us] & others & (board.getPawns() ^ 0xFFFFFFFFFFFFFFFFL) | (ai.knightAttacks[us] | ai.bishopAttacks[us]) & others & (board.getRooks() | board.getQueens()) | ai.rookAttacks[us] & others & board.getQueens())) >= 2) {
            attacks += superiorAttacksCount * HUNG_PIECES;
        }
        if ((pinnedNotPawn = ai.pinnedPieces & (board.getPawns() ^ 0xFFFFFFFFFFFFFFFFL) & others) != 0L) {
            attacks += PINNED_PIECE * BitboardUtils.popCount(pinnedNotPawn);
        }
        return attacks;
    }

    public int getPieceType(IBoard board, long bitboard) {
        return (board.getPawns() & bitboard) != 0L ? 1 : ((board.getKnights() & bitboard) != 0L ? 2 : ((board.getBishops() & bitboard) != 0L ? 3 : ((board.getRooks() & bitboard) != 0L ? 4 : ((board.getQueens() & bitboard) != 0L ? 5 : ((board.getKings() & bitboard) != 0L ? 6 : 46)))));
    }
}

