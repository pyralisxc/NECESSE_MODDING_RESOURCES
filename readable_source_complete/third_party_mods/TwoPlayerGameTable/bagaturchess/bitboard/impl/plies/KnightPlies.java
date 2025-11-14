/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;

public class KnightPlies
extends Fields {
    public static final long ALL_KNIGHT_MOVES_FROM_A1 = 0x20400000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B1 = 0x10A00000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_C1 = 0x88500000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_D1 = 19184278881435648L;
    public static final long ALL_KNIGHT_MOVES_FROM_E1 = 9592139440717824L;
    public static final long ALL_KNIGHT_MOVES_FROM_F1 = 0x110A0000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_G1 = 0x8050000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_H1 = 0x4020000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_A2 = 0x2000204000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B2 = 0x100010A000000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_C2 = -8646761407372591104L;
    public static final long ALL_KNIGHT_MOVES_FROM_D2 = 4899991333168480256L;
    public static final long ALL_KNIGHT_MOVES_FROM_E2 = 2449995666584240128L;
    public static final long ALL_KNIGHT_MOVES_FROM_F2 = 0x1100110A00000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_G2 = 0x800080500000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_H2 = 0x400040200000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_A3 = 0x4020002040000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B3 = -6913025356609880064L;
    public static final long ALL_KNIGHT_MOVES_FROM_C3 = 0x5088008850000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_D3 = 2901444352662306816L;
    public static final long ALL_KNIGHT_MOVES_FROM_E3 = 1450722176331153408L;
    public static final long ALL_KNIGHT_MOVES_FROM_F3 = 0xA1100110A000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_G3 = 0x508000805000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_H3 = 0x204000402000000L;
    public static final long ALL_KNIGHT_MOVES_FROM_A4 = 0x40200020400000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B4 = 0xA0100010A00000L;
    public static final long ALL_KNIGHT_MOVES_FROM_C4 = 0x50880088500000L;
    public static final long ALL_KNIGHT_MOVES_FROM_D4 = 11333767002587136L;
    public static final long ALL_KNIGHT_MOVES_FROM_E4 = 5666883501293568L;
    public static final long ALL_KNIGHT_MOVES_FROM_F4 = 0xA1100110A0000L;
    public static final long ALL_KNIGHT_MOVES_FROM_G4 = 0x5080008050000L;
    public static final long ALL_KNIGHT_MOVES_FROM_H4 = 0x2040004020000L;
    public static final long ALL_KNIGHT_MOVES_FROM_A5 = 0x402000204000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B5 = 0xA0100010A000L;
    public static final long ALL_KNIGHT_MOVES_FROM_C5 = 0x508800885000L;
    public static final long ALL_KNIGHT_MOVES_FROM_D5 = 44272527353856L;
    public static final long ALL_KNIGHT_MOVES_FROM_E5 = 22136263676928L;
    public static final long ALL_KNIGHT_MOVES_FROM_F5 = 0xA1100110A00L;
    public static final long ALL_KNIGHT_MOVES_FROM_G5 = 0x50800080500L;
    public static final long ALL_KNIGHT_MOVES_FROM_H5 = 0x20400040200L;
    public static final long ALL_KNIGHT_MOVES_FROM_A6 = 0x4020002040L;
    public static final long ALL_KNIGHT_MOVES_FROM_B6 = 0xA0100010A0L;
    public static final long ALL_KNIGHT_MOVES_FROM_C6 = 0x5088008850L;
    public static final long ALL_KNIGHT_MOVES_FROM_D6 = 172939559976L;
    public static final long ALL_KNIGHT_MOVES_FROM_E6 = 86469779988L;
    public static final long ALL_KNIGHT_MOVES_FROM_F6 = 0xA1100110AL;
    public static final long ALL_KNIGHT_MOVES_FROM_G6 = 0x508000805L;
    public static final long ALL_KNIGHT_MOVES_FROM_H6 = 0x204000402L;
    public static final long ALL_KNIGHT_MOVES_FROM_A7 = 0x40200020L;
    public static final long ALL_KNIGHT_MOVES_FROM_B7 = 0xA0100010L;
    public static final long ALL_KNIGHT_MOVES_FROM_C7 = 0x50880088L;
    public static final long ALL_KNIGHT_MOVES_FROM_D7 = 675545156L;
    public static final long ALL_KNIGHT_MOVES_FROM_E7 = 337772578L;
    public static final long ALL_KNIGHT_MOVES_FROM_F7 = 0xA110011L;
    public static final long ALL_KNIGHT_MOVES_FROM_G7 = 0x5080008L;
    public static final long ALL_KNIGHT_MOVES_FROM_H7 = 0x2040004L;
    public static final long ALL_KNIGHT_MOVES_FROM_A8 = 0x402000L;
    public static final long ALL_KNIGHT_MOVES_FROM_B8 = 0xA01000L;
    public static final long ALL_KNIGHT_MOVES_FROM_C8 = 0x508800L;
    public static final long ALL_KNIGHT_MOVES_FROM_D8 = 2638848L;
    public static final long ALL_KNIGHT_MOVES_FROM_E8 = 1319424L;
    public static final long ALL_KNIGHT_MOVES_FROM_F8 = 659712L;
    public static final long ALL_KNIGHT_MOVES_FROM_G8 = 329728L;
    public static final long ALL_KNIGHT_MOVES_FROM_H8 = 132096L;
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{{0x400000000000L}, {0x20000000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1 = new int[]{0, 1};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{{0x200000000000L}, {0x10000000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0], {0x800000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1 = new int[]{0, 1, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{{0x100000000000L}, {0x8000000000000L}, new long[0], new long[0], new long[0], new long[0], {0x80000000000000L}, {0x400000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1 = new int[]{0, 1, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{{0x80000000000L}, {0x4000000000000L}, new long[0], new long[0], new long[0], new long[0], {0x40000000000000L}, {0x200000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1 = new int[]{0, 1, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{{0x40000000000L}, {0x2000000000000L}, new long[0], new long[0], new long[0], new long[0], {0x20000000000000L}, {0x100000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1 = new int[]{0, 1, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{{0x20000000000L}, {0x1000000000000L}, new long[0], new long[0], new long[0], new long[0], {0x10000000000000L}, {0x80000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1 = new int[]{0, 1, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{{0x10000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0], {0x8000000000000L}, {0x40000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1 = new int[]{0, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{new long[0], new long[0], new long[0], new long[0], new long[0], new long[0], {0x4000000000000L}, {0x20000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1 = new int[]{6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{{0x4000000000L}, {0x200000000000L}, {0x2000000000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2 = new int[]{0, 1, 2};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{0x2000000000L}, {0x100000000000L}, {0x1000000000000000L}, new long[0], new long[0], new long[0], new long[0], {0x8000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2 = new int[]{0, 1, 2, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x1000000000L}, {0x80000000000L}, {0x800000000000000L}, new long[0], new long[0], {Long.MIN_VALUE}, {0x800000000000L}, {0x4000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2 = new int[]{0, 1, 2, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x800000000L}, {0x40000000000L}, {0x400000000000000L}, new long[0], new long[0], {0x4000000000000000L}, {0x400000000000L}, {0x2000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2 = new int[]{0, 1, 2, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x400000000L}, {0x20000000000L}, {0x200000000000000L}, new long[0], new long[0], {0x2000000000000000L}, {0x200000000000L}, {0x1000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2 = new int[]{0, 1, 2, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x200000000L}, {0x10000000000L}, {0x100000000000000L}, new long[0], new long[0], {0x1000000000000000L}, {0x100000000000L}, {0x800000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2 = new int[]{0, 1, 2, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x100000000L}, new long[0], new long[0], new long[0], new long[0], {0x800000000000000L}, {0x80000000000L}, {0x400000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2 = new int[]{0, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{new long[0], new long[0], new long[0], new long[0], new long[0], {0x400000000000000L}, {0x40000000000L}, {0x200000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2 = new int[]{5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{{0x40000000L}, {0x2000000000L}, {0x20000000000000L}, {0x4000000000000000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3 = new int[]{0, 1, 2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x20000000L}, {0x1000000000L}, {0x10000000000000L}, {0x2000000000000000L}, {Long.MIN_VALUE}, new long[0], new long[0], {0x80000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3 = new int[]{0, 1, 2, 3, 4, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x10000000L}, {0x800000000L}, {0x8000000000000L}, {0x1000000000000000L}, {0x4000000000000000L}, {0x80000000000000L}, {0x8000000000L}, {0x40000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x8000000L}, {0x400000000L}, {0x4000000000000L}, {0x800000000000000L}, {0x2000000000000000L}, {0x40000000000000L}, {0x4000000000L}, {0x20000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x4000000L}, {0x200000000L}, {0x2000000000000L}, {0x400000000000000L}, {0x1000000000000000L}, {0x20000000000000L}, {0x2000000000L}, {0x10000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x2000000L}, {0x100000000L}, {0x1000000000000L}, {0x200000000000000L}, {0x800000000000000L}, {0x10000000000000L}, {0x1000000000L}, {0x8000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x1000000L}, new long[0], new long[0], {0x100000000000000L}, {0x400000000000000L}, {0x8000000000000L}, {0x800000000L}, {0x4000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3 = new int[]{0, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{new long[0], new long[0], new long[0], new long[0], {0x200000000000000L}, {0x4000000000000L}, {0x400000000L}, {0x2000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3 = new int[]{4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{{0x400000L}, {0x20000000L}, {0x200000000000L}, {0x40000000000000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4 = new int[]{0, 1, 2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x200000L}, {0x10000000L}, {0x100000000000L}, {0x20000000000000L}, {0x80000000000000L}, new long[0], new long[0], {0x800000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4 = new int[]{0, 1, 2, 3, 4, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x100000L}, {0x8000000L}, {0x80000000000L}, {0x10000000000000L}, {0x40000000000000L}, {0x800000000000L}, {0x80000000L}, {0x400000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{524288L}, {0x4000000L}, {0x40000000000L}, {0x8000000000000L}, {0x20000000000000L}, {0x400000000000L}, {0x40000000L}, {0x200000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{262144L}, {0x2000000L}, {0x20000000000L}, {0x4000000000000L}, {0x10000000000000L}, {0x200000000000L}, {0x20000000L}, {0x100000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{131072L}, {0x1000000L}, {0x10000000000L}, {0x2000000000000L}, {0x8000000000000L}, {0x100000000000L}, {0x10000000L}, {524288L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{65536L}, new long[0], new long[0], {0x1000000000000L}, {0x4000000000000L}, {0x80000000000L}, {0x8000000L}, {262144L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4 = new int[]{0, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{new long[0], new long[0], new long[0], new long[0], {0x2000000000000L}, {0x40000000000L}, {0x4000000L}, {131072L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4 = new int[]{4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{{16384L}, {0x200000L}, {0x2000000000L}, {0x400000000000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5 = new int[]{0, 1, 2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{8192L}, {0x100000L}, {0x1000000000L}, {0x200000000000L}, {0x800000000000L}, new long[0], new long[0], {32768L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5 = new int[]{0, 1, 2, 3, 4, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{4096L}, {524288L}, {0x800000000L}, {0x100000000000L}, {0x400000000000L}, {0x8000000000L}, {0x800000L}, {16384L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{2048L}, {262144L}, {0x400000000L}, {0x80000000000L}, {0x200000000000L}, {0x4000000000L}, {0x400000L}, {8192L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{1024L}, {131072L}, {0x200000000L}, {0x40000000000L}, {0x100000000000L}, {0x2000000000L}, {0x200000L}, {4096L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{512L}, {65536L}, {0x100000000L}, {0x20000000000L}, {0x80000000000L}, {0x1000000000L}, {0x100000L}, {2048L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{256L}, new long[0], new long[0], {0x10000000000L}, {0x40000000000L}, {0x800000000L}, {524288L}, {1024L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5 = new int[]{0, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{new long[0], new long[0], new long[0], new long[0], {0x20000000000L}, {0x400000000L}, {262144L}, {512L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5 = new int[]{4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{{64L}, {8192L}, {0x20000000L}, {0x4000000000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6 = new int[]{0, 1, 2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{32L}, {4096L}, {0x10000000L}, {0x2000000000L}, {0x8000000000L}, new long[0], new long[0], {128L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6 = new int[]{0, 1, 2, 3, 4, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{16L}, {2048L}, {0x8000000L}, {0x1000000000L}, {0x4000000000L}, {0x80000000L}, {32768L}, {64L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{8L}, {1024L}, {0x4000000L}, {0x800000000L}, {0x2000000000L}, {0x40000000L}, {16384L}, {32L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{4L}, {512L}, {0x2000000L}, {0x400000000L}, {0x1000000000L}, {0x20000000L}, {8192L}, {16L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{2L}, {256L}, {0x1000000L}, {0x200000000L}, {0x800000000L}, {0x10000000L}, {4096L}, {8L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{1L}, new long[0], new long[0], {0x100000000L}, {0x400000000L}, {0x8000000L}, {2048L}, {4L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6 = new int[]{0, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{new long[0], new long[0], new long[0], new long[0], {0x200000000L}, {0x4000000L}, {1024L}, {2L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6 = new int[]{4, 5, 6, 7};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{new long[0], {32L}, {0x200000L}, {0x40000000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7 = new int[]{1, 2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{new long[0], {16L}, {0x100000L}, {0x20000000L}, {0x80000000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7 = new int[]{1, 2, 3, 4};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{new long[0], {8L}, {524288L}, {0x10000000L}, {0x40000000L}, {0x800000L}, {128L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7 = new int[]{1, 2, 3, 4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{new long[0], {4L}, {262144L}, {0x8000000L}, {0x20000000L}, {0x400000L}, {64L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7 = new int[]{1, 2, 3, 4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{new long[0], {2L}, {131072L}, {0x4000000L}, {0x10000000L}, {0x200000L}, {32L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7 = new int[]{1, 2, 3, 4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{new long[0], {1L}, {65536L}, {0x2000000L}, {0x8000000L}, {0x100000L}, {16L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7 = new int[]{1, 2, 3, 4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{new long[0], new long[0], new long[0], {0x1000000L}, {0x4000000L}, {524288L}, {8L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7 = new int[]{3, 4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{new long[0], new long[0], new long[0], new long[0], {0x2000000L}, {262144L}, {4L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7 = new int[]{4, 5, 6};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], new long[0], {8192L}, {0x400000L}, new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8 = new int[]{2, 3};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], new long[0], {4096L}, {0x200000L}, {0x800000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8 = new int[]{2, 3, 4};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], new long[0], {2048L}, {0x100000L}, {0x400000L}, {32768L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8 = new int[]{2, 3, 4, 5};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], new long[0], {1024L}, {524288L}, {0x200000L}, {16384L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8 = new int[]{2, 3, 4, 5};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], new long[0], {512L}, {262144L}, {0x100000L}, {8192L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8 = new int[]{2, 3, 4, 5};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], new long[0], {256L}, {131072L}, {524288L}, {4096L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8 = new int[]{2, 3, 4, 5};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], new long[0], new long[0], {65536L}, {262144L}, {2048L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8 = new int[]{3, 4, 5};
    public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0], new long[0], new long[0], {131072L}, {1024L}, new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8 = new int[]{4, 5};
    public static final long[] ALL_ORDERED_KNIGHT_MOVES = new long[]{0x20400000000000L, 0x10A00000000000L, 0x88500000000000L, 19184278881435648L, 9592139440717824L, 0x110A0000000000L, 0x8050000000000L, 0x4020000000000L, 0x2000204000000000L, 0x100010A000000000L, -8646761407372591104L, 4899991333168480256L, 2449995666584240128L, 0x1100110A00000000L, 0x800080500000000L, 0x400040200000000L, 0x4020002040000000L, -6913025356609880064L, 0x5088008850000000L, 2901444352662306816L, 1450722176331153408L, 0xA1100110A000000L, 0x508000805000000L, 0x204000402000000L, 0x40200020400000L, 0xA0100010A00000L, 0x50880088500000L, 11333767002587136L, 5666883501293568L, 0xA1100110A0000L, 0x5080008050000L, 0x2040004020000L, 0x402000204000L, 0xA0100010A000L, 0x508800885000L, 44272527353856L, 22136263676928L, 0xA1100110A00L, 0x50800080500L, 0x20400040200L, 0x4020002040L, 0xA0100010A0L, 0x5088008850L, 172939559976L, 86469779988L, 0xA1100110AL, 0x508000805L, 0x204000402L, 0x40200020L, 0xA0100010L, 0x50880088L, 675545156L, 337772578L, 0xA110011L, 0x5080008L, 0x2040004L, 0x402000L, 0xA01000L, 0x508800L, 2638848L, 1319424L, 659712L, 329728L, 132096L};
    public static final long[][][] ALL_ORDERED_KNIGHT_DIRS = new long[][][]{ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_H8};
    public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][]{ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8};
    public static final long[] ALL_KNIGHT_MOVES = new long[64];
    public static final int[][] ALL_KNIGHT_VALID_DIRS = new int[64][];
    public static final int[][][] ALL_KNIGHT_DIRS_WITH_FIELD_IDS = new int[64][][];
    public static final long[][][] ALL_KNIGHT_DIRS_WITH_BITBOARDS = new long[64][][];
    public static final int[] W_MAGIC = Utils.reverseSpecial(new int[]{0, 10, 20, 20, 20, 10, 10, 0, 10, 24, 26, 26, 26, 24, 24, 10, 10, 28, 40, 50, 50, 28, 28, 10, 10, 23, 36, 40, 40, 23, 23, 10, 10, 22, 28, 30, 30, 22, 22, 10, 0, 26, 26, 30, 30, 26, 26, 10, 5, 20, 20, 23, 20, 20, 20, 5, 0, 5, 15, 15, 15, 15, 0, 0});
    public static final int[] B_MAGIC = Utils.reverseSpecial(new int[]{0, 5, 15, 15, 15, 15, 0, 0, 5, 20, 20, 23, 20, 20, 20, 5, 0, 26, 26, 30, 30, 26, 26, 10, 0, 22, 28, 30, 30, 22, 22, 10, 0, 23, 36, 40, 40, 23, 23, 10, 0, 28, 40, 50, 50, 28, 28, 10, 0, 24, 26, 26, 26, 24, 24, 10, 0, 10, 20, 20, 20, 10, 10, 0});

    public static int getMagic(int colour, int fieldID) {
        if (colour == 0) {
            return W_MAGIC[fieldID];
        }
        return B_MAGIC[fieldID];
    }

    public static int getMagic(int colour, int fromID, int toID) {
        if (colour == 0) {
            return -W_MAGIC[fromID] + W_MAGIC[toID];
        }
        return -B_MAGIC[fromID] + B_MAGIC[toID];
    }

    private static final void verify() {
        for (int i = 0; i < 64; ++i) {
            int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
            long moves = ALL_KNIGHT_MOVES[field_normalized_id];
            String result = "Field[" + i + ": " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
            int j = Bits.nextSetBit_L2R(0, moves);
            while (j <= 63 && j != -1) {
                result = result + Fields.ALL_ORDERED_NAMES[j] + " ";
                j = Bits.nextSetBit_L2R(j + 1, moves);
            }
        }
    }

    public static void main(String[] args) {
        KnightPlies.genMembers();
    }

    private static void genMembers() {
        String prefix;
        int letter;
        int digit;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                prefix = "public static final long ALL_KNIGHT_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                if (letter + 2 <= 7) {
                    if (digit + 1 <= 7) {
                        result = (String)result + letters[letter + 2] + digits[digit + 1] + " | ";
                    }
                    if (digit - 1 >= 0) {
                        result = (String)result + letters[letter + 2] + digits[digit - 1] + " | ";
                    }
                }
                if (letter - 2 >= 0) {
                    if (digit + 1 <= 7) {
                        result = (String)result + letters[letter - 2] + digits[digit + 1] + " | ";
                    }
                    if (digit - 1 >= 0) {
                        result = (String)result + letters[letter - 2] + digits[digit - 1] + " | ";
                    }
                }
                if (letter + 1 <= 7) {
                    if (digit + 2 <= 7) {
                        result = (String)result + letters[letter + 1] + digits[digit + 2] + " | ";
                    }
                    if (digit - 2 >= 0) {
                        result = (String)result + letters[letter + 1] + digits[digit - 2] + " | ";
                    }
                }
                if (letter - 1 >= 0) {
                    if (digit + 2 <= 7) {
                        result = (String)result + letters[letter - 1] + digits[digit + 2] + " | ";
                    }
                    if (digit - 2 >= 0) {
                        result = (String)result + letters[letter - 1] + digits[digit - 2] + " | ";
                    }
                }
                if (((String)result).endsWith(" | ")) {
                    result = ((String)result).substring(0, ((String)result).length() - 3);
                }
                result = (String)result + ";";
                System.out.println((String)result);
            }
        }
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                prefix = "public static final long[][] ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix1 = "public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                result = prefix;
                Object result1 = prefix1;
                if (letter + 1 <= 7 && digit + 2 <= 7) {
                    result = (String)result + "{" + letters[letter + 1] + digits[digit + 2] + "}, ";
                    result1 = (String)result1 + "0, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 2 <= 7 && digit + 1 <= 7) {
                    result = (String)result + "{" + letters[letter + 2] + digits[digit + 1] + "}, ";
                    result1 = (String)result1 + "1, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 2 <= 7 && digit - 1 >= 0) {
                    result = (String)result + "{" + letters[letter + 2] + digits[digit - 1] + "}, ";
                    result1 = (String)result1 + "2, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 1 <= 7 && digit - 2 >= 0) {
                    result = (String)result + "{" + letters[letter + 1] + digits[digit - 2] + "}, ";
                    result1 = (String)result1 + "3, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 1 >= 0 && digit - 2 >= 0) {
                    result = (String)result + "{" + letters[letter - 1] + digits[digit - 2] + "}, ";
                    result1 = (String)result1 + "4, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 2 >= 0 && digit - 1 >= 0) {
                    result = (String)result + "{" + letters[letter - 2] + digits[digit - 1] + "}, ";
                    result1 = (String)result1 + "5, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 2 >= 0 && digit + 1 <= 7) {
                    result = (String)result + "{" + letters[letter - 2] + digits[digit + 1] + "}, ";
                    result1 = (String)result1 + "6, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 1 >= 0 && digit + 2 <= 7) {
                    result = (String)result + "{" + letters[letter - 1] + digits[digit + 2] + "}, ";
                    result1 = (String)result1 + "7, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (((String)result).endsWith(", ")) {
                    result = ((String)result).substring(0, ((String)result).length() - 2);
                }
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                result = (String)result + "};";
                result1 = (String)result1 + "};";
                System.out.println((String)result);
                System.out.println((String)result1);
            }
        }
        result = "public static final long[] ALL_ORDERED_KNIGHT_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_KNIGHT_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_KNIGHT_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_KNIGHT_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
    }

    static {
        for (int i = 0; i < ALL_ORDERED_KNIGHT_MOVES.length; ++i) {
            int idx = Fields.IDX_ORDERED_2_A1H1[i];
            long fieldMoves = ALL_ORDERED_KNIGHT_MOVES[i];
            long[][] dirs = ALL_ORDERED_KNIGHT_DIRS[i];
            KnightPlies.ALL_KNIGHT_MOVES[idx] = fieldMoves;
            KnightPlies.ALL_KNIGHT_VALID_DIRS[idx] = ALL_ORDERED_KNIGHT_VALID_DIRS[i];
            KnightPlies.ALL_KNIGHT_DIRS_WITH_BITBOARDS[idx] = dirs;
            KnightPlies.ALL_KNIGHT_DIRS_WITH_FIELD_IDS[idx] = KnightPlies.bitboards2fieldIDs(dirs);
        }
        KnightPlies.verify();
    }
}

