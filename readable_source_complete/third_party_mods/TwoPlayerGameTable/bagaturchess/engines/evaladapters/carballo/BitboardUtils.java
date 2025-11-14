/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo;

public class BitboardUtils {
    public static final long b_d = 255L;
    public static final long b_u = -72057594037927936L;
    public static final long b_r = 0x101010101010101L;
    public static final long b_l = -9187201950435737472L;
    public static final long b2_d = 65535L;
    public static final long b2_u = -281474976710656L;
    public static final long b2_r = 0x303030303030303L;
    public static final long b2_l = -4557430888798830400L;
    public static final long A = -9187201950435737472L;
    public static final long B = 0x4040404040404040L;
    public static final long C = 0x2020202020202020L;
    public static final long D = 0x1010101010101010L;
    public static final long E = 0x808080808080808L;
    public static final long F = 0x404040404040404L;
    public static final long G = 0x202020202020202L;
    public static final long H = 0x101010101010101L;
    public static final long[] FILE = new long[]{-9187201950435737472L, 0x4040404040404040L, 0x2020202020202020L, 0x1010101010101010L, 0x808080808080808L, 0x404040404040404L, 0x202020202020202L, 0x101010101010101L};
    public static final long[] FILES_ADJACENT = new long[]{FILE[1], FILE[0] | FILE[2], FILE[1] | FILE[3], FILE[2] | FILE[4], FILE[3] | FILE[5], FILE[4] | FILE[6], FILE[5] | FILE[7], FILE[6]};
    public static final long[] FILES_LEFT = new long[]{0L, FILE[0], FILE[0] | FILE[1], FILE[0] | FILE[1] | FILE[2], FILE[0] | FILE[1] | FILE[2] | FILE[3], FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4], FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5], FILE[0] | FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6]};
    public static final long[] FILES_RIGHT = new long[]{FILE[1] | FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], FILE[2] | FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], FILE[3] | FILE[4] | FILE[5] | FILE[6] | FILE[7], FILE[4] | FILE[5] | FILE[6] | FILE[7], FILE[5] | FILE[6] | FILE[7], FILE[6] | FILE[7], FILE[7], 0L};
    public static final long R1 = 255L;
    public static final long R2 = 65280L;
    public static final long R3 = 0xFF0000L;
    public static final long R4 = 0xFF000000L;
    public static final long R5 = 0xFF00000000L;
    public static final long R6 = 0xFF0000000000L;
    public static final long R7 = 0xFF000000000000L;
    public static final long R8 = -72057594037927936L;
    public static final long[] RANK = new long[]{255L, 65280L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, -72057594037927936L};
    public static final long[] RANKS_UPWARDS = new long[]{RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[5] | RANK[6] | RANK[7], RANK[6] | RANK[7], RANK[7], 0L};
    public static final long[] RANK_AND_UPWARDS = new long[]{RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[4] | RANK[5] | RANK[6] | RANK[7], RANK[5] | RANK[6] | RANK[7], RANK[6] | RANK[7], RANK[7]};
    public static final long[] RANKS_DOWNWARDS = new long[]{0L, RANK[0], RANK[0] | RANK[1], RANK[0] | RANK[1] | RANK[2], RANK[0] | RANK[1] | RANK[2] | RANK[3], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6]};
    public static final long[] RANK_AND_DOWNWARDS = new long[]{RANK[0], RANK[0] | RANK[1], RANK[0] | RANK[1] | RANK[2], RANK[0] | RANK[1] | RANK[2] | RANK[3], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6], RANK[0] | RANK[1] | RANK[2] | RANK[3] | RANK[4] | RANK[5] | RANK[6] | RANK[7]};
    public static final long[][] RANKS_FORWARD = new long[][]{RANKS_UPWARDS, RANKS_DOWNWARDS};
    public static final long[][] RANKS_BACKWARD = new long[][]{RANKS_DOWNWARDS, RANKS_UPWARDS};
    public static final long[][] RANK_AND_FORWARD = new long[][]{RANK_AND_UPWARDS, RANK_AND_DOWNWARDS};
    public static final long[][] RANK_AND_BACKWARD = new long[][]{RANK_AND_DOWNWARDS, RANK_AND_UPWARDS};
    public static final String[] SQUARE_NAMES = BitboardUtils.changeEndianArray64(new String[]{"a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1"});
    public static final byte[] BIT_TABLE = new byte[]{63, 30, 3, 32, 25, 41, 22, 33, 15, 50, 42, 13, 11, 53, 19, 34, 61, 29, 2, 51, 21, 43, 45, 10, 18, 47, 1, 54, 9, 57, 0, 35, 62, 31, 40, 4, 49, 5, 52, 26, 60, 6, 23, 44, 46, 27, 56, 16, 7, 39, 48, 24, 59, 14, 12, 55, 38, 28, 58, 20, 37, 17, 36, 8};

    public static byte square2Index(long square) {
        long b = square ^ square - 1L;
        int fold = (int)(b ^ b >>> 32);
        return BIT_TABLE[fold * 2017106723 >>> 26];
    }

    public static long index2Square(int index) {
        return 1L << index;
    }

    public static String[] changeEndianArray64(String[] sarray) {
        String[] out = new String[64];
        for (int i = 0; i < 64; ++i) {
            out[i] = sarray[63 - i];
        }
        return out;
    }

    public static int[] changeEndianArray64(int[] sarray) {
        int[] out = new int[64];
        for (int i = 0; i < 64; ++i) {
            out[i] = sarray[63 - i];
        }
        return out;
    }

    public static String toString(long b) {
        StringBuilder sb = new StringBuilder();
        for (long i = Long.MIN_VALUE; i != 0L; i >>>= 1) {
            sb.append((b & i) != 0L ? "1 " : "0 ");
            if ((i & 0x101010101010101L) == 0L) continue;
            sb.append("\n");
        }
        return sb.toString();
    }

    public static long flipVertical(long in) {
        long k1 = 0xFF00FF00FF00FFL;
        long k2 = 0xFFFF0000FFFFL;
        in = in >>> 8 & 0xFF00FF00FF00FFL | (in & 0xFF00FF00FF00FFL) << 8;
        in = in >>> 16 & 0xFFFF0000FFFFL | (in & 0xFFFF0000FFFFL) << 16;
        in = in >>> 32 | in << 32;
        return in;
    }

    public static int flipHorizontalIndex(int index) {
        return index & 0xF8 | 7 - (index & 7);
    }

    public static int popCount(long x) {
        if (x == 0L) {
            return 0;
        }
        long k1 = 0x5555555555555555L;
        long k2 = 0x3333333333333333L;
        long k4 = 0xF0F0F0F0F0F0F0FL;
        long kf = 0x101010101010101L;
        x -= x >> 1 & 0x5555555555555555L;
        x = (x & 0x3333333333333333L) + (x >> 2 & 0x3333333333333333L);
        x = x + (x >> 4) & 0xF0F0F0F0F0F0F0FL;
        x = x * 0x101010101010101L >> 56;
        return (int)x;
    }

    public static String square2Algebraic(long square) {
        return SQUARE_NAMES[BitboardUtils.square2Index(square)];
    }

    public static String index2Algebraic(int index) {
        return SQUARE_NAMES[index];
    }

    public static int algebraic2Index(String name) {
        for (int i = 0; i < 64; ++i) {
            if (!name.equals(SQUARE_NAMES[i])) continue;
            return i;
        }
        return -1;
    }

    public static long algebraic2Square(String name) {
        long aux = 1L;
        for (int i = 0; i < 64; ++i) {
            if (name.equals(SQUARE_NAMES[i])) {
                return aux;
            }
            aux <<= 1;
        }
        return 0L;
    }

    public static int getFile(long square) {
        for (int file = 0; file < 8; ++file) {
            if ((FILE[file] & square) == 0L) continue;
            return file;
        }
        return 0;
    }

    public static int getRankLsb(long square) {
        for (int rank = 0; rank <= 7; ++rank) {
            if ((RANK[rank] & square) == 0L) continue;
            return rank;
        }
        return 0;
    }

    public static int getRankMsb(long square) {
        for (int rank = 7; rank >= 0; --rank) {
            if ((RANK[rank] & square) == 0L) continue;
            return rank;
        }
        return 0;
    }

    public static int getFileOfIndex(int index) {
        return 7 - index & 7;
    }

    public static int getRankOfIndex(int index) {
        return index >> 3;
    }

    public static long lsb(long board) {
        return board & -board;
    }

    public static long msb(long board) {
        board |= board >>> 32;
        board |= board >>> 16;
        board |= board >>> 8;
        board |= board >>> 4;
        board |= board >>> 2;
        return (board |= board >>> 1) == 0L ? 0L : (board >>> 1) + 1L;
    }

    public static int distance(int index1, int index2) {
        return Math.max(Math.abs((index1 & 7) - (index2 & 7)), Math.abs((index1 >> 3) - (index2 >> 3)));
    }

    public static long getHorizontalLine(long square1, long square2) {
        return (square1 | square1 - 1L) & (square2 - 1L ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public static boolean isWhiteSquare(long square) {
        return (square & 0xAA55AA55AA55AA55L) != 0L;
    }

    public static boolean isBlackSquare(long square) {
        return (square & 0x55AA55AA55AA55AAL) != 0L;
    }

    public static long getSameColorSquares(long square) {
        return (square & 0xAA55AA55AA55AA55L) != 0L ? -6172840429334713771L : 0x55AA55AA55AA55AAL;
    }

    public static long frontPawnSpan(long pawn, int color) {
        byte index = BitboardUtils.square2Index(pawn);
        int rank = index >> 3;
        int file = 7 - index & 7;
        return RANKS_FORWARD[color][rank] & (FILE[file] | FILES_ADJACENT[file]);
    }

    public static long frontFile(long square, int color) {
        byte index = BitboardUtils.square2Index(square);
        int rank = index >> 3;
        int file = 7 - index & 7;
        return RANKS_FORWARD[color][rank] & FILE[file];
    }

    public static boolean sameRankOrFile(int index1, int index2) {
        return index1 >> 3 == index2 >> 3 || (index1 & 7) == (index2 & 7);
    }
}

