/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class PSTConstants_LKG {
    private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
    public static final int[] PAWN_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, -16, -5, -1, 4, 4, -1, -5, -16, -16, -5, 2, 4, 4, 2, -5, -16, -16, -5, 5, 14, 14, 5, -5, -16, -16, -5, 5, 24, 24, 5, -5, -16, -16, -5, 2, 14, 14, 2, -5, -16, -16, -5, -1, 4, 4, -1, -5, -16, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] BISHOP_O = Utils.reverseSpecial(new int[]{-8, -8, -6, -4, -4, -6, -8, -8, -8, 0, -2, 0, 0, -2, 0, -8, -6, -2, 3, 1, 1, 3, -2, -6, -4, 0, 1, 7, 7, 1, 0, -4, -4, 0, 1, 7, 7, 1, 0, -4, -6, -2, 3, 1, 1, 3, -2, -6, -8, 0, -2, 0, 0, -2, 0, -8, -18, -18, -16, -14, -14, -16, -18, -18});
    public static final int[] KNIGHT_O = Utils.reverseSpecial(new int[]{-72, -25, -15, -10, -10, -15, -25, -72, -20, -10, 0, 4, 4, 0, -10, -20, -5, 4, 14, 19, 19, 14, 4, -5, -5, 4, 14, 19, 19, 14, 4, -5, -10, 0, 9, 14, 14, 9, 0, -10, -20, -10, 0, 4, 4, 0, -10, -20, -35, -25, -15, -10, -10, -15, -25, -35, -50, -40, -30, -25, -25, -30, -40, -50});
    public static final int[] ROOK_O = Utils.reverseSpecial(new int[]{-6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6, -6, -2, 0, 3, 3, 0, -2, -6});
    public static final int[] QUEEN_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] KING_O = Utils.reverseSpecial(new int[]{38, 48, 28, 8, 8, 28, 48, 38, 48, 58, 38, 18, 18, 38, 58, 48, 58, 68, 48, 28, 28, 48, 68, 58, 68, 78, 58, 38, 38, 58, 78, 68, 78, 87, 68, 48, 48, 68, 87, 78, 87, 98, 78, 58, 58, 78, 98, 87, 107, 117, 98, 78, 78, 98, 117, 107, 117, 128, 107, 87, 87, 107, 128, 117});
    public static final int[] PAWN_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] BISHOP_E = Utils.reverseSpecial(new int[]{-18, -12, -9, -6, -6, -9, -12, -18, -12, -6, -3, 0, 0, -3, -6, -12, -9, -3, 0, 2, 2, 0, -3, -9, -6, 0, 2, 5, 5, 2, 0, -6, -6, 0, 2, 5, 5, 2, 0, -6, -9, -3, 0, 2, 2, 0, -3, -9, -12, -6, -3, 0, 0, -3, -6, -12, -18, -12, -9, -6, -6, -9, -12, -18});
    public static final int[] KNIGHT_E = Utils.reverseSpecial(new int[]{-40, -30, -20, -15, -15, -20, -30, -40, -30, -20, -10, -5, -5, -10, -20, -30, -20, -10, 0, 4, 4, 0, -10, -20, -15, -5, 4, 9, 9, 4, -5, -15, -15, -5, 4, 9, 9, 4, -5, -15, -20, -10, 0, 4, 4, 0, -10, -20, -30, -20, -10, -5, -5, -10, -20, -30, -40, -30, -20, -15, -15, -20, -30, -40});
    public static final int[] ROOK_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] QUEEN_E = Utils.reverseSpecial(new int[]{-24, -16, -12, -8, -8, -12, -16, -24, -16, -8, -4, 0, 0, -4, -8, -16, -12, -4, 0, 3, 3, 0, -4, -12, -8, 0, 3, 7, 7, 3, 0, -8, -8, 0, 3, 7, 7, 3, 0, -8, -12, -4, 0, 3, 3, 0, -4, -12, -16, -8, -4, 0, 0, -4, -8, -16, -24, -16, -12, -8, -8, -12, -16, -24});
    public static final int[] KING_E = Utils.reverseSpecial(new int[]{6, 30, 42, 54, 54, 42, 30, 6, 30, 54, 66, 78, 78, 66, 54, 30, 42, 66, 78, 89, 89, 78, 66, 42, 54, 78, 89, 101, 101, 89, 78, 54, 54, 78, 89, 101, 101, 89, 78, 54, 42, 66, 78, 89, 89, 78, 66, 42, 30, 54, 66, 78, 78, 66, 54, 30, 6, 30, 42, 54, 54, 42, 30, 6});

    public static final int getMoveScores_o(int move) {
        int type = MoveInt.getFigureType(move);
        int from = MoveInt.getFromFieldID(move);
        int to = MoveInt.getToFieldID(move);
        if (!MoveInt.isWhite(move)) {
            from = HORIZONTAL_SYMMETRY[from];
            to = HORIZONTAL_SYMMETRY[to];
        }
        int[] pst = PSTConstants_LKG.getArray_o(type);
        return pst[to] - pst[from];
    }

    public static final int getMoveScores_e(int move) {
        int type = MoveInt.getFigureType(move);
        int from = MoveInt.getFromFieldID(move);
        int to = MoveInt.getToFieldID(move);
        if (!MoveInt.isWhite(move)) {
            from = HORIZONTAL_SYMMETRY[from];
            to = HORIZONTAL_SYMMETRY[to];
        }
        int[] pst = PSTConstants_LKG.getArray_e(type);
        return pst[to] - pst[from];
    }

    public static final int getPieceScores_o(int field, int type) {
        int[] pst = PSTConstants_LKG.getArray_o(type);
        return pst[field];
    }

    public static final int getPieceScores_e(int field, int type) {
        int[] pst = PSTConstants_LKG.getArray_e(type);
        return pst[field];
    }

    public static final int[] getArray_o(int type) {
        switch (type) {
            case 1: {
                return PAWN_O;
            }
            case 6: {
                return KING_O;
            }
            case 2: {
                return KNIGHT_O;
            }
            case 3: {
                return BISHOP_O;
            }
            case 4: {
                return ROOK_O;
            }
            case 5: {
                return QUEEN_O;
            }
        }
        throw new IllegalStateException();
    }

    public static final int[] getArray_e(int type) {
        switch (type) {
            case 1: {
                return PAWN_E;
            }
            case 6: {
                return KING_E;
            }
            case 2: {
                return KNIGHT_E;
            }
            case 3: {
                return BISHOP_E;
            }
            case 4: {
                return ROOK_E;
            }
            case 5: {
                return QUEEN_E;
            }
        }
        throw new IllegalStateException();
    }
}

