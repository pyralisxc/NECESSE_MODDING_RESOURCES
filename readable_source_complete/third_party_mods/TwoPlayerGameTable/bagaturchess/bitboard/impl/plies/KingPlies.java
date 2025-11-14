/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;

public class KingPlies
extends Fields {
    public static final long ALL_KING_MOVES_FROM_A1 = 0x40C0000000000000L;
    public static final long ALL_KING_MOVES_FROM_B1 = -6854478632857894912L;
    public static final long ALL_KING_MOVES_FROM_C1 = 0x5070000000000000L;
    public static final long ALL_KING_MOVES_FROM_D1 = 2898066360212914176L;
    public static final long ALL_KING_MOVES_FROM_E1 = 1449033180106457088L;
    public static final long ALL_KING_MOVES_FROM_F1 = 0xA0E000000000000L;
    public static final long ALL_KING_MOVES_FROM_G1 = 0x507000000000000L;
    public static final long ALL_KING_MOVES_FROM_H1 = 0x203000000000000L;
    public static final long ALL_KING_MOVES_FROM_A2 = -4593460513685372928L;
    public static final long ALL_KING_MOVES_FROM_B2 = -2260560722335367168L;
    public static final long ALL_KING_MOVES_FROM_C2 = 0x7050700000000000L;
    public static final long ALL_KING_MOVES_FROM_D2 = 4046545837843546112L;
    public static final long ALL_KING_MOVES_FROM_E2 = 2023272918921773056L;
    public static final long ALL_KING_MOVES_FROM_F2 = 0xE0A0E0000000000L;
    public static final long ALL_KING_MOVES_FROM_G2 = 0x705070000000000L;
    public static final long ALL_KING_MOVES_FROM_H2 = 0x302030000000000L;
    public static final long ALL_KING_MOVES_FROM_A3 = 0xC040C000000000L;
    public static final long ALL_KING_MOVES_FROM_B3 = 0xE0A0E000000000L;
    public static final long ALL_KING_MOVES_FROM_C3 = 0x70507000000000L;
    public static final long ALL_KING_MOVES_FROM_D3 = 15806819679076352L;
    public static final long ALL_KING_MOVES_FROM_E3 = 7903409839538176L;
    public static final long ALL_KING_MOVES_FROM_F3 = 0xE0A0E00000000L;
    public static final long ALL_KING_MOVES_FROM_G3 = 0x7050700000000L;
    public static final long ALL_KING_MOVES_FROM_H3 = 0x3020300000000L;
    public static final long ALL_KING_MOVES_FROM_A4 = 0xC040C0000000L;
    public static final long ALL_KING_MOVES_FROM_B4 = 0xE0A0E0000000L;
    public static final long ALL_KING_MOVES_FROM_C4 = 0x705070000000L;
    public static final long ALL_KING_MOVES_FROM_D4 = 61745389371392L;
    public static final long ALL_KING_MOVES_FROM_E4 = 30872694685696L;
    public static final long ALL_KING_MOVES_FROM_F4 = 0xE0A0E000000L;
    public static final long ALL_KING_MOVES_FROM_G4 = 0x70507000000L;
    public static final long ALL_KING_MOVES_FROM_H4 = 0x30203000000L;
    public static final long ALL_KING_MOVES_FROM_A5 = 0xC040C00000L;
    public static final long ALL_KING_MOVES_FROM_B5 = 0xE0A0E00000L;
    public static final long ALL_KING_MOVES_FROM_C5 = 0x7050700000L;
    public static final long ALL_KING_MOVES_FROM_D5 = 241192927232L;
    public static final long ALL_KING_MOVES_FROM_E5 = 120596463616L;
    public static final long ALL_KING_MOVES_FROM_F5 = 0xE0A0E0000L;
    public static final long ALL_KING_MOVES_FROM_G5 = 0x705070000L;
    public static final long ALL_KING_MOVES_FROM_H5 = 0x302030000L;
    public static final long ALL_KING_MOVES_FROM_A6 = 0xC040C000L;
    public static final long ALL_KING_MOVES_FROM_B6 = 0xE0A0E000L;
    public static final long ALL_KING_MOVES_FROM_C6 = 0x70507000L;
    public static final long ALL_KING_MOVES_FROM_D6 = 942159872L;
    public static final long ALL_KING_MOVES_FROM_E6 = 471079936L;
    public static final long ALL_KING_MOVES_FROM_F6 = 0xE0A0E00L;
    public static final long ALL_KING_MOVES_FROM_G6 = 0x7050700L;
    public static final long ALL_KING_MOVES_FROM_H6 = 0x3020300L;
    public static final long ALL_KING_MOVES_FROM_A7 = 0xC040C0L;
    public static final long ALL_KING_MOVES_FROM_B7 = 0xE0A0E0L;
    public static final long ALL_KING_MOVES_FROM_C7 = 0x705070L;
    public static final long ALL_KING_MOVES_FROM_D7 = 0x382838L;
    public static final long ALL_KING_MOVES_FROM_E7 = 0x1C141CL;
    public static final long ALL_KING_MOVES_FROM_F7 = 920078L;
    public static final long ALL_KING_MOVES_FROM_G7 = 460039L;
    public static final long ALL_KING_MOVES_FROM_H7 = 197123L;
    public static final long ALL_KING_MOVES_FROM_A8 = 49216L;
    public static final long ALL_KING_MOVES_FROM_B8 = 57504L;
    public static final long ALL_KING_MOVES_FROM_C8 = 28752L;
    public static final long ALL_KING_MOVES_FROM_D8 = 14376L;
    public static final long ALL_KING_MOVES_FROM_E8 = 7188L;
    public static final long ALL_KING_MOVES_FROM_F8 = 3594L;
    public static final long ALL_KING_MOVES_FROM_G8 = 1797L;
    public static final long ALL_KING_MOVES_FROM_H8 = 770L;
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{{0x80000000000000L}, {0x40000000000000L}, {0x4000000000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1 = new int[]{0, 1, 2};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{{0x40000000000000L}, {0x20000000000000L}, {0x2000000000000000L}, new long[0], new long[0], new long[0], {Long.MIN_VALUE}, {0x80000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{{0x20000000000000L}, {0x10000000000000L}, {0x1000000000000000L}, new long[0], new long[0], new long[0], {0x4000000000000000L}, {0x40000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{{0x10000000000000L}, {0x8000000000000L}, {0x800000000000000L}, new long[0], new long[0], new long[0], {0x2000000000000000L}, {0x20000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{{0x8000000000000L}, {0x4000000000000L}, {0x400000000000000L}, new long[0], new long[0], new long[0], {0x1000000000000000L}, {0x10000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{{0x4000000000000L}, {0x2000000000000L}, {0x200000000000000L}, new long[0], new long[0], new long[0], {0x800000000000000L}, {0x8000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{{0x2000000000000L}, {0x1000000000000L}, {0x100000000000000L}, new long[0], new long[0], new long[0], {0x400000000000000L}, {0x4000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1 = new int[]{0, 1, 2, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{{0x1000000000000L}, new long[0], new long[0], new long[0], new long[0], new long[0], {0x200000000000000L}, {0x2000000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1 = new int[]{0, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{{0x800000000000L}, {0x400000000000L}, {0x40000000000000L}, {0x4000000000000000L}, {Long.MIN_VALUE}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{0x400000000000L}, {0x200000000000L}, {0x20000000000000L}, {0x2000000000000000L}, {0x4000000000000000L}, {Long.MIN_VALUE}, {0x80000000000000L}, {0x800000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x200000000000L}, {0x100000000000L}, {0x10000000000000L}, {0x1000000000000000L}, {0x2000000000000000L}, {0x4000000000000000L}, {0x40000000000000L}, {0x400000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x100000000000L}, {0x80000000000L}, {0x8000000000000L}, {0x800000000000000L}, {0x1000000000000000L}, {0x2000000000000000L}, {0x20000000000000L}, {0x200000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x80000000000L}, {0x40000000000L}, {0x4000000000000L}, {0x400000000000000L}, {0x800000000000000L}, {0x1000000000000000L}, {0x10000000000000L}, {0x100000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x40000000000L}, {0x20000000000L}, {0x2000000000000L}, {0x200000000000000L}, {0x400000000000000L}, {0x800000000000000L}, {0x8000000000000L}, {0x80000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x20000000000L}, {0x10000000000L}, {0x1000000000000L}, {0x100000000000000L}, {0x200000000000000L}, {0x400000000000000L}, {0x4000000000000L}, {0x40000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{{0x10000000000L}, new long[0], new long[0], new long[0], {0x100000000000000L}, {0x200000000000000L}, {0x2000000000000L}, {0x20000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{{0x8000000000L}, {0x4000000000L}, {0x400000000000L}, {0x40000000000000L}, {0x80000000000000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x4000000000L}, {0x2000000000L}, {0x200000000000L}, {0x20000000000000L}, {0x40000000000000L}, {0x80000000000000L}, {0x800000000000L}, {0x8000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x2000000000L}, {0x1000000000L}, {0x100000000000L}, {0x10000000000000L}, {0x20000000000000L}, {0x40000000000000L}, {0x400000000000L}, {0x4000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x1000000000L}, {0x800000000L}, {0x80000000000L}, {0x8000000000000L}, {0x10000000000000L}, {0x20000000000000L}, {0x200000000000L}, {0x2000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x800000000L}, {0x400000000L}, {0x40000000000L}, {0x4000000000000L}, {0x8000000000000L}, {0x10000000000000L}, {0x100000000000L}, {0x1000000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x400000000L}, {0x200000000L}, {0x20000000000L}, {0x2000000000000L}, {0x4000000000000L}, {0x8000000000000L}, {0x80000000000L}, {0x800000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x200000000L}, {0x100000000L}, {0x10000000000L}, {0x1000000000000L}, {0x2000000000000L}, {0x4000000000000L}, {0x40000000000L}, {0x400000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{{0x100000000L}, new long[0], new long[0], new long[0], {0x1000000000000L}, {0x2000000000000L}, {0x20000000000L}, {0x200000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{{0x80000000L}, {0x40000000L}, {0x4000000000L}, {0x400000000000L}, {0x800000000000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x40000000L}, {0x20000000L}, {0x2000000000L}, {0x200000000000L}, {0x400000000000L}, {0x800000000000L}, {0x8000000000L}, {0x80000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x20000000L}, {0x10000000L}, {0x1000000000L}, {0x100000000000L}, {0x200000000000L}, {0x400000000000L}, {0x4000000000L}, {0x40000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{0x10000000L}, {0x8000000L}, {0x800000000L}, {0x80000000000L}, {0x100000000000L}, {0x200000000000L}, {0x2000000000L}, {0x20000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{0x8000000L}, {0x4000000L}, {0x400000000L}, {0x40000000000L}, {0x80000000000L}, {0x100000000000L}, {0x1000000000L}, {0x10000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{0x4000000L}, {0x2000000L}, {0x200000000L}, {0x20000000000L}, {0x40000000000L}, {0x80000000000L}, {0x800000000L}, {0x8000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{0x2000000L}, {0x1000000L}, {0x100000000L}, {0x10000000000L}, {0x20000000000L}, {0x40000000000L}, {0x400000000L}, {0x4000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{{0x1000000L}, new long[0], new long[0], new long[0], {0x10000000000L}, {0x20000000000L}, {0x200000000L}, {0x2000000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{{0x800000L}, {0x400000L}, {0x40000000L}, {0x4000000000L}, {0x8000000000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{0x400000L}, {0x200000L}, {0x20000000L}, {0x2000000000L}, {0x4000000000L}, {0x8000000000L}, {0x80000000L}, {0x800000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{0x200000L}, {0x100000L}, {0x10000000L}, {0x1000000000L}, {0x2000000000L}, {0x4000000000L}, {0x40000000L}, {0x400000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{0x100000L}, {524288L}, {0x8000000L}, {0x800000000L}, {0x1000000000L}, {0x2000000000L}, {0x20000000L}, {0x200000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{524288L}, {262144L}, {0x4000000L}, {0x400000000L}, {0x800000000L}, {0x1000000000L}, {0x10000000L}, {0x100000L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{262144L}, {131072L}, {0x2000000L}, {0x200000000L}, {0x400000000L}, {0x800000000L}, {0x8000000L}, {524288L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{131072L}, {65536L}, {0x1000000L}, {0x100000000L}, {0x200000000L}, {0x400000000L}, {0x4000000L}, {262144L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{{65536L}, new long[0], new long[0], new long[0], {0x100000000L}, {0x200000000L}, {0x2000000L}, {131072L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{{32768L}, {16384L}, {0x400000L}, {0x40000000L}, {0x80000000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{16384L}, {8192L}, {0x200000L}, {0x20000000L}, {0x40000000L}, {0x80000000L}, {0x800000L}, {32768L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{8192L}, {4096L}, {0x100000L}, {0x10000000L}, {0x20000000L}, {0x40000000L}, {0x400000L}, {16384L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{4096L}, {2048L}, {524288L}, {0x8000000L}, {0x10000000L}, {0x20000000L}, {0x200000L}, {8192L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{2048L}, {1024L}, {262144L}, {0x4000000L}, {0x8000000L}, {0x10000000L}, {0x100000L}, {4096L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{1024L}, {512L}, {131072L}, {0x2000000L}, {0x4000000L}, {0x8000000L}, {524288L}, {2048L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{512L}, {256L}, {65536L}, {0x1000000L}, {0x2000000L}, {0x4000000L}, {262144L}, {1024L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{{256L}, new long[0], new long[0], new long[0], {0x1000000L}, {0x2000000L}, {131072L}, {512L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{{128L}, {64L}, {16384L}, {0x400000L}, {0x800000L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7 = new int[]{0, 1, 2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{{64L}, {32L}, {8192L}, {0x200000L}, {0x400000L}, {0x800000L}, {32768L}, {128L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{{32L}, {16L}, {4096L}, {0x100000L}, {0x200000L}, {0x400000L}, {16384L}, {64L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{{16L}, {8L}, {2048L}, {524288L}, {0x100000L}, {0x200000L}, {8192L}, {32L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{{8L}, {4L}, {1024L}, {262144L}, {524288L}, {0x100000L}, {4096L}, {16L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{{4L}, {2L}, {512L}, {131072L}, {262144L}, {524288L}, {2048L}, {8L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{{2L}, {1L}, {256L}, {65536L}, {131072L}, {262144L}, {1024L}, {4L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7 = new int[]{0, 1, 2, 3, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{{1L}, new long[0], new long[0], new long[0], {65536L}, {131072L}, {512L}, {2L}};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7 = new int[]{0, 4, 5, 6, 7};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], new long[0], {64L}, {16384L}, {32768L}, new long[0], new long[0], new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8 = new int[]{2, 3, 4};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], new long[0], {32L}, {8192L}, {16384L}, {32768L}, {128L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], new long[0], {16L}, {4096L}, {8192L}, {16384L}, {64L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], new long[0], {8L}, {2048L}, {4096L}, {8192L}, {32L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], new long[0], {4L}, {1024L}, {2048L}, {4096L}, {16L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], new long[0], {2L}, {512L}, {1024L}, {2048L}, {8L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], new long[0], {1L}, {256L}, {512L}, {1024L}, {4L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8 = new int[]{2, 3, 4, 5, 6};
    public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0], new long[0], new long[0], {256L}, {512L}, {2L}, new long[0]};
    public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8 = new int[]{4, 5, 6};
    public static final long[] ALL_ORDERED_KING_MOVES = new long[]{0x40C0000000000000L, -6854478632857894912L, 0x5070000000000000L, 2898066360212914176L, 1449033180106457088L, 0xA0E000000000000L, 0x507000000000000L, 0x203000000000000L, -4593460513685372928L, -2260560722335367168L, 0x7050700000000000L, 4046545837843546112L, 2023272918921773056L, 0xE0A0E0000000000L, 0x705070000000000L, 0x302030000000000L, 0xC040C000000000L, 0xE0A0E000000000L, 0x70507000000000L, 15806819679076352L, 7903409839538176L, 0xE0A0E00000000L, 0x7050700000000L, 0x3020300000000L, 0xC040C0000000L, 0xE0A0E0000000L, 0x705070000000L, 61745389371392L, 30872694685696L, 0xE0A0E000000L, 0x70507000000L, 0x30203000000L, 0xC040C00000L, 0xE0A0E00000L, 0x7050700000L, 241192927232L, 120596463616L, 0xE0A0E0000L, 0x705070000L, 0x302030000L, 0xC040C000L, 0xE0A0E000L, 0x70507000L, 942159872L, 471079936L, 0xE0A0E00L, 0x7050700L, 0x3020300L, 0xC040C0L, 0xE0A0E0L, 0x705070L, 0x382838L, 0x1C141CL, 920078L, 460039L, 197123L, 49216L, 57504L, 28752L, 14376L, 7188L, 3594L, 1797L, 770L};
    public static final long[][][] ALL_ORDERED_KING_DIRS = new long[][][]{ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_H8};
    public static final int[][] ALL_ORDERED_KNIGHT_VALID_DIRS = new int[][]{ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H1, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H2, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H3, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H4, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H5, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H6, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H7, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_A8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_B8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_C8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_D8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_E8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_F8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_G8, ALL_KNIGHT_VALID_DIR_INDEXES_FROM_H8};
    public static final long[] ALL_KING_MOVES = new long[64];
    public static final int[][] ALL_KING_VALID_DIRS = new int[64][];
    public static final int[][][] ALL_KING_DIRS_WITH_FIELD_IDS = new int[64][][];
    public static final long[][][] ALL_KING_DIRS_WITH_BITBOARDS = new long[64][][];

    private static final void verify() {
        for (int i = 0; i < 64; ++i) {
            int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
            long moves = ALL_KING_MOVES[field_normalized_id];
            String result = "Field[" + i + ": " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
            int j = Bits.nextSetBit_L2R(0, moves);
            while (j <= 63 && j != -1) {
                result = result + Fields.ALL_ORDERED_NAMES[j] + " ";
                j = Bits.nextSetBit_L2R(j + 1, moves);
            }
        }
    }

    public static void main(String[] args) {
        KingPlies.genMembers();
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
                prefix = "public static final long ALL_KING_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                if (digit + 1 <= 7) {
                    result = (String)result + letters[letter] + digits[digit + 1] + " | ";
                }
                if (digit - 1 >= 0) {
                    result = (String)result + letters[letter] + digits[digit - 1] + " | ";
                }
                if (letter + 1 <= 7) {
                    result = (String)result + letters[letter + 1] + digits[digit] + " | ";
                    if (digit + 1 <= 7) {
                        result = (String)result + letters[letter + 1] + digits[digit + 1] + " | ";
                    }
                    if (digit - 1 >= 0) {
                        result = (String)result + letters[letter + 1] + digits[digit - 1] + " | ";
                    }
                }
                if (letter - 1 >= 0) {
                    result = (String)result + letters[letter - 1] + digits[digit] + " | ";
                    if (digit + 1 <= 7) {
                        result = (String)result + letters[letter - 1] + digits[digit + 1] + " | ";
                    }
                    if (digit - 1 >= 0) {
                        result = (String)result + letters[letter - 1] + digits[digit - 1] + " | ";
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
                prefix = "public static final long[][] ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix1 = "public static final int[] ALL_KNIGHT_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                result = prefix;
                Object result1 = prefix1;
                if (digit + 1 <= 7) {
                    result = (String)result + "{" + letters[letter] + digits[digit + 1] + "}, ";
                    result1 = (String)result1 + "0, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 1 <= 7 && digit + 1 <= 7) {
                    result = (String)result + "{" + letters[letter + 1] + digits[digit + 1] + "}, ";
                    result1 = (String)result1 + "1, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 1 <= 7) {
                    result = (String)result + "{" + letters[letter + 1] + digits[digit] + "}, ";
                    result1 = (String)result1 + "2, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter + 1 <= 7 && digit - 1 >= 0) {
                    result = (String)result + "{" + letters[letter + 1] + digits[digit - 1] + "}, ";
                    result1 = (String)result1 + "3, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (digit - 1 >= 0) {
                    result = (String)result + "{" + letters[letter] + digits[digit - 1] + "}, ";
                    result1 = (String)result1 + "4, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 1 >= 0 && digit - 1 >= 0) {
                    result = (String)result + "{" + letters[letter - 1] + digits[digit - 1] + "}, ";
                    result1 = (String)result1 + "5, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 1 >= 0) {
                    result = (String)result + "{" + letters[letter - 1] + digits[digit] + "}, ";
                    result1 = (String)result1 + "6, ";
                } else {
                    result = (String)result + "{  }, ";
                }
                if (letter - 1 >= 0 && digit + 1 <= 7) {
                    result = (String)result + "{" + letters[letter - 1] + digits[digit + 1] + "}, ";
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
        result = "public static final long[] ALL_ORDERED_KING_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_KING_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_KING_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_KING_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
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
        for (int i = 0; i < ALL_ORDERED_KING_MOVES.length; ++i) {
            int idx = Fields.IDX_ORDERED_2_A1H1[i];
            long fieldMoves = ALL_ORDERED_KING_MOVES[i];
            long[][] dirs = ALL_ORDERED_KING_DIRS[i];
            KingPlies.ALL_KING_MOVES[idx] = fieldMoves;
            KingPlies.ALL_KING_VALID_DIRS[idx] = ALL_ORDERED_KNIGHT_VALID_DIRS[i];
            KingPlies.ALL_KING_DIRS_WITH_BITBOARDS[idx] = dirs;
            KingPlies.ALL_KING_DIRS_WITH_FIELD_IDS[idx] = KingPlies.bitboards2fieldIDs(dirs);
        }
        KingPlies.verify();
    }
}

