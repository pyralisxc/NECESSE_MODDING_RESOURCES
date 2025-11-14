/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;

public class BlackPawnPlies
extends Fields {
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H1 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H1 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H1 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A2 = -4611686018427387904L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A2 = 0x4000000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A2 = Long.MIN_VALUE;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B2 = -2305843009213693952L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B2 = -6917529027641081856L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B2 = 0x4000000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C2 = 0x7000000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C2 = 0x5000000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C2 = 0x2000000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D2 = 0x3800000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D2 = 0x2800000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D2 = 0x1000000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E2 = 0x1C00000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E2 = 0x1400000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E2 = 0x800000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F2 = 0xE00000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F2 = 0xA00000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F2 = 0x400000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G2 = 0x700000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G2 = 0x500000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G2 = 0x200000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H2 = 0x300000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H2 = 0x200000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H2 = 0x100000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A3 = 0xC0000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A3 = 0x40000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A3 = 0x80000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B3 = 0xE0000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B3 = 0xA0000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B3 = 0x40000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C3 = 0x70000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C3 = 0x50000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C3 = 0x20000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D3 = 0x38000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D3 = 0x28000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D3 = 0x10000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E3 = 0x1C000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E3 = 0x14000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E3 = 0x8000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F3 = 0xE000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F3 = 0xA000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F3 = 0x4000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G3 = 0x7000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G3 = 0x5000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G3 = 0x2000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H3 = 0x3000000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H3 = 0x2000000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H3 = 0x1000000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A4 = 0xC00000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A4 = 0x400000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A4 = 0x800000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B4 = 0xE00000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B4 = 0xA00000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B4 = 0x400000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C4 = 0x700000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C4 = 0x500000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C4 = 0x200000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D4 = 0x380000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D4 = 0x280000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D4 = 0x100000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E4 = 0x1C0000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E4 = 0x140000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E4 = 0x80000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F4 = 0xE0000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F4 = 0xA0000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F4 = 0x40000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G4 = 0x70000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G4 = 0x50000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G4 = 0x20000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H4 = 0x30000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H4 = 0x20000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H4 = 0x10000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A5 = 0xC000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A5 = 0x4000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A5 = 0x8000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B5 = 0xE000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B5 = 0xA000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B5 = 0x4000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C5 = 0x7000000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C5 = 0x5000000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C5 = 0x2000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D5 = 0x3800000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D5 = 0x2800000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D5 = 0x1000000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E5 = 0x1C00000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E5 = 0x1400000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E5 = 0x800000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F5 = 0xE00000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F5 = 0xA00000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F5 = 0x400000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G5 = 0x700000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G5 = 0x500000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G5 = 0x200000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H5 = 0x300000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H5 = 0x200000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H5 = 0x100000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A6 = 0xC0000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A6 = 0x40000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A6 = 0x80000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B6 = 0xE0000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B6 = 0xA0000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B6 = 0x40000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C6 = 0x70000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C6 = 0x50000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C6 = 0x20000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D6 = 0x38000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D6 = 0x28000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D6 = 0x10000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E6 = 0x1C000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E6 = 0x14000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E6 = 0x8000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F6 = 0xE000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F6 = 0xA000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F6 = 0x4000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G6 = 0x7000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G6 = 0x5000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G6 = 0x2000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H6 = 0x3000000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H6 = 0x2000000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H6 = 0x1000000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A7 = 0x80C00000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A7 = 0x400000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A7 = 0x80800000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B7 = 0x40E00000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B7 = 0xA00000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B7 = 0x40400000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C7 = 0x20700000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C7 = 0x500000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C7 = 0x20200000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D7 = 272105472L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D7 = 0x280000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D7 = 0x10100000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E7 = 136052736L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E7 = 0x140000L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E7 = 0x8080000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F7 = 0x40E0000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F7 = 655360L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F7 = 0x4040000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G7 = 0x2070000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G7 = 327680L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G7 = 0x2020000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H7 = 0x1030000L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H7 = 131072L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H7 = 0x1010000L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_A8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_A8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_A8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_B8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_B8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_B8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_C8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_C8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_C8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_D8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_D8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_D8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_E8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_E8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_E8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_F8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_F8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_F8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_G8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_G8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_G8 = 0L;
    public static final long ALL_BLACK_PAWN_MOVES_FROM_H8 = 0L;
    public static final long ALL_BLACK_PAWN_ATTACKS_FROM_H8 = 0L;
    public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_H8 = 0L;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H1;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H1;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H1;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H1;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H2;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H2;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H2;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H2;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H3;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H3;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H3;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H3;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H4;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H4;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H4;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H4;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H5;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H5;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H5;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H5;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H6;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H6;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H6;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H6;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H7;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H7;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H7;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H7;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G8;
    public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H8;
    public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H8;
    public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H8;
    public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H8;
    public static final long[] ALL_ORDERED_BLACK_PAWN_MOVES;
    public static final long[] ALL_ORDERED_BLACK_PAWN_ATTACKS;
    public static final long[] ALL_ORDERED_BLACK_PAWN_NONATTACKS;
    public static final long[][][] ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS;
    public static final long[][][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS;
    public static final int[][] ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS;
    public static final int[][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS;
    public static final long[] ALL_BLACK_PAWN_MOVES;
    public static final long[] ALL_BLACK_PAWN_ATTACKS_MOVES;
    public static final long[] ALL_BLACK_PAWN_NONATTACKS_MOVES;
    public static final int[][] ALL_BLACK_PAWN_ATTACKS_VALID_DIRS;
    public static final long[][][] ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS;
    public static final int[][][] ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS;
    public static final int[][] ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS;
    public static final long[][][] ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS;
    public static final int[][][] ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS;
    public static final int[] B_MAGIC;

    public static int getMagic(int colour, int fromID, int toID) {
        if (colour != 1) {
            throw new IllegalStateException();
        }
        return -B_MAGIC[fromID] + B_MAGIC[toID];
    }

    private static final void verify() {
        for (int i = 0; i < 64; ++i) {
            int idx = Fields.IDX_ORDERED_2_A1H1[i];
            long moves = ALL_BLACK_PAWN_MOVES[idx];
            String result = "Field[{" + i + ", " + Fields.IDX_ORDERED_2_A1H1[i] + "}: " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
            if (moves != 0L) {
                int j = Bits.nextSetBit_L2R(0, moves);
                while (j <= 63 && j != -1) {
                    result = result + Fields.ALL_ORDERED_NAMES[j] + " ";
                    j = Bits.nextSetBit_L2R(j + 1, moves);
                }
                continue;
            }
            result = result + "NO_MOVES";
        }
    }

    public static void main(String[] args) {
        BlackPawnPlies.genMembers();
    }

    private static void genMembers() {
        int letter;
        int digit;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                String prefix = "public static final long ALL_BLACK_PAWN_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                String prefix1 = "public static final long ALL_BLACK_PAWN_ATTACKS_FROM_" + letters[letter] + digits[digit] + " = ";
                String prefix2 = "public static final long ALL_BLACK_PAWN_NONATTACKS_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                Object result1 = prefix1;
                Object result2 = prefix2;
                if (digit >= 1 && digit <= 6 && letter <= 6) {
                    result = (String)result + letters[letter + 1] + digits[digit - 1] + " | ";
                    result1 = (String)result1 + letters[letter + 1] + digits[digit - 1] + " | ";
                }
                if (digit >= 1 && digit <= 6 && letter >= 1) {
                    result = (String)result + letters[letter - 1] + digits[digit - 1] + " | ";
                    result1 = (String)result1 + letters[letter - 1] + digits[digit - 1] + " | ";
                }
                if (digit >= 1 && digit <= 6) {
                    result = (String)result + letters[letter] + digits[digit - 1] + " | ";
                    result2 = (String)result2 + letters[letter] + digits[digit - 1] + " | ";
                }
                if (digit == 6) {
                    result = (String)result + letters[letter] + digits[digit - 2] + " | ";
                    result2 = (String)result2 + letters[letter] + digits[digit - 2] + " | ";
                }
                if (((String)result).equals(prefix)) {
                    result = (String)result + "NUMBER_0";
                }
                if (((String)result1).equals(prefix1)) {
                    result1 = (String)result1 + "NUMBER_0";
                }
                if (((String)result2).equals(prefix2)) {
                    result2 = (String)result2 + "NUMBER_0";
                }
                if (((String)result).endsWith(" | ")) {
                    result = ((String)result).substring(0, ((String)result).length() - 3);
                }
                if (((String)result1).endsWith(" | ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 3);
                }
                if (((String)result2).endsWith(" | ")) {
                    result2 = ((String)result2).substring(0, ((String)result2).length() - 3);
                }
                result = (String)result + ";";
                result1 = (String)result1 + ";";
                result2 = (String)result2 + ";";
                System.out.println((String)result);
                System.out.println((String)result1);
                System.out.println((String)result2);
            }
        }
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                String prefix1 = "public static final long[][] ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix2 = "public static final long[][] ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix3 = "public static final int[] ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                String prefix4 = "public static final int[] ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                Object result1 = prefix1;
                Object result2 = prefix2;
                Object result3 = prefix3;
                Object result4 = prefix4;
                if (digit >= 1 && digit <= 6) {
                    result2 = (String)result2 + "{" + letters[letter] + digits[digit - 1] + "}, ";
                    result4 = (String)result4 + "0, ";
                } else {
                    result2 = (String)result2 + "{  }, ";
                }
                if (digit == 6) {
                    result2 = (String)result2 + "{" + letters[letter] + digits[digit - 2] + "}, ";
                    result4 = (String)result4 + "1, ";
                } else {
                    result2 = (String)result2 + "{  }, ";
                }
                if (digit >= 1 && digit <= 6 && letter >= 1) {
                    result1 = (String)result1 + "{" + letters[letter - 1] + digits[digit - 1] + "}, ";
                    result3 = (String)result3 + "0, ";
                } else {
                    result1 = (String)result1 + "{  }, ";
                }
                if (digit >= 1 && digit <= 6 && letter <= 6) {
                    result1 = (String)result1 + "{" + letters[letter + 1] + digits[digit - 1] + "}, ";
                    result3 = (String)result3 + "1, ";
                } else {
                    result1 = (String)result1 + "{  }, ";
                }
                if (((String)result1).equals(prefix1)) {
                    result1 = (String)result1 + "};";
                } else {
                    if (((String)result1).endsWith(", ")) {
                        result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                    }
                    result1 = (String)result1 + "};";
                }
                if (((String)result2).equals(prefix2)) {
                    result2 = (String)result2 + "};";
                } else {
                    if (((String)result2).endsWith(", ")) {
                        result2 = ((String)result2).substring(0, ((String)result2).length() - 2);
                    }
                    result2 = (String)result2 + "};";
                }
                if (((String)result3).equals(prefix3)) {
                    result3 = (String)result3 + "};";
                } else {
                    if (((String)result3).endsWith(", ")) {
                        result3 = ((String)result3).substring(0, ((String)result3).length() - 2);
                    }
                    result3 = (String)result3 + "};";
                }
                if (((String)result4).equals(prefix4)) {
                    result4 = (String)result4 + "};";
                } else {
                    if (((String)result4).endsWith(", ")) {
                        result4 = ((String)result4).substring(0, ((String)result4).length() - 2);
                    }
                    result4 = (String)result4 + "};";
                }
                System.out.println((String)result1);
                System.out.println((String)result2);
                System.out.println((String)result3);
                System.out.println((String)result4);
            }
        }
        result = "public static final long[] ALL_ORDERED_BLACK_PAWN_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_BLACK_PAWN_ATTACKS = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_ATTACKS_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_BLACK_PAWN_NONATTACKS = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_NONATTACKS_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final int[][] ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final int[][] ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
    }

    static {
        long fieldMoves;
        int idx;
        int i;
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H1 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H1 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{new long[0], {0x4000000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{{Long.MIN_VALUE}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A2 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{Long.MIN_VALUE}, {0x2000000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{0x4000000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x4000000000000000L}, {0x1000000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x2000000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x2000000000000000L}, {0x800000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x1000000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x1000000000000000L}, {0x400000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x800000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x800000000000000L}, {0x200000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x400000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x400000000000000L}, {0x100000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x200000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G2 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{{0x200000000000000L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{{0x100000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H2 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H2 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{new long[0], {0x40000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{{0x80000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A3 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x80000000000000L}, {0x20000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x40000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x40000000000000L}, {0x10000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x20000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x20000000000000L}, {0x8000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x10000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x10000000000000L}, {0x4000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x8000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x8000000000000L}, {0x2000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x4000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x4000000000000L}, {0x1000000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x2000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G3 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{{0x2000000000000L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{{0x1000000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H3 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H3 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{new long[0], {0x400000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{{0x800000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A4 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x800000000000L}, {0x200000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x400000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x400000000000L}, {0x100000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x200000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{0x200000000000L}, {0x80000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{0x100000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{0x100000000000L}, {0x40000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{0x80000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{0x80000000000L}, {0x20000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{0x40000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{0x40000000000L}, {0x10000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{0x20000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G4 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{{0x20000000000L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{{0x10000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H4 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H4 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{new long[0], {0x4000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{{0x8000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A5 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{0x8000000000L}, {0x2000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{0x4000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{0x4000000000L}, {0x1000000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{0x2000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{0x2000000000L}, {0x800000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{0x1000000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{0x1000000000L}, {0x400000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{0x800000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{0x800000000L}, {0x200000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{0x400000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{0x400000000L}, {0x100000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{0x200000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G5 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{{0x200000000L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{{0x100000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H5 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H5 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{new long[0], {0x40000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{{0x80000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A6 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{0x80000000L}, {0x20000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{0x40000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{0x40000000L}, {0x10000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{0x20000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{0x20000000L}, {0x8000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{0x10000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{0x10000000L}, {0x4000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{0x8000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{0x8000000L}, {0x2000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{0x4000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{0x4000000L}, {0x1000000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{0x2000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G6 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{{0x2000000L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{{0x1000000L}, new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H6 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H6 = new int[]{0};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{new long[0], {0x400000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{{0x800000L}, {0x80000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A7 = new int[]{1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{{0x800000L}, {0x200000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{{0x400000L}, {0x40000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{{0x400000L}, {0x100000L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{{0x200000L}, {0x20000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{{0x200000L}, {524288L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{{0x100000L}, {0x10000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{{0x100000L}, {262144L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{{524288L}, {0x8000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{{524288L}, {131072L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{{262144L}, {0x4000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{{262144L}, {65536L}};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{{131072L}, {0x2000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G7 = new int[]{0, 1};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{{131072L}, new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{{65536L}, {0x1000000L}};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H7 = new int[]{0};
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H7 = new int[]{0, 1};
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G8 = new int[0];
        ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0]};
        ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H8 = new int[0];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H8 = new int[0];
        ALL_ORDERED_BLACK_PAWN_MOVES = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -4611686018427387904L, -2305843009213693952L, 0x7000000000000000L, 0x3800000000000000L, 0x1C00000000000000L, 0xE00000000000000L, 0x700000000000000L, 0x300000000000000L, 0xC0000000000000L, 0xE0000000000000L, 0x70000000000000L, 0x38000000000000L, 0x1C000000000000L, 0xE000000000000L, 0x7000000000000L, 0x3000000000000L, 0xC00000000000L, 0xE00000000000L, 0x700000000000L, 0x380000000000L, 0x1C0000000000L, 0xE0000000000L, 0x70000000000L, 0x30000000000L, 0xC000000000L, 0xE000000000L, 0x7000000000L, 0x3800000000L, 0x1C00000000L, 0xE00000000L, 0x700000000L, 0x300000000L, 0xC0000000L, 0xE0000000L, 0x70000000L, 0x38000000L, 0x1C000000L, 0xE000000L, 0x7000000L, 0x3000000L, 0x80C00000L, 0x40E00000L, 0x20700000L, 272105472L, 136052736L, 0x40E0000L, 0x2070000L, 0x1030000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_BLACK_PAWN_ATTACKS = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4000000000000000L, -6917529027641081856L, 0x5000000000000000L, 0x2800000000000000L, 0x1400000000000000L, 0xA00000000000000L, 0x500000000000000L, 0x200000000000000L, 0x40000000000000L, 0xA0000000000000L, 0x50000000000000L, 0x28000000000000L, 0x14000000000000L, 0xA000000000000L, 0x5000000000000L, 0x2000000000000L, 0x400000000000L, 0xA00000000000L, 0x500000000000L, 0x280000000000L, 0x140000000000L, 0xA0000000000L, 0x50000000000L, 0x20000000000L, 0x4000000000L, 0xA000000000L, 0x5000000000L, 0x2800000000L, 0x1400000000L, 0xA00000000L, 0x500000000L, 0x200000000L, 0x40000000L, 0xA0000000L, 0x50000000L, 0x28000000L, 0x14000000L, 0xA000000L, 0x5000000L, 0x2000000L, 0x400000L, 0xA00000L, 0x500000L, 0x280000L, 0x140000L, 655360L, 327680L, 131072L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_BLACK_PAWN_NONATTACKS = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, 0x80000000000000L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L, 0x800000000000L, 0x400000000000L, 0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L, 0x8000000000L, 0x4000000000L, 0x2000000000L, 0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L, 0x80000000L, 0x40000000L, 0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L, 0x80800000L, 0x40400000L, 0x20200000L, 0x10100000L, 0x8080000L, 0x4040000L, 0x2020000L, 0x1010000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS = new long[][][]{ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H1, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H2, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H3, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H4, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H5, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H6, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H7, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_A8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_B8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_C8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_D8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_E8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_F8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_G8, ALL_BLACK_PAWN_ATTACKS_BY_DIR_AND_SEQ_FROM_H8};
        ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS = new long[][][]{ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H1, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H2, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H3, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H4, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H5, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H6, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H7, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_A8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_B8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_C8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_D8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_E8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_F8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_G8, ALL_BLACK_PAWN_NONATTACKS_BY_DIR_AND_SEQ_FROM_H8};
        ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[][]{ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H1, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H2, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H3, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H4, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H5, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H6, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H7, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_A8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_B8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_C8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_D8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_E8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_F8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_G8, ALL_BLACK_PAWN_ATTACKS_VALID_DIR_INDEXES_FROM_H8};
        ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[][]{ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H1, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H2, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H3, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H4, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H5, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H6, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H7, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_A8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_B8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_C8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_D8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_E8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_F8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_G8, ALL_BLACK_PAWN_NONATTACKS_VALID_DIR_INDEXES_FROM_H8};
        ALL_BLACK_PAWN_MOVES = new long[64];
        ALL_BLACK_PAWN_ATTACKS_MOVES = new long[64];
        ALL_BLACK_PAWN_NONATTACKS_MOVES = new long[64];
        ALL_BLACK_PAWN_ATTACKS_VALID_DIRS = new int[64][];
        ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS = new long[64][][];
        ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS = new int[64][][];
        ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS = new int[64][];
        ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS = new long[64][][];
        ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS = new int[64][][];
        B_MAGIC = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7});
        for (i = 0; i < ALL_ORDERED_BLACK_PAWN_MOVES.length; ++i) {
            idx = Fields.IDX_ORDERED_2_A1H1[i];
            BlackPawnPlies.ALL_BLACK_PAWN_MOVES[idx] = fieldMoves = ALL_ORDERED_BLACK_PAWN_MOVES[i];
        }
        for (i = 0; i < ALL_ORDERED_BLACK_PAWN_ATTACKS.length; ++i) {
            idx = Fields.IDX_ORDERED_2_A1H1[i];
            BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_MOVES[idx] = fieldMoves = ALL_ORDERED_BLACK_PAWN_ATTACKS[i];
        }
        for (i = 0; i < ALL_ORDERED_BLACK_PAWN_NONATTACKS.length; ++i) {
            idx = Fields.IDX_ORDERED_2_A1H1[i];
            BlackPawnPlies.ALL_BLACK_PAWN_NONATTACKS_MOVES[idx] = fieldMoves = ALL_ORDERED_BLACK_PAWN_NONATTACKS[i];
        }
        for (i = 0; i < ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS.length; ++i) {
            idx = Fields.IDX_ORDERED_2_A1H1[i];
            long[][] dirs = ALL_ORDERED_BLACK_PAWN_ATTACKS_DIRS[i];
            BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_VALID_DIRS[idx] = ALL_ORDERED_BLACK_PAWN_ATTACKS_VALID_DIRS[i];
            BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[idx] = dirs;
            BlackPawnPlies.ALL_BLACK_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[idx] = BlackPawnPlies.bitboards2fieldIDs(dirs);
        }
        for (i = 0; i < ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS.length; ++i) {
            idx = Fields.IDX_ORDERED_2_A1H1[i];
            long[][] dirs = ALL_ORDERED_BLACK_PAWN_NONATTACKS_DIRS[i];
            BlackPawnPlies.ALL_BLACK_PAWN_NONATTACKS_VALID_DIRS[idx] = ALL_ORDERED_BLACK_PAWN_NONATTACKS_VALID_DIRS[i];
            BlackPawnPlies.ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS[idx] = dirs;
            BlackPawnPlies.ALL_BLACK_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS[idx] = BlackPawnPlies.bitboards2fieldIDs(dirs);
        }
        BlackPawnPlies.verify();
    }
}

