/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class PSTConstants_RI {
    private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
    public static final int[] PAWN_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 88, 111, 120, 129, 132, 140, 132, 108, 64, 78, 92, 95, 95, 101, 100, 78, 55, 72, 77, 95, 95, 85, 80, 59, 66, 61, 61, 76, 77, 68, 84, 69, 50, 50, 60, 52, 61, 72, 58, 57, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_PASSED_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 234, 209, 207, 197, 171, 171, 156, 195, 148, 141, 154, 157, 133, 160, 127, 117, 78, 115, 108, 97, 107, 116, 110, 84, 58, 87, 99, 96, 90, 111, 93, 72, 60, 80, 81, 72, 74, 82, 90, 98, 48, 66, 85, 59, 69, 96, 86, 65, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] BISHOP_O = Utils.reverseSpecial(new int[]{265, 265, 272, 276, 284, 270, 272, 278, 276, 284, 291, 303, 307, 310, 313, 282, 283, 305, 309, 317, 328, 311, 315, 302, 294, 295, 305, 318, 317, 295, 281, 289, 289, 298, 288, 303, 312, 292, 288, 289, 284, 296, 302, 292, 287, 286, 300, 285, 305, 297, 296, 300, 286, 295, 301, 302, 286, 276, 269, 270, 267, 269, 275, 264});
    public static final int[] KNIGHT_O = Utils.reverseSpecial(new int[]{190, 263, 271, 284, 298, 286, 258, 185, 271, 273, 303, 319, 328, 310, 290, 267, 276, 299, 319, 314, 316, 328, 308, 286, 273, 286, 296, 296, 290, 299, 277, 284, 259, 266, 279, 278, 273, 280, 275, 250, 240, 259, 255, 270, 276, 265, 272, 238, 237, 245, 256, 248, 252, 250, 244, 231, 214, 222, 235, 235, 244, 233, 236, 220});
    public static final int[] ROOK_O = Utils.reverseSpecial(new int[]{419, 425, 445, 464, 456, 466, 421, 439, 423, 402, 432, 435, 451, 445, 438, 441, 392, 390, 397, 405, 410, 401, 400, 407, 372, 366, 373, 392, 391, 390, 384, 386, 360, 361, 372, 392, 376, 371, 366, 368, 363, 366, 375, 370, 376, 368, 379, 355, 358, 358, 376, 376, 371, 382, 372, 359, 362, 370, 384, 384, 391, 379, 376, 363});
    public static final int[] QUEEN_O = Utils.reverseSpecial(new int[]{821, 824, 859, 855, 847, 862, 803, 784, 784, 788, 782, 788, 792, 784, 770, 786, 776, 787, 790, 800, 806, 802, 787, 796, 774, 784, 791, 798, 787, 766, 783, 777, 774, 778, 776, 781, 772, 775, 775, 775, 770, 780, 771, 767, 770, 766, 776, 780, 758, 767, 779, 771, 772, 778, 777, 767, 753, 753, 760, 764, 757, 758, 751, 743});
    public static final int[] KING_O = Utils.reverseSpecial(new int[]{-16, 15, -3, 10, -6, 65, 1, 16, -11, -16, -25, 17, -21, 60, 123, -36, 2, -59, 46, -23, -53, -26, -24, 0, -17, 37, -27, 6, -77, 3, 32, -6, -44, 12, -2, -22, 0, -24, -18, -50, 11, 13, -39, -28, -64, -39, -2, 1, 42, 25, -8, -55, -52, 29, 55, 46, 61, 46, 27, -53, 13, -8, 62, 53});
    public static final int[] PAWN_DOUBLED_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -63, -59, -61, -59, -39, -39, -12, -31, -30, -19, -3, -8, -4, -10, -2, -9, 0, -1, -11, -11, 0, -5, -7, -19, -7, -8, 0, -1, -2, -13, -3, -12, -9, -8, -6, -13, -10, 0, -7, -3, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_ISOLATED_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, -10, -15, -3, -7, -2, -18, -8, -13, -2, -12, -15, -1, -13, -12, -8, 0, -4, -8, -15, -10, -9, -9, -2, -11, -11, -10, -14, -7, -19, -8, -13, -22, -3, -8, -13, -19, -3, -7, -8, -10, -1, -24, -1, -8, -14, -10, -1, -4, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_BACKWARD_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4, -2, -9, -12, -7, -4, -9, -3, -22, -29, -31, -13, -8, -15, -6, -31, -18, -7, -17, -4, -8, -11, -3, -3, -7, -3, 0, -2, 0, -2, -8, -1, -2, -2, -5, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_CANNOTBS_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -1, 0, 0, 0, -8, 0, 0, -1, -5, -5, -6, 0, -20, -5, -1, -14, -4, -7, -9, -7, -1, 0, -3, -2, 0, 0, -2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_SUPPORTED_O = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 60, 64, 56, 63, 61, 62, 57, 62, 0, 11, 28, 42, 7, 15, 29, 19, 4, 5, 1, 2, 2, 11, 7, 6, 0, 6, 21, 12, 12, 12, 10, 2, 14, 6, 9, 4, 5, 1, 24, 13, 0, 1, 5, 0, 0, 0, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 139, 121, 124, 133, 137, 125, 123, 133, 122, 119, 136, 121, 111, 120, 135, 136, 126, 118, 118, 113, 113, 119, 103, 117, 118, 105, 119, 126, 118, 130, 106, 109, 135, 129, 110, 136, 112, 144, 116, 108, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_PASSED_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 249, 236, 236, 249, 249, 238, 242, 249, 247, 244, 242, 230, 225, 217, 231, 250, 223, 205, 227, 168, 189, 173, 196, 197, 161, 141, 156, 151, 143, 161, 130, 143, 132, 123, 116, 125, 129, 102, 110, 121, 139, 131, 131, 141, 109, 132, 121, 152, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] BISHOP_E = Utils.reverseSpecial(new int[]{405, 398, 407, 389, 410, 394, 402, 382, 403, 386, 389, 397, 391, 409, 406, 417, 404, 407, 414, 413, 405, 408, 422, 413, 390, 425, 416, 435, 429, 423, 411, 405, 399, 426, 446, 421, 422, 426, 402, 392, 419, 424, 421, 427, 428, 415, 408, 402, 414, 403, 418, 421, 408, 414, 412, 407, 396, 416, 398, 397, 399, 427, 388, 403});
    public static final int[] KNIGHT_E = Utils.reverseSpecial(new int[]{351, 375, 381, 381, 370, 368, 378, 374, 357, 369, 381, 386, 398, 383, 362, 367, 365, 376, 399, 425, 399, 402, 390, 392, 374, 411, 393, 428, 434, 398, 407, 387, 370, 386, 409, 395, 411, 402, 420, 376, 358, 366, 386, 402, 393, 384, 356, 350, 346, 385, 373, 390, 380, 362, 369, 360, 323, 351, 365, 386, 366, 371, 343, 320});
    public static final int[] ROOK_E = Utils.reverseSpecial(new int[]{664, 677, 645, 654, 646, 674, 666, 664, 678, 721, 680, 697, 699, 683, 671, 693, 658, 682, 678, 672, 673, 683, 670, 670, 648, 659, 660, 665, 655, 658, 652, 665, 643, 642, 653, 666, 662, 671, 644, 655, 663, 652, 659, 656, 659, 665, 645, 644, 663, 673, 670, 660, 662, 659, 644, 660, 631, 643, 655, 660, 652, 666, 666, 646});
    public static final int[] QUEEN_E = Utils.reverseSpecial(new int[]{1221, 1244, 1238, 1269, 1277, 1265, 1279, 1301, 1255, 1292, 1295, 1299, 1322, 1345, 1323, 1284, 1234, 1261, 1277, 1291, 1301, 1309, 1280, 1283, 1222, 1244, 1272, 1282, 1308, 1302, 1285, 1267, 1216, 1237, 1261, 1280, 1283, 1256, 1259, 1246, 1220, 1217, 1245, 1252, 1247, 1250, 1231, 1237, 1210, 1247, 1213, 1230, 1225, 1225, 1229, 1222, 1207, 1231, 1226, 1217, 1226, 1223, 1211, 1217});
    public static final int[] KING_E = Utils.reverseSpecial(new int[]{119, 88, 33, 9, 34, 3, -56, 17, 41, 67, 47, 36, -2, 27, 19, 36, 27, 60, 63, 26, 39, 26, 56, 22, 8, 30, 7, 0, 13, 3, 4, 28, -9, 8, 3, 19, -2, -5, 26, -2, -21, -2, 6, 15, 4, -6, -13, -45, -68, -38, -26, 22, -6, -27, -25, -63, -100, -70, -61, -5, -68, -35, -63, -83});
    public static final int[] PAWN_DOUBLED_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -3, -37, -64, -61, -28, -33, -63, -53, -16, -17, -57, -24, -35, -56, -35, -47, 0, -13, -29, -3, -16, -9, -22, -13, -14, -8, -8, -4, -3, -1, -24, -8, -12, -9, -3, -13, -17, -15, -15, -13, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_ISOLATED_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, -9, -5, -32, 0, -17, -1, 0, 0, -10, -6, -11, -7, -29, -28, 0, -16, -45, -35, -27, -16, -21, -34, 0, -16, -25, -14, -5, -4, -16, -23, -14, -25, -22, -30, -16, -8, -3, -8, -21, -15, -19, -16, -5, -18, -4, -4, -8, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_BACKWARD_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -7, -1, -20, -12, -3, 0, -9, -6, -27, -2, -24, -19, -6, -1, -2, -26, -8, -3, -25, 0, -2, -16, -1, -3, -7, -6, -5, -15, -2, 0, -6, -12, -1, -3, -5, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_CANNOTBS_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -4, 0, 0, 0, 0, 0, 0, -6, -2, -2, -2, 0, -18, -20, -12, -6, -20, -14, -5, -1, -7, 0, -3, -10, -1, 0, -4, -6, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});
    public static final int[] PAWN_SUPPORTED_E = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 62, 64, 51, 63, 63, 62, 45, 63, 50, 48, 63, 41, 34, 27, 39, 52, 0, 16, 13, 21, 12, 17, 19, 11, 0, 4, 14, 5, 4, 2, 15, 3, 12, 3, 9, 2, 2, 1, 1, 14, 9, 7, 0, 1, 3, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0});

    public static final int getMoveScores_o(int move) {
        int type = MoveInt.getFigureType(move);
        int from = MoveInt.getFromFieldID(move);
        int to = MoveInt.getToFieldID(move);
        if (!MoveInt.isWhite(move)) {
            from = HORIZONTAL_SYMMETRY[from];
            to = HORIZONTAL_SYMMETRY[to];
        }
        int[] pst = PSTConstants_RI.getArray_o(type);
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
        int[] pst = PSTConstants_RI.getArray_e(type);
        return pst[to] - pst[from];
    }

    public static final int getPieceScores_o(int field, int type) {
        int[] pst = PSTConstants_RI.getArray_o(type);
        return pst[field];
    }

    public static final int getPieceScores_e(int field, int type) {
        int[] pst = PSTConstants_RI.getArray_e(type);
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

