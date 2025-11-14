/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;

public class CastlePlies
extends Fields {
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A1 = 0x80808080808080L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A1 = 0x7F00000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A1 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A1;
    public static final long ALL_CASTLE_MOVES_FROM_A1 = 9187484529235886208L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B1 = 0x40404040404040L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B1 = 0x3F00000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B1 = Long.MIN_VALUE;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B1;
    public static final long ALL_CASTLE_MOVES_FROM_B1 = -4665658569255796672L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C1 = 0x20202020202020L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C1 = 0x1F00000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C1 = -4611686018427387904L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C1;
    public static final long ALL_CASTLE_MOVES_FROM_C1 = -2368858081646862304L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D1 = 0x10101010101010L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D1 = 0xF00000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D1 = -2305843009213693952L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D1;
    public static final long ALL_CASTLE_MOVES_FROM_D1 = -1220457837842395120L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E1 = 0x8080808080808L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E1 = 0x700000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E1 = -1152921504606846976L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E1;
    public static final long ALL_CASTLE_MOVES_FROM_E1 = -646257715940161528L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F1 = 0x4040404040404L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F1 = 0x300000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F1 = -576460752303423488L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F1;
    public static final long ALL_CASTLE_MOVES_FROM_F1 = -359157654989044732L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G1 = 0x2020202020202L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G1 = 0x100000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G1 = -288230376151711744L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G1;
    public static final long ALL_CASTLE_MOVES_FROM_G1 = -215607624513486334L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H1 = 0x1010101010101L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H1 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H1 = 0L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H1 = -144115188075855872L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H1;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H1;
    public static final long ALL_CASTLE_MOVES_FROM_H1 = -143832609275707135L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A2 = 0x808080808080L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A2 = 0x7F000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A2 = Long.MIN_VALUE;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A2 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A2;
    public static final long ALL_CASTLE_MOVES_FROM_A2 = -9187483425412448128L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B2 = 0x404040404040L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B2 = 0x3F000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B2 = 0x4000000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B2 = 0x80000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B2;
    public static final long ALL_CASTLE_MOVES_FROM_B2 = 4665518383679160384L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C2 = 0x202020202020L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C2 = 0x1F000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C2 = 0x2000000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C2 = 0xC0000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C2;
    public static final long ALL_CASTLE_MOVES_FROM_C2 = 2368647251370188832L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D2 = 0x101010101010L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D2 = 0xF000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D2 = 0x1000000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D2 = 0xE0000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D2;
    public static final long ALL_CASTLE_MOVES_FROM_D2 = 1220211685215703056L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E2 = 0x80808080808L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E2 = 0x7000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E2 = 0x800000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E2 = 0xF0000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E2;
    public static final long ALL_CASTLE_MOVES_FROM_E2 = 645993902138460168L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F2 = 0x40404040404L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F2 = 0x3000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F2 = 0x400000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F2 = 0xF8000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F2;
    public static final long ALL_CASTLE_MOVES_FROM_F2 = 358885010599838724L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G2 = 0x20202020202L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G2 = 0x1000000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G2 = 0x200000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G2 = 0xFC000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G2;
    public static final long ALL_CASTLE_MOVES_FROM_G2 = 215330564830528002L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H2 = 0x10101010101L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H2 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H2 = 0x100000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H2 = 0xFE000000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H2;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H2;
    public static final long ALL_CASTLE_MOVES_FROM_H2 = 143553341945872641L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A3 = 0x8080808080L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A3 = 0x7F0000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A3 = -9187343239835811840L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A3 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A3;
    public static final long ALL_CASTLE_MOVES_FROM_A3 = -9187203049947365248L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B3 = 0x4040404040L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B3 = 0x3F0000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B3 = 0x4040000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B3 = 0x800000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B3;
    public static final long ALL_CASTLE_MOVES_FROM_B3 = 4629910699613634624L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C3 = 0x2020202020L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C3 = 0x1F0000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C3 = 0x2020000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C3 = 0xC00000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C3;
    public static final long ALL_CASTLE_MOVES_FROM_C3 = 2315095537539358752L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D3 = 0x1010101010L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D3 = 0xF0000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D3 = 0x1010000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D3 = 0xE00000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D3;
    public static final long ALL_CASTLE_MOVES_FROM_D3 = 1157687956502220816L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E3 = 0x808080808L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E3 = 0x70000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E3 = 0x808000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E3 = 0xF00000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E3;
    public static final long ALL_CASTLE_MOVES_FROM_E3 = 578984165983651848L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F3 = 0x404040404L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F3 = 0x30000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F3 = 0x404000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F3 = 0xF80000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F3;
    public static final long ALL_CASTLE_MOVES_FROM_F3 = 289632270724367364L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G3 = 0x202020202L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G3 = 0x10000000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G3 = 0x202000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G3 = 0xFC0000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G3;
    public static final long ALL_CASTLE_MOVES_FROM_G3 = 144956323094725122L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H3 = 0x101010101L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H3 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H3 = 0x101000000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H3 = 0xFE0000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H3;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H3;
    public static final long ALL_CASTLE_MOVES_FROM_H3 = 72618349279904001L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A4 = 0x80808080L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A4 = 0x7F00000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A4 = -9187202502347456512L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A4 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A4;
    public static final long ALL_CASTLE_MOVES_FROM_A4 = -9187201954730704768L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B4 = 0x40404040L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B4 = 0x3F00000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B4 = 0x4040400000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B4 = 0x8000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B4;
    public static final long ALL_CASTLE_MOVES_FROM_B4 = 4629771607097753664L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C4 = 0x20202020L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C4 = 0x1F00000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C4 = 0x2020200000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C4 = 0xC000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C4;
    public static final long ALL_CASTLE_MOVES_FROM_C4 = 2314886351157207072L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D4 = 0x10101010L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D4 = 0xF00000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D4 = 0x1010100000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D4 = 0xE000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D4;
    public static final long ALL_CASTLE_MOVES_FROM_D4 = 1157443723186933776L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E4 = 0x8080808L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E4 = 0x700000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E4 = 0x808080000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E4 = 0xF000000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E4;
    public static final long ALL_CASTLE_MOVES_FROM_E4 = 578722409201797128L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F4 = 0x4040404L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F4 = 0x300000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F4 = 0x404040000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F4 = 0xF800000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F4;
    public static final long ALL_CASTLE_MOVES_FROM_F4 = 289361752209228804L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G4 = 0x2020202L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G4 = 0x100000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G4 = 0x202020000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G4 = 0xFC00000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G4;
    public static final long ALL_CASTLE_MOVES_FROM_G4 = 144681423712944642L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H4 = 0x1010101L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H4 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H4 = 0x101010000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H4 = 0xFE00000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H4;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H4;
    public static final long ALL_CASTLE_MOVES_FROM_H4 = 72341259464802561L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A5 = 0x808080L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A5 = 0x7F000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A5 = -9187201952591642624L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A5 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A5;
    public static final long ALL_CASTLE_MOVES_FROM_A5 = -9187201950452514688L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B5 = 0x404040L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B5 = 0x3F000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B5 = 0x4040404000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B5 = 0x80000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B5;
    public static final long ALL_CASTLE_MOVES_FROM_B5 = 4629771063767613504L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C5 = 0x202020L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C5 = 0x1F000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C5 = 0x2020202000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C5 = 0xC0000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C5;
    public static final long ALL_CASTLE_MOVES_FROM_C5 = 2314885534022901792L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D5 = 0x101010L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D5 = 0xF000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D5 = 0x1010101000000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D5 = 0xE0000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D5;
    public static final long ALL_CASTLE_MOVES_FROM_D5 = 1157442769150545936L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E5 = 526344L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E5 = 0x7000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E5 = 0x808080800000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E5 = 0xF0000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E5;
    public static final long ALL_CASTLE_MOVES_FROM_E5 = 578721386714368008L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F5 = 263172L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F5 = 0x3000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F5 = 0x404040400000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F5 = 0xF8000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F5;
    public static final long ALL_CASTLE_MOVES_FROM_F5 = 289360695496279044L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G5 = 131586L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G5 = 0x1000000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G5 = 0x202020200000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G5 = 0xFC000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G5;
    public static final long ALL_CASTLE_MOVES_FROM_G5 = 144680349887234562L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H5 = 65793L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H5 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H5 = 0x101010100000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H5 = 0xFE000000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H5;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H5;
    public static final long ALL_CASTLE_MOVES_FROM_H5 = 72340177082712321L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A6 = 32896L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A6 = 0x7F0000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A6 = -9187201950444158976L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A6 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A6;
    public static final long ALL_CASTLE_MOVES_FROM_A6 = -9187201950435803008L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B6 = 16448L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B6 = 0x3F0000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B6 = 0x4040404040000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B6 = 0x800000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B6;
    public static final long ALL_CASTLE_MOVES_FROM_B6 = 4629771061645230144L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C6 = 8224L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C6 = 0x1F0000L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C6 = 0x2020202020000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C6 = 0xC00000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C6;
    public static final long ALL_CASTLE_MOVES_FROM_C6 = 2314885530830970912L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D6 = 4112L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D6 = 983040L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D6 = 0x1010101010000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D6 = 0xE00000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D6;
    public static final long ALL_CASTLE_MOVES_FROM_D6 = 1157442765423841296L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E6 = 2056L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E6 = 458752L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E6 = 0x808080808000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E6 = 0xF00000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E6;
    public static final long ALL_CASTLE_MOVES_FROM_E6 = 578721382720276488L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F6 = 1028L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F6 = 196608L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F6 = 0x404040404000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F6 = 0xF80000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F6;
    public static final long ALL_CASTLE_MOVES_FROM_F6 = 289360691368494084L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G6 = 514L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G6 = 65536L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G6 = 0x202020202000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G6 = 0xFC0000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G6;
    public static final long ALL_CASTLE_MOVES_FROM_G6 = 144680345692602882L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H6 = 257L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H6 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H6 = 0x101010101000000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H6 = 0xFE0000L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H6;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H6;
    public static final long ALL_CASTLE_MOVES_FROM_H6 = 72340172854657281L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A7 = 128L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A7 = 32512L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A7 = -9187201950435770368L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A7 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A7;
    public static final long ALL_CASTLE_MOVES_FROM_A7 = -9187201950435737728L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B7 = 64L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B7 = 16128L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B7 = 0x4040404040400000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B7 = 32768L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B7;
    public static final long ALL_CASTLE_MOVES_FROM_B7 = 4629771061636939584L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C7 = 32L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C7 = 7936L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C7 = 0x2020202020200000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C7 = 49152L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C7;
    public static final long ALL_CASTLE_MOVES_FROM_C7 = 2314885530818502432L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D7 = 16L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D7 = 3840L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D7 = 0x1010101010100000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D7 = 57344L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D7;
    public static final long ALL_CASTLE_MOVES_FROM_D7 = 1157442765409283856L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E7 = 8L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E7 = 1792L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E7 = 0x808080808080000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E7 = 61440L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E7;
    public static final long ALL_CASTLE_MOVES_FROM_E7 = 578721382704674568L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F7 = 4L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F7 = 768L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F7 = 0x404040404040000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F7 = 63488L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F7;
    public static final long ALL_CASTLE_MOVES_FROM_F7 = 289360691352369924L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G7 = 2L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G7 = 256L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G7 = 0x202020202020000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G7 = 64512L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G7;
    public static final long ALL_CASTLE_MOVES_FROM_G7 = 144680345676217602L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H7 = 1L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H7 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H7 = 0x101010101010000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H7 = 65024L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H7;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H7;
    public static final long ALL_CASTLE_MOVES_FROM_H7 = 72340172838141441L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_A8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_A8 = 127L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_A8 = -9187201950435737600L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_A8 = 0L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_A8;
    public static final long ALL_CASTLE_MOVES_FROM_A8 = -9187201950435737473L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_B8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_B8 = 63L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_B8 = 0x4040404040404000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_B8 = 128L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_B8;
    public static final long ALL_CASTLE_MOVES_FROM_B8 = 4629771061636907199L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_C8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_C8 = 31L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_C8 = 0x2020202020202000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_C8 = 192L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_C8;
    public static final long ALL_CASTLE_MOVES_FROM_C8 = 2314885530818453727L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_D8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_D8 = 15L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_D8 = 0x1010101010101000L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_D8 = 224L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_D8;
    public static final long ALL_CASTLE_MOVES_FROM_D8 = 1157442765409226991L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_E8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_E8 = 7L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_E8 = 0x808080808080800L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_E8 = 240L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_E8;
    public static final long ALL_CASTLE_MOVES_FROM_E8 = 578721382704613623L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_F8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_F8 = 3L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_F8 = 0x404040404040400L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_F8 = 248L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_F8;
    public static final long ALL_CASTLE_MOVES_FROM_F8 = 289360691352306939L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_G8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_G8 = 1L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_G8 = 0x202020202020200L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_G8 = 252L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_G8;
    public static final long ALL_CASTLE_MOVES_FROM_G8 = 144680345676153597L;
    public static final long ALL_CASTLE_DIR0_MOVES_FROM_H8 = 0L;
    public static final long ALL_CASTLE_DIR1_MOVES_FROM_H8 = 0L;
    public static final long ALL_CASTLE_DIR2_MOVES_FROM_H8 = 0x101010101010100L;
    public static final long ALL_CASTLE_DIR3_MOVES_FROM_H8 = 254L;
    public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H8;
    public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_H8;
    public static final long ALL_CASTLE_MOVES_FROM_H8 = 72340172838076926L;
    public static final long[] ALL_ORDERED_CASTLE_MOVES;
    public static final long[] ALL_ORDERED_DIR0_CASTLE_MOVES;
    public static final long[] ALL_ORDERED_DIR1_CASTLE_MOVES;
    public static final long[] ALL_ORDERED_DIR2_CASTLE_MOVES;
    public static final long[] ALL_ORDERED_DIR3_CASTLE_MOVES;
    public static final long[][][] ALL_ORDERED_CASTLE_DIRS;
    public static final int[][] ALL_ORDERED_CASTLE_VALID_DIRS;
    public static final long[] ALL_CASTLE_MOVES;
    public static final long[][] ALL_CASTLE_DIR_MOVES;
    public static final long[] ALL_CASTLE_DIR0_MOVES;
    public static final long[] ALL_CASTLE_DIR1_MOVES;
    public static final long[] ALL_CASTLE_DIR2_MOVES;
    public static final long[] ALL_CASTLE_DIR3_MOVES;
    public static final long[] ALL_CASTLE_MOVES_1P;
    public static final long[] ALL_CASTLE_MOVES_2P;
    public static final long[] ALL_CASTLE_MOVES_34P;
    public static final long[] ALL_CASTLE_MOVES_567P;
    public static final int UP_DIR = 0;
    public static final int DOWN_DIR = 2;
    public static final int LEFT_DIR = 3;
    public static final int RIGHT_DIR = 1;
    public static final int[][] ALL_CASTLE_VALID_DIRS;
    public static final int[][][] ALL_CASTLE_DIRS_WITH_FIELD_IDS;
    public static final long[][][] ALL_CASTLE_DIRS_WITH_BITBOARDS;
    public static final long[][] PATHS;
    public static final long PATH_NONE = -1L;

    public static final int mobility(int fieldID, long availableFields) {
        long mobility_1p = ALL_CASTLE_MOVES_1P[fieldID] & availableFields;
        if (mobility_1p == 0L) {
            return 0;
        }
        int max = 16;
        int mobility = 0;
        int mobility_1p_count = Utils.countBits(mobility_1p);
        max = max * mobility_1p_count / 4;
        mobility += mobility_1p_count;
        if (max > 0) {
            long mobility_2p = ALL_CASTLE_MOVES_2P[fieldID] & availableFields;
            int mobility_2p_count = Utils.countBits(mobility_2p);
            max = max * mobility_2p_count / 4;
            mobility += mobility_2p_count;
            if (max > 0) {
                long mobility_34p = ALL_CASTLE_MOVES_34P[fieldID] & availableFields;
                int mobility_34p_count = Utils.countBits(mobility_34p);
                max = max * mobility_34p_count / 8;
                mobility += mobility_34p_count;
                if (max > 0) {
                    long mobility_567p = ALL_CASTLE_MOVES_567P[fieldID] & availableFields;
                    int mobility_567p_count = Utils.countBits(mobility_567p);
                    max = max * mobility_567p_count / 12;
                    mobility += mobility_567p_count;
                }
            }
        }
        return mobility;
    }

    private static final void verify() {
        for (int i = 0; i < 64; ++i) {
            int field_normalized_id = Fields.IDX_ORDERED_2_A1H1[i];
            long moves = ALL_CASTLE_MOVES[field_normalized_id];
            String result = "Field[" + i + ": " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
            int j = Bits.nextSetBit_L2R(0, moves);
            while (j <= 63 && j != -1) {
                result = result + Fields.ALL_ORDERED_NAMES[j] + " ";
                j = Bits.nextSetBit_L2R(j + 1, moves);
            }
        }
    }

    public static void main(String[] args) {
        CastlePlies.genMembers();
    }

    private static void genMembers() {
        int letter;
        int digit;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                String prefix1 = "public static final long[][] ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix2 = "public static final int[] ALL_CASTLE_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                String prefix3 = "public static final long ALL_CASTLE_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                Object result1 = prefix1;
                Object result2 = prefix2;
                String result3 = prefix3;
                String prefix = "public static final long ALL_CASTLE_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                int dir_letter = letter;
                int dir_digit = digit + 1;
                if (digit == 7) {
                    result = (String)result + "NUMBER_0";
                } else {
                    result2 = (String)result2 + "0, ";
                }
                result1 = (String)result1 + "{";
                if (dir_letter <= 7 && dir_digit <= 7) {
                    result = prefix;
                    while (dir_letter <= 7 && dir_digit <= 7) {
                        result = (String)result + letters[dir_letter] + digits[dir_digit] + " | ";
                        result1 = (String)result1 + letters[dir_letter] + digits[dir_digit] + ", ";
                        ++dir_digit;
                    }
                    if (((String)result).endsWith(" | ")) {
                        result = ((String)result).substring(0, ((String)result).length() - 3);
                    }
                }
                if (!((String)result).equals(prefix)) {
                    result = (String)result + ";";
                    System.out.println((String)result);
                }
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                result1 = (String)result1 + "}, ";
                result3 = result3 + "ALL_CASTLE_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_CASTLE_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter + 1;
                dir_digit = digit;
                if (letter == 7) {
                    result = (String)result + "NUMBER_0";
                } else {
                    result2 = (String)result2 + "1, ";
                }
                result1 = (String)result1 + "{";
                if (dir_letter <= 7 && dir_digit >= 0) {
                    result = prefix;
                    while (dir_letter <= 7 && dir_digit >= 0) {
                        result = (String)result + letters[dir_letter] + digits[dir_digit] + " | ";
                        result1 = (String)result1 + letters[dir_letter] + digits[dir_digit] + ", ";
                        ++dir_letter;
                    }
                    if (((String)result).endsWith(" | ")) {
                        result = ((String)result).substring(0, ((String)result).length() - 3);
                    }
                }
                if (!((String)result).equals(prefix)) {
                    result = (String)result + ";";
                    System.out.println((String)result);
                }
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                result1 = (String)result1 + "}, ";
                result3 = result3 + "ALL_CASTLE_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_CASTLE_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter;
                dir_digit = digit - 1;
                if (digit == 0) {
                    result = (String)result + "NUMBER_0";
                } else {
                    result2 = (String)result2 + "2, ";
                }
                result1 = (String)result1 + "{";
                if (dir_letter >= 0 && dir_digit >= 0) {
                    result = prefix;
                    while (dir_letter >= 0 && dir_digit >= 0) {
                        result = (String)result + letters[dir_letter] + digits[dir_digit] + " | ";
                        result1 = (String)result1 + letters[dir_letter] + digits[dir_digit] + ", ";
                        --dir_digit;
                    }
                    if (((String)result).endsWith(" | ")) {
                        result = ((String)result).substring(0, ((String)result).length() - 3);
                    }
                }
                if (!((String)result).equals(prefix)) {
                    result = (String)result + ";";
                    System.out.println((String)result);
                }
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                result1 = (String)result1 + "}, ";
                result3 = result3 + "ALL_CASTLE_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_CASTLE_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter - 1;
                dir_digit = digit;
                if (letter == 0) {
                    result = (String)result + "NUMBER_0";
                } else {
                    result2 = (String)result2 + "3, ";
                }
                result1 = (String)result1 + "{";
                if (dir_letter >= 0 && dir_digit <= 7) {
                    result = prefix;
                    while (dir_letter >= 0 && dir_digit <= 7) {
                        result = (String)result + letters[dir_letter] + digits[dir_digit] + " | ";
                        result1 = (String)result1 + letters[dir_letter] + digits[dir_digit] + ", ";
                        --dir_letter;
                    }
                    if (((String)result).endsWith(" | ")) {
                        result = ((String)result).substring(0, ((String)result).length() - 3);
                    }
                }
                if (!((String)result).equals(prefix)) {
                    result = (String)result + ";";
                    System.out.println((String)result);
                }
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                result1 = (String)result1 + "}, ";
                result3 = result3 + "ALL_CASTLE_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ";";
                if (((String)result1).endsWith(", ")) {
                    result1 = ((String)result1).substring(0, ((String)result1).length() - 2);
                }
                if (((String)result2).endsWith(", ")) {
                    result2 = ((String)result2).substring(0, ((String)result2).length() - 2);
                }
                result1 = (String)result1 + "};";
                result2 = (String)result2 + "};";
                System.out.println((String)result1);
                System.out.println((String)result2);
                System.out.println(result3);
            }
        }
        System.out.println("\r\n");
        result = "public static final long[] ALL_ORDERED_CASTLE_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR0_CASTLE_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR1_CASTLE_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR2_CASTLE_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR3_CASTLE_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_CASTLE_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final int[][] ALL_ORDERED_CASTLE_VALID_DIRS = new int[][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_CASTLE_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
    }

    static {
        int i;
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{{0x80000000000000L, 0x800000000000L, 0x8000000000L, 0x80000000L, 0x800000L, 32768L, 128L}, {0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L}, new long[0], new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A1 = new int[]{0, 1};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{{0x40000000000000L, 0x400000000000L, 0x4000000000L, 0x40000000L, 0x400000L, 16384L, 64L}, {0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L}, new long[0], {Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{{0x20000000000000L, 0x200000000000L, 0x2000000000L, 0x20000000L, 0x200000L, 8192L, 32L}, {0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L}, new long[0], {0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{{0x10000000000000L, 0x100000000000L, 0x1000000000L, 0x10000000L, 0x100000L, 4096L, 16L}, {0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L}, new long[0], {0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{{0x8000000000000L, 0x80000000000L, 0x800000000L, 0x8000000L, 524288L, 2048L, 8L}, {0x400000000000000L, 0x200000000000000L, 0x100000000000000L}, new long[0], {0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{{0x4000000000000L, 0x40000000000L, 0x400000000L, 0x4000000L, 262144L, 1024L, 4L}, {0x200000000000000L, 0x100000000000000L}, new long[0], {0x800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{{0x2000000000000L, 0x20000000000L, 0x200000000L, 0x2000000L, 131072L, 512L, 2L}, {0x100000000000000L}, new long[0], {0x400000000000000L, 0x800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G1 = new int[]{0, 1, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{{0x1000000000000L, 0x10000000000L, 0x100000000L, 0x1000000L, 65536L, 256L, 1L}, new long[0], new long[0], {0x200000000000000L, 0x400000000000000L, 0x800000000000000L, 0x1000000000000000L, 0x2000000000000000L, 0x4000000000000000L, Long.MIN_VALUE}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H1 = new int[]{0, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{{0x800000000000L, 0x8000000000L, 0x80000000L, 0x800000L, 32768L, 128L}, {0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L}, {Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A2 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{0x400000000000L, 0x4000000000L, 0x40000000L, 0x400000L, 16384L, 64L}, {0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L}, {0x4000000000000000L}, {0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x200000000000L, 0x2000000000L, 0x20000000L, 0x200000L, 8192L, 32L}, {0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L}, {0x2000000000000000L}, {0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x100000000000L, 0x1000000000L, 0x10000000L, 0x100000L, 4096L, 16L}, {0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L}, {0x1000000000000000L}, {0x20000000000000L, 0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x80000000000L, 0x800000000L, 0x8000000L, 524288L, 2048L, 8L}, {0x4000000000000L, 0x2000000000000L, 0x1000000000000L}, {0x800000000000000L}, {0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x40000000000L, 0x400000000L, 0x4000000L, 262144L, 1024L, 4L}, {0x2000000000000L, 0x1000000000000L}, {0x400000000000000L}, {0x8000000000000L, 0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x20000000000L, 0x200000000L, 0x2000000L, 131072L, 512L, 2L}, {0x1000000000000L}, {0x200000000000000L}, {0x4000000000000L, 0x8000000000000L, 0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G2 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{{0x10000000000L, 0x100000000L, 0x1000000L, 65536L, 256L, 1L}, new long[0], {0x100000000000000L}, {0x2000000000000L, 0x4000000000000L, 0x8000000000000L, 0x10000000000000L, 0x20000000000000L, 0x40000000000000L, 0x80000000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H2 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{{0x8000000000L, 0x80000000L, 0x800000L, 32768L, 128L}, {0x400000000000L, 0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L}, {0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A3 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x4000000000L, 0x40000000L, 0x400000L, 16384L, 64L}, {0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L}, {0x40000000000000L, 0x4000000000000000L}, {0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x2000000000L, 0x20000000L, 0x200000L, 8192L, 32L}, {0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L}, {0x20000000000000L, 0x2000000000000000L}, {0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x1000000000L, 0x10000000L, 0x100000L, 4096L, 16L}, {0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L}, {0x10000000000000L, 0x1000000000000000L}, {0x200000000000L, 0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x800000000L, 0x8000000L, 524288L, 2048L, 8L}, {0x40000000000L, 0x20000000000L, 0x10000000000L}, {0x8000000000000L, 0x800000000000000L}, {0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x400000000L, 0x4000000L, 262144L, 1024L, 4L}, {0x20000000000L, 0x10000000000L}, {0x4000000000000L, 0x400000000000000L}, {0x80000000000L, 0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x200000000L, 0x2000000L, 131072L, 512L, 2L}, {0x10000000000L}, {0x2000000000000L, 0x200000000000000L}, {0x40000000000L, 0x80000000000L, 0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G3 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{{0x100000000L, 0x1000000L, 65536L, 256L, 1L}, new long[0], {0x1000000000000L, 0x100000000000000L}, {0x20000000000L, 0x40000000000L, 0x80000000000L, 0x100000000000L, 0x200000000000L, 0x400000000000L, 0x800000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H3 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{{0x80000000L, 0x800000L, 32768L, 128L}, {0x4000000000L, 0x2000000000L, 0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L}, {0x800000000000L, 0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A4 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x40000000L, 0x400000L, 16384L, 64L}, {0x2000000000L, 0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L}, {0x400000000000L, 0x40000000000000L, 0x4000000000000000L}, {0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x20000000L, 0x200000L, 8192L, 32L}, {0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L}, {0x200000000000L, 0x20000000000000L, 0x2000000000000000L}, {0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{0x10000000L, 0x100000L, 4096L, 16L}, {0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L}, {0x100000000000L, 0x10000000000000L, 0x1000000000000000L}, {0x2000000000L, 0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{0x8000000L, 524288L, 2048L, 8L}, {0x400000000L, 0x200000000L, 0x100000000L}, {0x80000000000L, 0x8000000000000L, 0x800000000000000L}, {0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{0x4000000L, 262144L, 1024L, 4L}, {0x200000000L, 0x100000000L}, {0x40000000000L, 0x4000000000000L, 0x400000000000000L}, {0x800000000L, 0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{0x2000000L, 131072L, 512L, 2L}, {0x100000000L}, {0x20000000000L, 0x2000000000000L, 0x200000000000000L}, {0x400000000L, 0x800000000L, 0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G4 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{{0x1000000L, 65536L, 256L, 1L}, new long[0], {0x10000000000L, 0x1000000000000L, 0x100000000000000L}, {0x200000000L, 0x400000000L, 0x800000000L, 0x1000000000L, 0x2000000000L, 0x4000000000L, 0x8000000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H4 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{{0x800000L, 32768L, 128L}, {0x40000000L, 0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L}, {0x8000000000L, 0x800000000000L, 0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A5 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{0x400000L, 16384L, 64L}, {0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L}, {0x4000000000L, 0x400000000000L, 0x40000000000000L, 0x4000000000000000L}, {0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{0x200000L, 8192L, 32L}, {0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L}, {0x2000000000L, 0x200000000000L, 0x20000000000000L, 0x2000000000000000L}, {0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{0x100000L, 4096L, 16L}, {0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L}, {0x1000000000L, 0x100000000000L, 0x10000000000000L, 0x1000000000000000L}, {0x20000000L, 0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{524288L, 2048L, 8L}, {0x4000000L, 0x2000000L, 0x1000000L}, {0x800000000L, 0x80000000000L, 0x8000000000000L, 0x800000000000000L}, {0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{262144L, 1024L, 4L}, {0x2000000L, 0x1000000L}, {0x400000000L, 0x40000000000L, 0x4000000000000L, 0x400000000000000L}, {0x8000000L, 0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{131072L, 512L, 2L}, {0x1000000L}, {0x200000000L, 0x20000000000L, 0x2000000000000L, 0x200000000000000L}, {0x4000000L, 0x8000000L, 0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G5 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{{65536L, 256L, 1L}, new long[0], {0x100000000L, 0x10000000000L, 0x1000000000000L, 0x100000000000000L}, {0x2000000L, 0x4000000L, 0x8000000L, 0x10000000L, 0x20000000L, 0x40000000L, 0x80000000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H5 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{{32768L, 128L}, {0x400000L, 0x200000L, 0x100000L, 524288L, 262144L, 131072L, 65536L}, {0x80000000L, 0x8000000000L, 0x800000000000L, 0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A6 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{16384L, 64L}, {0x200000L, 0x100000L, 524288L, 262144L, 131072L, 65536L}, {0x40000000L, 0x4000000000L, 0x400000000000L, 0x40000000000000L, 0x4000000000000000L}, {0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{8192L, 32L}, {0x100000L, 524288L, 262144L, 131072L, 65536L}, {0x20000000L, 0x2000000000L, 0x200000000000L, 0x20000000000000L, 0x2000000000000000L}, {0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{4096L, 16L}, {524288L, 262144L, 131072L, 65536L}, {0x10000000L, 0x1000000000L, 0x100000000000L, 0x10000000000000L, 0x1000000000000000L}, {0x200000L, 0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{2048L, 8L}, {262144L, 131072L, 65536L}, {0x8000000L, 0x800000000L, 0x80000000000L, 0x8000000000000L, 0x800000000000000L}, {0x100000L, 0x200000L, 0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{1024L, 4L}, {131072L, 65536L}, {0x4000000L, 0x400000000L, 0x40000000000L, 0x4000000000000L, 0x400000000000000L}, {524288L, 0x100000L, 0x200000L, 0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{512L, 2L}, {65536L}, {0x2000000L, 0x200000000L, 0x20000000000L, 0x2000000000000L, 0x200000000000000L}, {262144L, 524288L, 0x100000L, 0x200000L, 0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G6 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{{256L, 1L}, new long[0], {0x1000000L, 0x100000000L, 0x10000000000L, 0x1000000000000L, 0x100000000000000L}, {131072L, 262144L, 524288L, 0x100000L, 0x200000L, 0x400000L, 0x800000L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H6 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{{128L}, {16384L, 8192L, 4096L, 2048L, 1024L, 512L, 256L}, {0x800000L, 0x80000000L, 0x8000000000L, 0x800000000000L, 0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A7 = new int[]{0, 1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{{64L}, {8192L, 4096L, 2048L, 1024L, 512L, 256L}, {0x400000L, 0x40000000L, 0x4000000000L, 0x400000000000L, 0x40000000000000L, 0x4000000000000000L}, {32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{{32L}, {4096L, 2048L, 1024L, 512L, 256L}, {0x200000L, 0x20000000L, 0x2000000000L, 0x200000000000L, 0x20000000000000L, 0x2000000000000000L}, {16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{{16L}, {2048L, 1024L, 512L, 256L}, {0x100000L, 0x10000000L, 0x1000000000L, 0x100000000000L, 0x10000000000000L, 0x1000000000000000L}, {8192L, 16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{{8L}, {1024L, 512L, 256L}, {524288L, 0x8000000L, 0x800000000L, 0x80000000000L, 0x8000000000000L, 0x800000000000000L}, {4096L, 8192L, 16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{{4L}, {512L, 256L}, {262144L, 0x4000000L, 0x400000000L, 0x40000000000L, 0x4000000000000L, 0x400000000000000L}, {2048L, 4096L, 8192L, 16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{{2L}, {256L}, {131072L, 0x2000000L, 0x200000000L, 0x20000000000L, 0x2000000000000L, 0x200000000000000L}, {1024L, 2048L, 4096L, 8192L, 16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G7 = new int[]{0, 1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{{1L}, new long[0], {65536L, 0x1000000L, 0x100000000L, 0x10000000000L, 0x1000000000000L, 0x100000000000000L}, {512L, 1024L, 2048L, 4096L, 8192L, 16384L, 32768L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H7 = new int[]{0, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], {64L, 32L, 16L, 8L, 4L, 2L, 1L}, {32768L, 0x800000L, 0x80000000L, 0x8000000000L, 0x800000000000L, 0x80000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_A8 = new int[]{1, 2};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], {32L, 16L, 8L, 4L, 2L, 1L}, {16384L, 0x400000L, 0x40000000L, 0x4000000000L, 0x400000000000L, 0x40000000000000L, 0x4000000000000000L}, {128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_B8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], {16L, 8L, 4L, 2L, 1L}, {8192L, 0x200000L, 0x20000000L, 0x2000000000L, 0x200000000000L, 0x20000000000000L, 0x2000000000000000L}, {64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_C8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], {8L, 4L, 2L, 1L}, {4096L, 0x100000L, 0x10000000L, 0x1000000000L, 0x100000000000L, 0x10000000000000L, 0x1000000000000000L}, {32L, 64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_D8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], {4L, 2L, 1L}, {2048L, 524288L, 0x8000000L, 0x800000000L, 0x80000000000L, 0x8000000000000L, 0x800000000000000L}, {16L, 32L, 64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_E8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], {2L, 1L}, {1024L, 262144L, 0x4000000L, 0x400000000L, 0x40000000000L, 0x4000000000000L, 0x400000000000000L}, {8L, 16L, 32L, 64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_F8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], {1L}, {512L, 131072L, 0x2000000L, 0x200000000L, 0x20000000000L, 0x2000000000000L, 0x200000000000000L}, {4L, 8L, 16L, 32L, 64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_G8 = new int[]{1, 2, 3};
        ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0], {256L, 65536L, 0x1000000L, 0x100000000L, 0x10000000000L, 0x1000000000000L, 0x100000000000000L}, {2L, 4L, 8L, 16L, 32L, 64L, 128L}};
        ALL_CASTLE_VALID_DIR_INDEXES_FROM_H8 = new int[]{2, 3};
        ALL_ORDERED_CASTLE_MOVES = new long[]{9187484529235886208L, -4665658569255796672L, -2368858081646862304L, -1220457837842395120L, -646257715940161528L, -359157654989044732L, -215607624513486334L, -143832609275707135L, -9187483425412448128L, 4665518383679160384L, 2368647251370188832L, 1220211685215703056L, 645993902138460168L, 358885010599838724L, 215330564830528002L, 143553341945872641L, -9187203049947365248L, 4629910699613634624L, 2315095537539358752L, 1157687956502220816L, 578984165983651848L, 289632270724367364L, 144956323094725122L, 72618349279904001L, -9187201954730704768L, 4629771607097753664L, 2314886351157207072L, 1157443723186933776L, 578722409201797128L, 289361752209228804L, 144681423712944642L, 72341259464802561L, -9187201950452514688L, 4629771063767613504L, 2314885534022901792L, 1157442769150545936L, 578721386714368008L, 289360695496279044L, 144680349887234562L, 72340177082712321L, -9187201950435803008L, 4629771061645230144L, 2314885530830970912L, 1157442765423841296L, 578721382720276488L, 289360691368494084L, 144680345692602882L, 72340172854657281L, -9187201950435737728L, 4629771061636939584L, 2314885530818502432L, 1157442765409283856L, 578721382704674568L, 289360691352369924L, 144680345676217602L, 72340172838141441L, -9187201950435737473L, 4629771061636907199L, 2314885530818453727L, 1157442765409226991L, 578721382704613623L, 289360691352306939L, 144680345676153597L, 72340172838076926L};
        ALL_ORDERED_DIR0_CASTLE_MOVES = new long[]{0x80808080808080L, 0x40404040404040L, 0x20202020202020L, 0x10101010101010L, 0x8080808080808L, 0x4040404040404L, 0x2020202020202L, 0x1010101010101L, 0x808080808080L, 0x404040404040L, 0x202020202020L, 0x101010101010L, 0x80808080808L, 0x40404040404L, 0x20202020202L, 0x10101010101L, 0x8080808080L, 0x4040404040L, 0x2020202020L, 0x1010101010L, 0x808080808L, 0x404040404L, 0x202020202L, 0x101010101L, 0x80808080L, 0x40404040L, 0x20202020L, 0x10101010L, 0x8080808L, 0x4040404L, 0x2020202L, 0x1010101L, 0x808080L, 0x404040L, 0x202020L, 0x101010L, 526344L, 263172L, 131586L, 65793L, 32896L, 16448L, 8224L, 4112L, 2056L, 1028L, 514L, 257L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_DIR1_CASTLE_MOVES = new long[]{0x7F00000000000000L, 0x3F00000000000000L, 0x1F00000000000000L, 0xF00000000000000L, 0x700000000000000L, 0x300000000000000L, 0x100000000000000L, 0L, 0x7F000000000000L, 0x3F000000000000L, 0x1F000000000000L, 0xF000000000000L, 0x7000000000000L, 0x3000000000000L, 0x1000000000000L, 0L, 0x7F0000000000L, 0x3F0000000000L, 0x1F0000000000L, 0xF0000000000L, 0x70000000000L, 0x30000000000L, 0x10000000000L, 0L, 0x7F00000000L, 0x3F00000000L, 0x1F00000000L, 0xF00000000L, 0x700000000L, 0x300000000L, 0x100000000L, 0L, 0x7F000000L, 0x3F000000L, 0x1F000000L, 0xF000000L, 0x7000000L, 0x3000000L, 0x1000000L, 0L, 0x7F0000L, 0x3F0000L, 0x1F0000L, 983040L, 458752L, 196608L, 65536L, 0L, 32512L, 16128L, 7936L, 3840L, 1792L, 768L, 256L, 0L, 127L, 63L, 31L, 15L, 7L, 3L, 1L, 0L};
        ALL_ORDERED_DIR2_CASTLE_MOVES = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, -9187343239835811840L, 0x4040000000000000L, 0x2020000000000000L, 0x1010000000000000L, 0x808000000000000L, 0x404000000000000L, 0x202000000000000L, 0x101000000000000L, -9187202502347456512L, 0x4040400000000000L, 0x2020200000000000L, 0x1010100000000000L, 0x808080000000000L, 0x404040000000000L, 0x202020000000000L, 0x101010000000000L, -9187201952591642624L, 0x4040404000000000L, 0x2020202000000000L, 0x1010101000000000L, 0x808080800000000L, 0x404040400000000L, 0x202020200000000L, 0x101010100000000L, -9187201950444158976L, 0x4040404040000000L, 0x2020202020000000L, 0x1010101010000000L, 0x808080808000000L, 0x404040404000000L, 0x202020202000000L, 0x101010101000000L, -9187201950435770368L, 0x4040404040400000L, 0x2020202020200000L, 0x1010101010100000L, 0x808080808080000L, 0x404040404040000L, 0x202020202020000L, 0x101010101010000L, -9187201950435737600L, 0x4040404040404000L, 0x2020202020202000L, 0x1010101010101000L, 0x808080808080800L, 0x404040404040400L, 0x202020202020200L, 0x101010101010100L};
        ALL_ORDERED_DIR3_CASTLE_MOVES = new long[]{0L, Long.MIN_VALUE, -4611686018427387904L, -2305843009213693952L, -1152921504606846976L, -576460752303423488L, -288230376151711744L, -144115188075855872L, 0L, 0x80000000000000L, 0xC0000000000000L, 0xE0000000000000L, 0xF0000000000000L, 0xF8000000000000L, 0xFC000000000000L, 0xFE000000000000L, 0L, 0x800000000000L, 0xC00000000000L, 0xE00000000000L, 0xF00000000000L, 0xF80000000000L, 0xFC0000000000L, 0xFE0000000000L, 0L, 0x8000000000L, 0xC000000000L, 0xE000000000L, 0xF000000000L, 0xF800000000L, 0xFC00000000L, 0xFE00000000L, 0L, 0x80000000L, 0xC0000000L, 0xE0000000L, 0xF0000000L, 0xF8000000L, 0xFC000000L, 0xFE000000L, 0L, 0x800000L, 0xC00000L, 0xE00000L, 0xF00000L, 0xF80000L, 0xFC0000L, 0xFE0000L, 0L, 32768L, 49152L, 57344L, 61440L, 63488L, 64512L, 65024L, 0L, 128L, 192L, 224L, 240L, 248L, 252L, 254L};
        ALL_ORDERED_CASTLE_DIRS = new long[][][]{ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_CASTLE_MOVES_BY_DIR_AND_SEQ_FROM_H8};
        ALL_ORDERED_CASTLE_VALID_DIRS = new int[][]{ALL_CASTLE_VALID_DIR_INDEXES_FROM_A1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H1, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H2, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H3, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H4, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H5, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H6, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H7, ALL_CASTLE_VALID_DIR_INDEXES_FROM_A8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_B8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_C8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_D8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_E8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_F8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_G8, ALL_CASTLE_VALID_DIR_INDEXES_FROM_H8};
        ALL_CASTLE_MOVES = new long[64];
        ALL_CASTLE_DIR_MOVES = new long[4][64];
        ALL_CASTLE_DIR0_MOVES = new long[64];
        ALL_CASTLE_DIR1_MOVES = new long[64];
        ALL_CASTLE_DIR2_MOVES = new long[64];
        ALL_CASTLE_DIR3_MOVES = new long[64];
        ALL_CASTLE_MOVES_1P = new long[64];
        ALL_CASTLE_MOVES_2P = new long[64];
        ALL_CASTLE_MOVES_34P = new long[64];
        ALL_CASTLE_MOVES_567P = new long[64];
        ALL_CASTLE_VALID_DIRS = new int[64][];
        ALL_CASTLE_DIRS_WITH_FIELD_IDS = new int[64][][];
        ALL_CASTLE_DIRS_WITH_BITBOARDS = new long[64][][];
        PATHS = new long[64][64];
        for (i = 0; i < ALL_ORDERED_CASTLE_MOVES.length; ++i) {
            int idx = Fields.IDX_ORDERED_2_A1H1[i];
            long fieldMoves = ALL_ORDERED_CASTLE_MOVES[i];
            long[][] dirs = ALL_ORDERED_CASTLE_DIRS[i];
            CastlePlies.ALL_CASTLE_MOVES[idx] = fieldMoves;
            CastlePlies.ALL_CASTLE_VALID_DIRS[idx] = ALL_ORDERED_CASTLE_VALID_DIRS[i];
            CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[idx] = dirs;
            CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[idx] = CastlePlies.bitboards2fieldIDs(dirs);
            CastlePlies.ALL_CASTLE_DIR0_MOVES[idx] = ALL_ORDERED_DIR0_CASTLE_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR1_MOVES[idx] = ALL_ORDERED_DIR1_CASTLE_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR2_MOVES[idx] = ALL_ORDERED_DIR2_CASTLE_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR3_MOVES[idx] = ALL_ORDERED_DIR3_CASTLE_MOVES[i];
            for (int dirID : ALL_CASTLE_VALID_DIRS[idx]) {
                long[] dirBitboards = dirs[dirID];
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long toBitboard = dirs[dirID][seq];
                    if (seq == 0) {
                        int n = idx;
                        ALL_CASTLE_MOVES_1P[n] = ALL_CASTLE_MOVES_1P[n] | toBitboard;
                        continue;
                    }
                    if (seq == 1) {
                        int n = idx;
                        ALL_CASTLE_MOVES_2P[n] = ALL_CASTLE_MOVES_2P[n] | toBitboard;
                        continue;
                    }
                    if (seq == 2 || seq == 3) {
                        int n = idx;
                        ALL_CASTLE_MOVES_34P[n] = ALL_CASTLE_MOVES_34P[n] | toBitboard;
                        continue;
                    }
                    int n = idx;
                    ALL_CASTLE_MOVES_567P[n] = ALL_CASTLE_MOVES_567P[n] | toBitboard;
                }
            }
        }
        for (int from = 0; from < ALL_ORDERED_CASTLE_MOVES.length; ++from) {
            for (int to = 0; to < ALL_ORDERED_CASTLE_MOVES.length; ++to) {
                int fromID = Fields.IDX_ORDERED_2_A1H1[from];
                long fromAttacks = ALL_CASTLE_MOVES[fromID];
                int toID = Fields.IDX_ORDERED_2_A1H1[to];
                long toBitboard = ALL_A1H1[toID];
                if ((fromAttacks & toBitboard) != 0L) {
                    long[] fieldBiboards;
                    int[] fieldIDs;
                    if ((ALL_CASTLE_DIR0_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_CASTLE_DIRS_WITH_FIELD_IDS[fromID][0];
                        fieldBiboards = ALL_CASTLE_DIRS_WITH_BITBOARDS[fromID][0];
                    } else if ((ALL_CASTLE_DIR1_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_CASTLE_DIRS_WITH_FIELD_IDS[fromID][1];
                        fieldBiboards = ALL_CASTLE_DIRS_WITH_BITBOARDS[fromID][1];
                    } else if ((ALL_CASTLE_DIR2_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_CASTLE_DIRS_WITH_FIELD_IDS[fromID][2];
                        fieldBiboards = ALL_CASTLE_DIRS_WITH_BITBOARDS[fromID][2];
                    } else if ((ALL_CASTLE_DIR3_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_CASTLE_DIRS_WITH_FIELD_IDS[fromID][3];
                        fieldBiboards = ALL_CASTLE_DIRS_WITH_BITBOARDS[fromID][3];
                    } else {
                        throw new IllegalStateException();
                    }
                    for (int i2 = 0; i2 < fieldIDs.length && fieldIDs[i2] != toID; ++i2) {
                        long[] lArray = PATHS[fromID];
                        int n = toID;
                        lArray[n] = lArray[n] | fieldBiboards[i2];
                        if (i2 != fieldIDs.length - 1) continue;
                        throw new IllegalStateException();
                    }
                    if (PATHS[fromID][toID] != -1L) continue;
                    throw new IllegalStateException();
                }
                CastlePlies.PATHS[fromID][toID] = -1L;
            }
        }
        for (i = 0; i < 64; ++i) {
            CastlePlies.ALL_CASTLE_DIR_MOVES[0][i] = ALL_CASTLE_DIR0_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR_MOVES[1][i] = ALL_CASTLE_DIR1_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR_MOVES[2][i] = ALL_CASTLE_DIR2_MOVES[i];
            CastlePlies.ALL_CASTLE_DIR_MOVES[3][i] = ALL_CASTLE_DIR3_MOVES[i];
        }
        CastlePlies.verify();
    }
}

