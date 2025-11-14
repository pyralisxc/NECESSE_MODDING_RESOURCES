/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl2;

public class Bitboard {
    public static final long H1 = 1L;
    public static final long G1 = 2L;
    public static final long F1 = 4L;
    public static final long E1 = 8L;
    public static final long D1 = 16L;
    public static final long C1 = 32L;
    public static final long B1 = 64L;
    public static final long A1 = 128L;
    public static final long H2 = 256L;
    public static final long G2 = 512L;
    public static final long F2 = 1024L;
    public static final long E2 = 2048L;
    public static final long D2 = 4096L;
    public static final long C2 = 8192L;
    public static final long B2 = 16384L;
    public static final long A2 = 32768L;
    public static final long H3 = 65536L;
    public static final long G3 = 131072L;
    public static final long F3 = 262144L;
    public static final long E3 = 524288L;
    public static final long D3 = 0x100000L;
    public static final long C3 = 0x200000L;
    public static final long B3 = 0x400000L;
    public static final long A3 = 0x800000L;
    public static final long H4 = 0x1000000L;
    public static final long G4 = 0x2000000L;
    public static final long F4 = 0x4000000L;
    public static final long E4 = 0x8000000L;
    public static final long D4 = 0x10000000L;
    public static final long C4 = 0x20000000L;
    public static final long B4 = 0x40000000L;
    public static final long A4 = 0x80000000L;
    public static final long H5 = 0x100000000L;
    public static final long G5 = 0x200000000L;
    public static final long F5 = 0x400000000L;
    public static final long E5 = 0x800000000L;
    public static final long D5 = 0x1000000000L;
    public static final long C5 = 0x2000000000L;
    public static final long B5 = 0x4000000000L;
    public static final long A5 = 0x8000000000L;
    public static final long H6 = 0x10000000000L;
    public static final long G6 = 0x20000000000L;
    public static final long F6 = 0x40000000000L;
    public static final long E6 = 0x80000000000L;
    public static final long D6 = 0x100000000000L;
    public static final long C6 = 0x200000000000L;
    public static final long B6 = 0x400000000000L;
    public static final long A6 = 0x800000000000L;
    public static final long H7 = 0x1000000000000L;
    public static final long G7 = 0x2000000000000L;
    public static final long F7 = 0x4000000000000L;
    public static final long E7 = 0x8000000000000L;
    public static final long D7 = 0x10000000000000L;
    public static final long C7 = 0x20000000000000L;
    public static final long B7 = 0x40000000000000L;
    public static final long A7 = 0x80000000000000L;
    public static final long H8 = 0x100000000000000L;
    public static final long G8 = 0x200000000000000L;
    public static final long F8 = 0x400000000000000L;
    public static final long E8 = 0x800000000000000L;
    public static final long D8 = 0x1000000000000000L;
    public static final long C8 = 0x2000000000000000L;
    public static final long B8 = 0x4000000000000000L;
    public static final long A8 = Long.MIN_VALUE;
    public static final long A1_B1 = 192L;
    public static final long A1_D1 = 144L;
    public static final long B1_C1 = 96L;
    public static final long C1_D1 = 48L;
    public static final long C1_G1 = 34L;
    public static final long D1_F1 = 20L;
    public static final long F1_G1 = 6L;
    public static final long F1_H1 = 5L;
    public static final long F1_H8 = 0x100000000000004L;
    public static final long G1_H1 = 3L;
    public static final long B3_C2 = 0x402000L;
    public static final long G3_F2 = 132096L;
    public static final long D4_E5 = 0x810000000L;
    public static final long E4_D5 = 0x1008000000L;
    public static final long B6_C7 = 0x20400000000000L;
    public static final long G6_F7 = 0x4020000000000L;
    public static final long A8_B8 = -4611686018427387904L;
    public static final long A8_D8 = -8070450532247928832L;
    public static final long B8_C8 = 0x6000000000000000L;
    public static final long C8_G8 = 0x2200000000000000L;
    public static final long D8_F8 = 0x1400000000000000L;
    public static final long F8_G8 = 0x600000000000000L;
    public static final long F8_H8 = 0x500000000000000L;
    public static final long G8_H8 = 0x300000000000000L;
    public static final long A1B1C1 = 224L;
    public static final long B1C1D1 = 112L;
    public static final long A8B8C8 = -2305843009213693952L;
    public static final long B8C8D8 = 0x7000000000000000L;
    public static final long A1B1A2B2 = 49344L;
    public static final long D1E1D2E2 = 6168L;
    public static final long G1H1G2H2 = 771L;
    public static final long D7E7D8E8 = 0x1818000000000000L;
    public static final long A7B7A8B8 = -4557642822898941952L;
    public static final long G7H7G8H8 = 0x303000000000000L;
    public static final long WHITE_SQUARES = -6172840429334713771L;
    public static final long BLACK_SQUARES = 0x55AA55AA55AA55AAL;
    public static final long CORNER_SQUARES = -9151314442816847743L;
    public static final long RANK_1 = 255L;
    public static final long RANK_2 = 65280L;
    public static final long RANK_3 = 0xFF0000L;
    public static final long RANK_4 = 0xFF000000L;
    public static final long RANK_5 = 0xFF00000000L;
    public static final long RANK_6 = 0xFF0000000000L;
    public static final long RANK_7 = 0xFF000000000000L;
    public static final long RANK_8 = -72057594037927936L;
    public static final long RANK_12 = 65535L;
    public static final long RANK_78 = -281474976710656L;
    public static final long RANK_234 = 0xFFFFFF00L;
    public static final long RANK_567 = 0xFFFFFF00000000L;
    public static final long RANK_23456 = 0xFFFFFFFFFF00L;
    public static final long RANK_34567 = 0xFFFFFFFFFF0000L;
    public static final long RANK_234567 = 0xFFFFFFFFFFFF00L;
    public static final long[] RANK_PROMOTION = new long[]{0xFF000000000000L, 65280L};
    public static final long[] RANK_NON_PROMOTION = new long[]{RANK_PROMOTION[0] ^ 0xFFFFFFFFFFFFFFFFL, RANK_PROMOTION[1] ^ 0xFFFFFFFFFFFFFFFFL};
    public static final long[] RANK_FIRST = new long[]{255L, -72057594037927936L};
    public static final long FILE_A = -9187201950435737472L;
    public static final long FILE_B = 0x4040404040404040L;
    public static final long FILE_C = 0x2020202020202020L;
    public static final long FILE_D = 0x1010101010101010L;
    public static final long FILE_E = 0x808080808080808L;
    public static final long FILE_F = 0x404040404040404L;
    public static final long FILE_G = 0x202020202020202L;
    public static final long FILE_H = 0x101010101010101L;
    public static final long FILE_ABC = -2242545357980376864L;
    public static final long FILE_FGH = 0x707070707070707L;
    public static final long FILE_CDEF = 0x3C3C3C3C3C3C3C3CL;
    public static final long NOT_FILE_A = 0x7F7F7F7F7F7F7F7FL;
    public static final long NOT_FILE_H = -72340172838076674L;
    public static final long WHITE_CORNERS = -508659636161278177L;
    public static final long BLACK_CORNERS = 2238015802301280504L;
    public static final long[] RANKS = new long[]{255L, 65280L, 0xFF0000L, 0xFF000000L, 0xFF00000000L, 0xFF0000000000L, 0xFF000000000000L, -72057594037927936L};
    public static final long[] FILES = new long[]{0x101010101010101L, 0x202020202020202L, 0x404040404040404L, 0x808080808080808L, 0x1010101010101010L, 0x2020202020202020L, 0x4040404040404040L, -9187201950435737472L};
    public static final long[] FILES_ADJACENT = new long[]{0x202020202020202L, 0x505050505050505L, 0xA0A0A0A0A0A0A0AL, 0x1414141414141414L, 0x2828282828282828L, 0x5050505050505050L, -6872316419617283936L, 0x4040404040404040L};
    public static final long KING_SIDE = 0x707070707070707L;
    public static final long QUEEN_SIDE = -2242545357980376864L;
    public static final long WHITE_SIDE = 0xFFFFFFFFL;
    public static final long BLACK_SIDE = -4294967296L;
    public static final long WHITE_SPACE_ZONE = 0x3C3C3C00L;
    public static final long BLACK_SPACE_ZONE = 0x3C3C3C00000000L;

    public static final long getWhitePawnAttacks(long pawns) {
        return pawns << 9 & 0xFEFEFEFEFEFEFEFEL | pawns << 7 & 0x7F7F7F7F7F7F7F7FL;
    }

    public static final long getBlackPawnAttacks(long pawns) {
        return pawns >>> 9 & 0x7F7F7F7F7F7F7F7FL | pawns >>> 7 & 0xFEFEFEFEFEFEFEFEL;
    }

    public static final long getPawnNeighbours(long pawns) {
        return pawns << 1 & 0xFEFEFEFEFEFEFEFEL | pawns >>> 1 & 0x7F7F7F7F7F7F7F7FL;
    }

    public static final int manhattanCenterDistance(int sq) {
        int file = sq & 7;
        int rank = sq >>> 3;
        file ^= file - 4 >>> 8;
        rank ^= rank - 4 >>> 8;
        return file + rank & 7;
    }

    public static final long getWhitePassedPawnMask(int index) {
        if (index > 55) {
            return 0L;
        }
        return (FILES[index & 7] | FILES_ADJACENT[index & 7]) << (index >>> 3 << 3) + 8;
    }

    public static final long getBlackPassedPawnMask(int index) {
        if (index < 8) {
            return 0L;
        }
        return (FILES[index & 7] | FILES_ADJACENT[index & 7]) >>> (71 - index >>> 3 << 3);
    }

    public static final long getWhiteAdjacentMask(int index) {
        return Bitboard.getWhitePassedPawnMask(index) & (FILES[index & 7] ^ 0xFFFFFFFFFFFFFFFFL);
    }

    public static final long getBlackAdjacentMask(int index) {
        return Bitboard.getBlackPassedPawnMask(index) & (FILES[index & 7] ^ 0xFFFFFFFFFFFFFFFFL);
    }
}

