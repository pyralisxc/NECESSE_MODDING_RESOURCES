/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.impl1.internal.Util;

public class EvalConstants {
    public static final int SIDE_TO_MOVE_BONUS = 0;
    public static final int SCORE_DRAW = 0;
    public static final int[] OTHER_SCORES;
    public static final int IX_ROOK_FILE_SEMI_OPEN = 0;
    public static final int IX_ROOK_FILE_SEMI_OPEN_ISOLATED = 1;
    public static final int IX_ROOK_FILE_OPEN = 2;
    public static final int IX_ROOK_7TH_RANK = 3;
    public static final int IX_ROOK_BATTERY = 4;
    public static final int IX_BISHOP_LONG = 5;
    public static final int IX_BISHOP_PRISON = 6;
    public static final int IX_SPACE = 7;
    public static final int IX_DRAWISH = 8;
    public static final int IX_CASTLING = 9;
    public static final int IX_ROOK_TRAPPED = 10;
    public static final int IX_OUTPOST = 11;
    public static final int IX_ONLY_MAJOR_DEFENDERS = 12;
    public static final int[] THREATS_MG;
    public static final int[] THREATS_EG;
    public static final int IX_MULTIPLE_PAWN_ATTACKS = 0;
    public static final int IX_PAWN_ATTACKS = 1;
    public static final int IX_QUEEN_ATTACKED = 2;
    public static final int IX_PAWN_PUSH_THREAT = 3;
    public static final int IX_ROOK_ATTACKED = 4;
    public static final int IX_QUEEN_ATTACKED_MINOR = 5;
    public static final int IX_MAJOR_ATTACKED = 6;
    public static final int IX_UNUSED_OUTPOST = 7;
    public static final int IX_PAWN_ATTACKED = 8;
    public static final int[] PAWN_SCORES;
    public static final int IX_PAWN_DOUBLE = 0;
    public static final int IX_PAWN_ISOLATED = 1;
    public static final int IX_PAWN_BACKWARD = 2;
    public static final int IX_PAWN_INVERSE = 3;
    public static final int[] IMBALANCE_SCORES;
    public static final int IX_ROOK_PAIR = 0;
    public static final int IX_BISHOP_DOUBLE = 1;
    public static final int IX_QUEEN_NIGHT = 2;
    public static final int[] PHASE;
    public static final int[] MATERIAL;
    public static final int[] MATERIAL_SEE;
    public static final int[] NIGHT_PAWN;
    public static final int[] ROOK_PAWN;
    public static final int[] PINNED;
    public static final int[] DISCOVERED;
    public static final int[] DOUBLE_ATTACKED;
    public static final int[] BISHOP_PAWN;
    public static final int[] SPACE;
    public static final int[] PAWN_BLOCKAGE;
    public static final int[] PAWN_CONNECTED;
    public static final int[] PAWN_NEIGHBOUR;
    public static final int[][] SHIELD_BONUS_MG;
    public static final int[][] SHIELD_BONUS_EG;
    public static final int[] PASSED_SCORE_EG;
    public static final int[] PASSED_CANDIDATE;
    public static final float[] PASSED_KING_MULTI;
    public static final float[] PASSED_MULTIPLIERS;
    public static final int[] KS_SCORES;
    public static final int[] KS_QUEEN_TROPISM;
    public static final int[] KS_CHECK_QUEEN;
    public static final int[] KS_NO_FRIENDS;
    public static final int[] KS_ATTACKS;
    public static final int[] KS_DOUBLE_ATTACKS;
    public static final int[] KS_ATTACK_PATTERN;
    public static final int[] KS_OTHER;
    public static final int[] MOBILITY_KNIGHT_MG;
    public static final int[] MOBILITY_KNIGHT_EG;
    public static final int[] MOBILITY_BISHOP_MG;
    public static final int[] MOBILITY_BISHOP_EG;
    public static final int[] MOBILITY_ROOK_MG;
    public static final int[] MOBILITY_ROOK_EG;
    public static final int[] MOBILITY_QUEEN_MG;
    public static final int[] MOBILITY_QUEEN_EG;
    public static final int[] MOBILITY_KING_MG;
    public static final int[] MOBILITY_KING_EG;
    public static final int[][][] PSQT_MG;
    public static final int[][][] PSQT_EG;
    public static final long[] ROOK_PRISON;
    public static final long[] BISHOP_PRISON;
    public static final int[] PROMOTION_SCORE_SEE;
    public static final int[] MIRRORED_LEFT_RIGHT;
    public static final int[] MIRRORED_UP_DOWN;

    public static final void initPSQT(IBoardConfig config) {
        EvalConstants.PSQT_MG[1][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_PAWN_O());
        EvalConstants.PSQT_EG[1][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_PAWN_E());
        EvalConstants.PSQT_MG[2][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_KNIGHT_O());
        EvalConstants.PSQT_EG[2][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_KNIGHT_E());
        EvalConstants.PSQT_MG[3][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_BISHOP_O());
        EvalConstants.PSQT_EG[3][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_BISHOP_E());
        EvalConstants.PSQT_MG[4][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_ROOK_O());
        EvalConstants.PSQT_EG[4][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_ROOK_E());
        EvalConstants.PSQT_MG[5][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_QUEEN_O());
        EvalConstants.PSQT_EG[5][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_QUEEN_E());
        EvalConstants.PSQT_MG[6][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_KING_O());
        EvalConstants.PSQT_EG[6][0] = EvalConstants.convertDoubleArray2IntArray(config.getPST_KING_E());
        for (int piece = 1; piece <= 6; ++piece) {
            for (int i = 0; i < 64; ++i) {
                EvalConstants.PSQT_MG[piece][1][i] = -PSQT_MG[piece][0][MIRRORED_UP_DOWN[i]];
                EvalConstants.PSQT_EG[piece][1][i] = -PSQT_EG[piece][0][MIRRORED_UP_DOWN[i]];
            }
        }
    }

    private static final int[] convertDoubleArray2IntArray(double[] src) {
        int[] result = new int[src.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = (int)src[i];
        }
        return result;
    }

    private static void initMgEg(int[] array, int[] arrayMg, int[] arrayEg) {
        for (int i = 0; i < array.length; ++i) {
            array[i] = EvalConstants.score(arrayMg[i], arrayEg[i]);
        }
    }

    public static int score(int mgScore, int egScore) {
        return (mgScore << 16) + egScore;
    }

    public static void main(String[] args) {
        int i = 0;
        while (i < 64) {
            int[] nArray = PSQT_EG[6][0];
            int n = i++;
            nArray[n] = nArray[n] + 20;
        }
    }

    static {
        int piece;
        int i;
        OTHER_SCORES = new int[]{-8, 16, 18, 8, 18, 12, -158, 12, 56, 20, -44, 28, -8};
        THREATS_MG = new int[]{34, 68, 108, 12, 66, 52, 8, 16, -6};
        THREATS_EG = new int[]{32, 14, -38, 16, 10, -12, 24, 4, 10};
        PAWN_SCORES = new int[]{10, 10, 12, 6};
        IMBALANCE_SCORES = new int[]{-28, 54, 20};
        PHASE = new int[]{0, 0, 3, 3, 5, 9};
        MATERIAL = new int[]{0, 100, 396, 416, 706, 1302, 3000};
        MATERIAL_SEE = new int[]{0, 100, 300, 300, 500, 900, 3000};
        NIGHT_PAWN = new int[]{68, -14, -2, 2, 8, 12, 20, 30, 36, 55, 70, 70, 70};
        ROOK_PAWN = new int[]{48, -4, -4, -4, -4, 0, 0, 0, 0, 0, 0, 0, 0};
        PINNED = new int[]{0, 2, -18, -54, -68, -84};
        DISCOVERED = new int[]{0, -14, 128, 110, 180, 0, 28};
        DOUBLE_ATTACKED = new int[]{0, 16, 34, 64, -4, -6, 0};
        BISHOP_PAWN = new int[]{20, 8, 6, 0, -6, -12, -22, -32, -46};
        SPACE = new int[]{0, 0, 0, 0, 0, -6, -6, -8, -7, -4, -4, -2, 0, -1, 0, 3, 7};
        PAWN_BLOCKAGE = new int[]{0, 0, -10, 2, 6, 28, 66, 196};
        PAWN_CONNECTED = new int[]{0, 0, 12, 14, 20, 58, 122};
        PAWN_NEIGHBOUR = new int[]{0, 0, 4, 10, 26, 88, 326};
        SHIELD_BONUS_MG = new int[][]{{0, 18, 14, 4, -24, -38, -270}, {0, 52, 36, 6, -44, 114, -250}, {0, 52, 4, 4, 46, 152, 16}, {0, 16, 4, 6, -16, 106, 2}};
        SHIELD_BONUS_EG = new int[][]{{0, -48, -18, -16, 8, -30, -28}, {0, -16, -26, -10, 42, 6, 20}, {0, 0, 8, 0, 28, 24, 38}, {0, -22, -14, 0, 38, 10, 60}};
        PASSED_SCORE_EG = new int[]{0, 14, 18, 34, 62, 128, 238};
        PASSED_CANDIDATE = new int[]{0, 2, 2, 8, 14, 40};
        PASSED_KING_MULTI = new float[]{0.0f, 1.5f, 1.3f, 1.2f, 1.1f, 1.0f, 0.8f, 0.8f, 0.9f};
        PASSED_MULTIPLIERS = new float[]{0.5f, 1.3f, 0.4f, 1.2f, 0.7f, 1.7f, 0.6f, 1.8f};
        KS_SCORES = new int[]{0, 5, 10, 15, 20, 25, 30, 35, 40, 50, 70, 80, 100, 110, 130, 160, 190, 240, 290, 330, 400, 480, 540, 630, 620, 800};
        KS_QUEEN_TROPISM = new int[]{0, 0, 1, 1, 1, 1, 0, 0};
        KS_CHECK_QUEEN = new int[]{0, 0, 0, 0, 2, 3, 4, 4, 4, 4, 3, 3, 3, 2, 0, 0, 0};
        KS_NO_FRIENDS = new int[]{6, 4, 0, 5, 5, 5, 6, 6, 7, 8, 9, 10};
        KS_ATTACKS = new int[]{0, 3, 3, 3, 3, 3, 4, 4, 5, 6, 6, 2, 9};
        KS_DOUBLE_ATTACKS = new int[]{0, 1, 3, 4, 0, -6, 0, 0, 0};
        KS_ATTACK_PATTERN = new int[]{4, 1, 2, 2, 2, 1, 2, 2, 1, 0, 1, 1, 1, 1, 1, 2, 2, 1, 2, 2, 2, 2, 3, 3, 1, 1, 3, 3, 3, 3, 4, 4};
        KS_OTHER = new int[]{3, 4, 3, 1};
        MOBILITY_KNIGHT_MG = new int[]{-34, -16, -6, 0, 12, 16, 26, 28, 56};
        MOBILITY_KNIGHT_EG = new int[]{-98, -34, -12, 0, 4, 12, 12, 14, 16};
        MOBILITY_BISHOP_MG = new int[]{-16, 2, 16, 24, 28, 36, 38, 40, 41, 42, 58, 82, 90, 120};
        MOBILITY_BISHOP_EG = new int[]{-36, -8, 6, 18, 28, 28, 36, 38, 42, 44, 46, 50, 54, 60};
        MOBILITY_ROOK_MG = new int[]{-34, -24, -18, -14, -12, -4, 0, 8, 16, 26, 30, 40, 52, 68, 66};
        MOBILITY_ROOK_EG = new int[]{-38, -12, 0, 8, 18, 24, 28, 28, 34, 34, 38, 40, 40, 42, 46};
        MOBILITY_QUEEN_MG = new int[]{-16, -14, -12, -10, -8, -8, -6, -4, -4, -4, -2, -2, -2, 0, 0, 0, 2, 8, 16, 22, 32, 48, 66, 70, 80, 90, 100, 110};
        MOBILITY_QUEEN_EG = new int[]{-102, -82, -82, -72, -63, -54, -40, -24, -10, -2, 8, 24, 30, 32, 38, 54, 60, 65, 70, 72, 74, 76, 78, 80, 82, 84, 86, 90};
        MOBILITY_KING_MG = new int[]{-12, -10, -8, 0, 10, 26, 36, 70, 85};
        MOBILITY_KING_EG = new int[]{-38, -2, 8, 8, 10, 12, 12, 26, 35};
        PSQT_MG = new int[7][2][64];
        PSQT_EG = new int[7][2][64];
        ROOK_PRISON = new long[]{0L, Long.MIN_VALUE, -4611686018427387904L, -2305843009213693952L, 0L, 0x300000000000000L, 0x100000000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 128L, 192L, 224L, 0L, 3L, 1L, 0L};
        BISHOP_PRISON = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x20400000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4020000000000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x402000L, 0L, 0L, 0L, 0L, 0L, 0L, 132096L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        PROMOTION_SCORE_SEE = new int[]{0, 0, MATERIAL_SEE[2] - MATERIAL_SEE[1], MATERIAL_SEE[3] - MATERIAL_SEE[1], MATERIAL_SEE[4] - MATERIAL_SEE[1], MATERIAL_SEE[5] - MATERIAL_SEE[1]};
        MIRRORED_LEFT_RIGHT = new int[64];
        for (i = 0; i < 64; ++i) {
            EvalConstants.MIRRORED_LEFT_RIGHT[i] = i / 8 * 8 + 7 - (i & 7);
        }
        MIRRORED_UP_DOWN = new int[64];
        for (i = 0; i < 64; ++i) {
            EvalConstants.MIRRORED_UP_DOWN[i] = (7 - i / 8) * 8 + (i & 7);
        }
        for (piece = 1; piece <= 6; ++piece) {
            Util.reverse(PSQT_MG[piece][0]);
            Util.reverse(PSQT_EG[piece][0]);
        }
        for (piece = 1; piece <= 6; ++piece) {
            for (int i2 = 0; i2 < 64; ++i2) {
                EvalConstants.PSQT_MG[piece][1][i2] = -PSQT_MG[piece][0][MIRRORED_UP_DOWN[i2]];
                EvalConstants.PSQT_EG[piece][1][i2] = -PSQT_EG[piece][0][MIRRORED_UP_DOWN[i2]];
            }
        }
        Util.reverse(ROOK_PRISON);
        Util.reverse(BISHOP_PRISON);
    }
}

