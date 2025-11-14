/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1.internal;

import bagaturchess.bitboard.impl1.internal.ChessBoard;

public class MaterialUtil {
    public static final int[][] VALUES = new int[][]{{0, 1, 16, 128, 1024, 8192}, {0, 65536, 0x100000, 0x800000, 0x4000000, 0x20000000}};
    public static final int[] SHIFT = new int[]{0, 16};
    private static final int MASK_MINOR_MAJOR_ALL = -983056;
    private static final int MASK_MINOR_MAJOR_WHITE = 65520;
    private static final int MASK_MINOR_MAJOR_BLACK = -1048576;
    private static final int[] MASK_MINOR_MAJOR = new int[]{65520, -1048576};
    private static final int[] MASK_NON_NIGHTS = new int[]{65423, -7405568};
    private static final int MASK_SINGLE_BISHOPS = 0x800080;
    private static final int MASK_SINGLE_BISHOP_NIGHT_WHITE = 144;
    private static final int MASK_SINGLE_BISHOP_NIGHT_BLACK = 0x900000;
    private static final int[] MASK_PAWNS_QUEENS = new int[]{57359, -535887872};
    private static final int[] MASK_SLIDING_PIECES = new int[]{65408, -8388608};
    private static final int[] MASK_MATING_MATERIAL = new int[]{65391, -9502720};

    public static boolean containsMajorPieces(int material) {
        return (material & 0xFFF0FFF0) != 0;
    }

    public static boolean hasNonPawnPieces(int material, int color) {
        return (material & MASK_MINOR_MAJOR[color]) != 0;
    }

    public static boolean hasWhiteNonPawnPieces(int material) {
        return (material & 0xFFF0) != 0;
    }

    public static boolean hasBlackNonPawnPieces(int material) {
        return (material & 0xFFF00000) != 0;
    }

    public static boolean oppositeBishops(int material) {
        return Long.bitCount(material & 0xFFF0FFF0) == 2 && Long.bitCount(material & 0x800080) == 2;
    }

    public static boolean onlyWhitePawnsOrOneNightOrBishop(int material) {
        switch (Long.bitCount(material & 0xFFF0)) {
            case 0: {
                return true;
            }
            case 1: {
                return Long.bitCount(material & 0x90) == 1;
            }
        }
        return false;
    }

    public static boolean onlyBlackPawnsOrOneNightOrBishop(int material) {
        switch (Long.bitCount(material & 0xFFF00000)) {
            case 0: {
                return true;
            }
            case 1: {
                return Long.bitCount(material & 0x900000) == 1;
            }
        }
        return false;
    }

    public static boolean hasPawnsOrQueens(int material, int color) {
        return (material & MASK_PAWNS_QUEENS[color]) != 0;
    }

    public static boolean hasOnlyNights(int material, int color) {
        return (material & MASK_NON_NIGHTS[color]) == 0;
    }

    public static int getMajorPieces(int material, int color) {
        return (material & MASK_MINOR_MAJOR[color]) >>> SHIFT[color];
    }

    public static boolean hasSlidingPieces(int material, int color) {
        return (material & MASK_SLIDING_PIECES[color]) != 0;
    }

    public static boolean isKBNK(int material) {
        return material == 144 || material == 0x900000;
    }

    public static boolean isKRKP(int material) {
        return material == 66560 || material == 0x4000001;
    }

    public static boolean isDrawByMaterial(ChessBoard cb) {
        if (Long.bitCount(cb.allPieces) > 4) {
            return false;
        }
        switch (cb.materialKey) {
            case 0: 
            case 16: 
            case 32: 
            case 128: 
            case 0x100000: 
            case 0x100010: 
            case 0x100080: 
            case 0x200000: 
            case 0x800000: 
            case 0x800010: 
            case 0x800080: {
                return true;
            }
        }
        return false;
    }

    public static boolean hasMatingMaterial(ChessBoard cb, int color) {
        if (Long.bitCount(cb.friendlyPieces[color]) > 3) {
            return true;
        }
        if (Long.bitCount(cb.friendlyPieces[color]) > 2) {
            return !MaterialUtil.hasOnlyNights(cb.materialKey, color);
        }
        return (cb.materialKey & MASK_MATING_MATERIAL[color]) != 0;
    }
}

