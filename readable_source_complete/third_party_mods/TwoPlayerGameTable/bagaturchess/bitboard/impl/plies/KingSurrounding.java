/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.KingPlies;
import bagaturchess.bitboard.impl.plies.KnightPlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class KingSurrounding
extends Fields {
    public static final long[] SURROUND_LEVEL1;
    public static final long[] SURROUND_LEVEL2;
    public static final int[][] W_BACK;
    public static final int[][] B_BACK;
    public static final int[][] W_FRONT;
    public static final int[][] B_FRONT;
    public static final int[][] W_FFRONT;
    public static final int[][] B_FFRONT;

    private static void add(int[][] arr, int idx, long fieldBoard) {
        int[] fieldIDs = arr[idx];
        int newSize = fieldIDs.length + 1;
        int[] newArr = new int[newSize];
        for (int i = 0; i < fieldIDs.length; ++i) {
            newArr[i] = fieldIDs[i];
        }
        newArr[fieldIDs.length] = KingSurrounding.get67IDByBitboard(fieldBoard);
        arr[idx] = newArr;
    }

    public static void main(String[] args) {
        new KingSurrounding();
    }

    static {
        int id;
        long a1h1;
        int i;
        SURROUND_LEVEL1 = new long[64];
        SURROUND_LEVEL2 = new long[64];
        W_BACK = new int[64][0];
        B_BACK = new int[64][0];
        W_FRONT = new int[64][0];
        B_FRONT = new int[64][0];
        W_FFRONT = new int[64][0];
        B_FFRONT = new int[64][0];
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            a1h1 = ALL_ORDERED_A1H1[i];
            id = KingSurrounding.get67IDByBitboard(a1h1);
            long w_back = 0L;
            if ((a1h1 & 0xFF00000000000000L) == 0L) {
                w_back |= a1h1 << 8;
                KingSurrounding.add(W_BACK, id, a1h1 << 8);
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    w_back |= a1h1 >>> 1;
                    KingSurrounding.add(W_BACK, id, a1h1 >>> 1);
                    w_back |= a1h1 << 7;
                    KingSurrounding.add(W_BACK, id, a1h1 << 7);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    w_back |= a1h1 << 1;
                    KingSurrounding.add(W_BACK, id, a1h1 << 1);
                    w_back |= a1h1 << 9;
                    KingSurrounding.add(W_BACK, id, a1h1 << 9);
                }
            } else {
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    w_back |= a1h1 >>> 1;
                    KingSurrounding.add(W_BACK, id, a1h1 >>> 1);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    w_back |= a1h1 << 1;
                    KingSurrounding.add(W_BACK, id, a1h1 << 1);
                }
            }
            long w_front = 0L;
            if ((a1h1 & 0xFFL) == 0L) {
                w_front |= a1h1 >>> 8;
                KingSurrounding.add(W_FRONT, id, a1h1 >>> 8);
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    w_front |= a1h1 >>> 9;
                    KingSurrounding.add(W_FRONT, id, a1h1 >>> 9);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    w_front |= a1h1 >>> 7;
                    KingSurrounding.add(W_FRONT, id, a1h1 >>> 7);
                }
            }
            long w_ffront = 0L;
            if ((a1h1 & 0xFFL) == 0L && (a1h1 & 0xFF00L) == 0L) {
                w_ffront |= a1h1 >>> 16;
                KingSurrounding.add(W_FFRONT, id, a1h1 >>> 16);
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    w_ffront |= a1h1 >>> 17;
                    KingSurrounding.add(W_FFRONT, id, a1h1 >>> 17);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    w_ffront |= a1h1 >>> 15;
                    KingSurrounding.add(W_FFRONT, id, a1h1 >>> 15);
                }
            }
            long b_back = 0L;
            if ((a1h1 & 0xFFL) == 0L) {
                b_back |= a1h1 >>> 8;
                KingSurrounding.add(B_BACK, id, a1h1 >>> 8);
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    b_back |= a1h1 >>> 1;
                    KingSurrounding.add(B_BACK, id, a1h1 >>> 1);
                    b_back |= a1h1 >>> 9;
                    KingSurrounding.add(B_BACK, id, a1h1 >>> 9);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    b_back |= a1h1 << 1;
                    KingSurrounding.add(B_BACK, id, a1h1 << 1);
                    b_back |= a1h1 >>> 7;
                    KingSurrounding.add(B_BACK, id, a1h1 >>> 7);
                }
            } else {
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    b_back |= a1h1 >>> 1;
                    KingSurrounding.add(B_BACK, id, a1h1 >>> 1);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    b_back |= a1h1 << 1;
                    KingSurrounding.add(B_BACK, id, a1h1 << 1);
                }
            }
            long b_front = 0L;
            if ((a1h1 & 0xFF00000000000000L) == 0L) {
                b_front |= a1h1 << 8;
                KingSurrounding.add(B_FRONT, id, a1h1 << 8);
                if ((a1h1 & 0x101010101010101L) == 0L) {
                    b_front |= a1h1 << 7;
                    KingSurrounding.add(B_FRONT, id, a1h1 << 7);
                }
                if ((a1h1 & 0x8080808080808080L) == 0L) {
                    b_front |= a1h1 << 9;
                    KingSurrounding.add(B_FRONT, id, a1h1 << 9);
                }
            }
            long b_ffront = 0L;
            if ((a1h1 & 0xFF00000000000000L) != 0L || (a1h1 & 0xFF000000000000L) != 0L) continue;
            b_ffront |= a1h1 << 16;
            KingSurrounding.add(B_FFRONT, id, a1h1 << 16);
            if ((a1h1 & 0x101010101010101L) == 0L) {
                b_ffront |= a1h1 << 15;
                KingSurrounding.add(B_FFRONT, id, a1h1 << 15);
            }
            if ((a1h1 & 0x8080808080808080L) != 0L) continue;
            b_ffront |= a1h1 << 17;
            KingSurrounding.add(B_FFRONT, id, a1h1 << 17);
        }
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            a1h1 = ALL_ORDERED_A1H1[i];
            id = KingSurrounding.get67IDByBitboard(a1h1);
            long result = KingPlies.ALL_KING_MOVES[id];
            if ((result & 0x101010101010101L) != 0L & (result & 0x404040404040404L) == 0L) {
                result |= result << 1;
            }
            if ((result & 0x8080808080808080L) != 0L & (result & 0x2020202020202020L) == 0L) {
                result |= result >> 1;
            }
            KingSurrounding.SURROUND_LEVEL1[id] = result;
        }
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            a1h1 = ALL_ORDERED_A1H1[i];
            id = KingSurrounding.get67IDByBitboard(a1h1);
            long knightMoves = KnightPlies.ALL_KNIGHT_MOVES[id];
            long officerMoves = 0L;
            int[] validDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[id];
            long[][] dirs = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[id];
            for (int dirID : validDirIDs) {
                int seq = 1;
                if (dirs[dirID].length <= 1) continue;
                long toBitboard = dirs[dirID][seq];
                officerMoves |= toBitboard;
            }
            long castleMoves = 0L;
            validDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[id];
            dirs = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[id];
            for (int dirID : validDirIDs) {
                int seq = 1;
                if (dirs[dirID].length <= 1) continue;
                long toBitboard = dirs[dirID][seq];
                castleMoves |= toBitboard;
            }
            long result = knightMoves | officerMoves | castleMoves;
            if ((result & 0x101010101010101L) != 0L & (result & 0x808080808080808L) == 0L) {
                result |= result << 1;
            }
            if ((result & 0x8080808080808080L) != 0L & (result & 0x1010101010101010L) == 0L) {
                result |= result >> 1;
            }
            KingSurrounding.SURROUND_LEVEL2[id] = result;
        }
    }
}

