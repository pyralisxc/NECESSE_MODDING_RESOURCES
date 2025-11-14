/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval.pawns.model;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Fields;

public class PawnStructureConstants
extends Fields {
    private static final long WHITE_PASSED_A1 = 0xC0C0C0C0C0C0C0L;
    private static final long WHITE_PASSED_B1 = 0xE0E0E0E0E0E0E0L;
    private static final long WHITE_PASSED_C1 = 0x70707070707070L;
    private static final long WHITE_PASSED_D1 = 0x38383838383838L;
    private static final long WHITE_PASSED_E1 = 0x1C1C1C1C1C1C1CL;
    private static final long WHITE_PASSED_F1 = 0xE0E0E0E0E0E0EL;
    private static final long WHITE_PASSED_G1 = 0x7070707070707L;
    private static final long WHITE_PASSED_H1 = 0x3030303030303L;
    private static final long WHITE_PASSED_A2 = 0xC0C0C0C0C0C0L;
    private static final long WHITE_PASSED_B2 = 0xE0E0E0E0E0E0L;
    private static final long WHITE_PASSED_C2 = 0x707070707070L;
    private static final long WHITE_PASSED_D2 = 0x383838383838L;
    private static final long WHITE_PASSED_E2 = 0x1C1C1C1C1C1CL;
    private static final long WHITE_PASSED_F2 = 0xE0E0E0E0E0EL;
    private static final long WHITE_PASSED_G2 = 0x70707070707L;
    private static final long WHITE_PASSED_H2 = 0x30303030303L;
    private static final long WHITE_PASSED_A3 = 0xC0C0C0C0C0L;
    private static final long WHITE_PASSED_B3 = 0xE0E0E0E0E0L;
    private static final long WHITE_PASSED_C3 = 0x7070707070L;
    private static final long WHITE_PASSED_D3 = 0x3838383838L;
    private static final long WHITE_PASSED_E3 = 0x1C1C1C1C1CL;
    private static final long WHITE_PASSED_F3 = 0xE0E0E0E0EL;
    private static final long WHITE_PASSED_G3 = 0x707070707L;
    private static final long WHITE_PASSED_H3 = 0x303030303L;
    private static final long WHITE_PASSED_A4 = 0xC0C0C0C0L;
    private static final long WHITE_PASSED_B4 = 0xE0E0E0E0L;
    private static final long WHITE_PASSED_C4 = 0x70707070L;
    private static final long WHITE_PASSED_D4 = 0x38383838L;
    private static final long WHITE_PASSED_E4 = 0x1C1C1C1CL;
    private static final long WHITE_PASSED_F4 = 0xE0E0E0EL;
    private static final long WHITE_PASSED_G4 = 0x7070707L;
    private static final long WHITE_PASSED_H4 = 0x3030303L;
    private static final long WHITE_PASSED_A5 = 0xC0C0C0L;
    private static final long WHITE_PASSED_B5 = 0xE0E0E0L;
    private static final long WHITE_PASSED_C5 = 0x707070L;
    private static final long WHITE_PASSED_D5 = 0x383838L;
    private static final long WHITE_PASSED_E5 = 0x1C1C1CL;
    private static final long WHITE_PASSED_F5 = 921102L;
    private static final long WHITE_PASSED_G5 = 460551L;
    private static final long WHITE_PASSED_H5 = 197379L;
    private static final long WHITE_PASSED_A6 = 49344L;
    private static final long WHITE_PASSED_B6 = 57568L;
    private static final long WHITE_PASSED_C6 = 28784L;
    private static final long WHITE_PASSED_D6 = 14392L;
    private static final long WHITE_PASSED_E6 = 7196L;
    private static final long WHITE_PASSED_F6 = 3598L;
    private static final long WHITE_PASSED_G6 = 1799L;
    private static final long WHITE_PASSED_H6 = 771L;
    private static final long WHITE_PASSED_A7 = 192L;
    private static final long WHITE_PASSED_B7 = 224L;
    private static final long WHITE_PASSED_C7 = 112L;
    private static final long WHITE_PASSED_D7 = 56L;
    private static final long WHITE_PASSED_E7 = 28L;
    private static final long WHITE_PASSED_F7 = 14L;
    private static final long WHITE_PASSED_G7 = 7L;
    private static final long WHITE_PASSED_H7 = 3L;
    private static final long WHITE_PASSED_A8 = 0L;
    private static final long WHITE_PASSED_B8 = 0L;
    private static final long WHITE_PASSED_C8 = 0L;
    private static final long WHITE_PASSED_D8 = 0L;
    private static final long WHITE_PASSED_E8 = 0L;
    private static final long WHITE_PASSED_F8 = 0L;
    private static final long WHITE_PASSED_G8 = 0L;
    private static final long WHITE_PASSED_H8 = 0L;
    private static final long[] WHITE_PASSED_ORDERED;
    private static final long BLACK_PASSED_H8 = 0x303030303030300L;
    private static final long BLACK_PASSED_G8 = 0x707070707070700L;
    private static final long BLACK_PASSED_F8 = 0xE0E0E0E0E0E0E00L;
    private static final long BLACK_PASSED_E8 = 0x1C1C1C1C1C1C1C00L;
    private static final long BLACK_PASSED_D8 = 0x3838383838383800L;
    private static final long BLACK_PASSED_C8 = 0x7070707070707000L;
    private static final long BLACK_PASSED_B8 = -2242545357980377088L;
    private static final long BLACK_PASSED_A8 = -4557430888798830592L;
    private static final long BLACK_PASSED_H7 = 0x303030303030000L;
    private static final long BLACK_PASSED_G7 = 0x707070707070000L;
    private static final long BLACK_PASSED_F7 = 0xE0E0E0E0E0E0000L;
    private static final long BLACK_PASSED_E7 = 0x1C1C1C1C1C1C0000L;
    private static final long BLACK_PASSED_D7 = 0x3838383838380000L;
    private static final long BLACK_PASSED_C7 = 0x7070707070700000L;
    private static final long BLACK_PASSED_B7 = -2242545357980434432L;
    private static final long BLACK_PASSED_A7 = -4557430888798879744L;
    private static final long BLACK_PASSED_H6 = 0x303030303000000L;
    private static final long BLACK_PASSED_G6 = 0x707070707000000L;
    private static final long BLACK_PASSED_F6 = 0xE0E0E0E0E000000L;
    private static final long BLACK_PASSED_E6 = 0x1C1C1C1C1C000000L;
    private static final long BLACK_PASSED_D6 = 0x3838383838000000L;
    private static final long BLACK_PASSED_C6 = 0x7070707070000000L;
    private static final long BLACK_PASSED_B6 = -2242545357995114496L;
    private static final long BLACK_PASSED_A6 = -4557430888811462656L;
    private static final long BLACK_PASSED_H5 = 0x303030300000000L;
    private static final long BLACK_PASSED_G5 = 0x707070700000000L;
    private static final long BLACK_PASSED_F5 = 0xE0E0E0E00000000L;
    private static final long BLACK_PASSED_E5 = 0x1C1C1C1C00000000L;
    private static final long BLACK_PASSED_D5 = 0x3838383800000000L;
    private static final long BLACK_PASSED_C5 = 0x7070707000000000L;
    private static final long BLACK_PASSED_B5 = -2242545361753210880L;
    private static final long BLACK_PASSED_A5 = -4557430892032688128L;
    private static final long BLACK_PASSED_H4 = 0x303030000000000L;
    private static final long BLACK_PASSED_G4 = 0x707070000000000L;
    private static final long BLACK_PASSED_F4 = 0xE0E0E0000000000L;
    private static final long BLACK_PASSED_E4 = 0x1C1C1C0000000000L;
    private static final long BLACK_PASSED_D4 = 0x3838380000000000L;
    private static final long BLACK_PASSED_C4 = 0x7070700000000000L;
    private static final long BLACK_PASSED_B4 = -2242546323825885184L;
    private static final long BLACK_PASSED_A4 = -4557431716666408960L;
    private static final long BLACK_PASSED_H3 = 0x303000000000000L;
    private static final long BLACK_PASSED_G3 = 0x707000000000000L;
    private static final long BLACK_PASSED_F3 = 0xE0E000000000000L;
    private static final long BLACK_PASSED_E3 = 0x1C1C000000000000L;
    private static final long BLACK_PASSED_D3 = 0x3838000000000000L;
    private static final long BLACK_PASSED_C3 = 0x7070000000000000L;
    private static final long BLACK_PASSED_B3 = -2242792614430507008L;
    private static final long BLACK_PASSED_A3 = -4557642822898941952L;
    private static final long BLACK_PASSED_H2 = 0x300000000000000L;
    private static final long BLACK_PASSED_G2 = 0x700000000000000L;
    private static final long BLACK_PASSED_F2 = 0xE00000000000000L;
    private static final long BLACK_PASSED_E2 = 0x1C00000000000000L;
    private static final long BLACK_PASSED_D2 = 0x3800000000000000L;
    private static final long BLACK_PASSED_C2 = 0x7000000000000000L;
    private static final long BLACK_PASSED_B2 = -2305843009213693952L;
    private static final long BLACK_PASSED_A2 = -4611686018427387904L;
    private static final long BLACK_PASSED_H1 = 0L;
    private static final long BLACK_PASSED_G1 = 0L;
    private static final long BLACK_PASSED_F1 = 0L;
    private static final long BLACK_PASSED_E1 = 0L;
    private static final long BLACK_PASSED_D1 = 0L;
    private static final long BLACK_PASSED_C1 = 0L;
    private static final long BLACK_PASSED_B1 = 0L;
    private static final long BLACK_PASSED_A1 = 0L;
    private static final long[] BLACK_PASSED_ORDERED;
    private static final long WHITE_BACKWARD_A1 = 0L;
    private static final long WHITE_BACKWARD_B1 = 0L;
    private static final long WHITE_BACKWARD_C1 = 0L;
    private static final long WHITE_BACKWARD_D1 = 0L;
    private static final long WHITE_BACKWARD_E1 = 0L;
    private static final long WHITE_BACKWARD_F1 = 0L;
    private static final long WHITE_BACKWARD_G1 = 0L;
    private static final long WHITE_BACKWARD_H1 = 0L;
    private static final long WHITE_BACKWARD_A2 = 0x4040000000000000L;
    private static final long WHITE_BACKWARD_B2 = -6872493031367376896L;
    private static final long WHITE_BACKWARD_C2 = 0x5050000000000000L;
    private static final long WHITE_BACKWARD_D2 = 0x2828000000000000L;
    private static final long WHITE_BACKWARD_E2 = 0x1414000000000000L;
    private static final long WHITE_BACKWARD_F2 = 0xA0A000000000000L;
    private static final long WHITE_BACKWARD_G2 = 0x505000000000000L;
    private static final long WHITE_BACKWARD_H2 = 0x202000000000000L;
    private static final long WHITE_BACKWARD_A3 = 0x4040400000000000L;
    private static final long WHITE_BACKWARD_B3 = -6872317109506932736L;
    private static final long WHITE_BACKWARD_C3 = 0x5050500000000000L;
    private static final long WHITE_BACKWARD_D3 = 0x2828280000000000L;
    private static final long WHITE_BACKWARD_E3 = 0x1414140000000000L;
    private static final long WHITE_BACKWARD_F3 = 0xA0A0A0000000000L;
    private static final long WHITE_BACKWARD_G3 = 0x505050000000000L;
    private static final long WHITE_BACKWARD_H3 = 0x202020000000000L;
    private static final long WHITE_BACKWARD_A4 = 0x4040404000000000L;
    private static final long WHITE_BACKWARD_B4 = -6872316422312165376L;
    private static final long WHITE_BACKWARD_C4 = 0x5050505000000000L;
    private static final long WHITE_BACKWARD_D4 = 0x2828282800000000L;
    private static final long WHITE_BACKWARD_E4 = 0x1414141400000000L;
    private static final long WHITE_BACKWARD_F4 = 0xA0A0A0A00000000L;
    private static final long WHITE_BACKWARD_G4 = 0x505050500000000L;
    private static final long WHITE_BACKWARD_H4 = 0x202020200000000L;
    private static final long WHITE_BACKWARD_A5 = 0x4040404040000000L;
    private static final long WHITE_BACKWARD_B5 = -6872316419627810816L;
    private static final long WHITE_BACKWARD_C5 = 0x5050505050000000L;
    private static final long WHITE_BACKWARD_D5 = 0x2828282828000000L;
    private static final long WHITE_BACKWARD_E5 = 0x1414141414000000L;
    private static final long WHITE_BACKWARD_F5 = 0xA0A0A0A0A000000L;
    private static final long WHITE_BACKWARD_G5 = 0x505050505000000L;
    private static final long WHITE_BACKWARD_H5 = 0x202020202000000L;
    private static final long WHITE_BACKWARD_A6 = 0x4040404040400000L;
    private static final long WHITE_BACKWARD_B6 = -6872316419617325056L;
    private static final long WHITE_BACKWARD_C6 = 0x5050505050500000L;
    private static final long WHITE_BACKWARD_D6 = 0x2828282828280000L;
    private static final long WHITE_BACKWARD_E6 = 0x1414141414140000L;
    private static final long WHITE_BACKWARD_F6 = 0xA0A0A0A0A0A0000L;
    private static final long WHITE_BACKWARD_G6 = 0x505050505050000L;
    private static final long WHITE_BACKWARD_H6 = 0x202020202020000L;
    private static final long WHITE_BACKWARD_A7 = 0x4040404040404000L;
    private static final long WHITE_BACKWARD_B7 = -6872316419617284096L;
    private static final long WHITE_BACKWARD_C7 = 0x5050505050505000L;
    private static final long WHITE_BACKWARD_D7 = 0x2828282828282800L;
    private static final long WHITE_BACKWARD_E7 = 0x1414141414141400L;
    private static final long WHITE_BACKWARD_F7 = 0xA0A0A0A0A0A0A00L;
    private static final long WHITE_BACKWARD_G7 = 0x505050505050500L;
    private static final long WHITE_BACKWARD_H7 = 0x202020202020200L;
    private static final long WHITE_BACKWARD_A8 = 0L;
    private static final long WHITE_BACKWARD_B8 = 0L;
    private static final long WHITE_BACKWARD_C8 = 0L;
    private static final long WHITE_BACKWARD_D8 = 0L;
    private static final long WHITE_BACKWARD_E8 = 0L;
    private static final long WHITE_BACKWARD_F8 = 0L;
    private static final long WHITE_BACKWARD_G8 = 0L;
    private static final long WHITE_BACKWARD_H8 = 0L;
    private static final long[] WHITE_BACKWARD_ORDERED;
    private static final long BLACK_BACKWARD_A1 = 0L;
    private static final long BLACK_BACKWARD_B1 = 0L;
    private static final long BLACK_BACKWARD_C1 = 0L;
    private static final long BLACK_BACKWARD_D1 = 0L;
    private static final long BLACK_BACKWARD_E1 = 0L;
    private static final long BLACK_BACKWARD_F1 = 0L;
    private static final long BLACK_BACKWARD_G1 = 0L;
    private static final long BLACK_BACKWARD_H1 = 0L;
    private static final long BLACK_BACKWARD_A2 = 0x40404040404040L;
    private static final long BLACK_BACKWARD_B2 = 0xA0A0A0A0A0A0A0L;
    private static final long BLACK_BACKWARD_C2 = 0x50505050505050L;
    private static final long BLACK_BACKWARD_D2 = 0x28282828282828L;
    private static final long BLACK_BACKWARD_E2 = 0x14141414141414L;
    private static final long BLACK_BACKWARD_F2 = 0xA0A0A0A0A0A0AL;
    private static final long BLACK_BACKWARD_G2 = 0x5050505050505L;
    private static final long BLACK_BACKWARD_H2 = 0x2020202020202L;
    private static final long BLACK_BACKWARD_A3 = 0x404040404040L;
    private static final long BLACK_BACKWARD_B3 = 0xA0A0A0A0A0A0L;
    private static final long BLACK_BACKWARD_C3 = 0x505050505050L;
    private static final long BLACK_BACKWARD_D3 = 0x282828282828L;
    private static final long BLACK_BACKWARD_E3 = 0x141414141414L;
    private static final long BLACK_BACKWARD_F3 = 0xA0A0A0A0A0AL;
    private static final long BLACK_BACKWARD_G3 = 0x50505050505L;
    private static final long BLACK_BACKWARD_H3 = 0x20202020202L;
    private static final long BLACK_BACKWARD_A4 = 0x4040404040L;
    private static final long BLACK_BACKWARD_B4 = 0xA0A0A0A0A0L;
    private static final long BLACK_BACKWARD_C4 = 0x5050505050L;
    private static final long BLACK_BACKWARD_D4 = 0x2828282828L;
    private static final long BLACK_BACKWARD_E4 = 0x1414141414L;
    private static final long BLACK_BACKWARD_F4 = 0xA0A0A0A0AL;
    private static final long BLACK_BACKWARD_G4 = 0x505050505L;
    private static final long BLACK_BACKWARD_H4 = 0x202020202L;
    private static final long BLACK_BACKWARD_A5 = 0x40404040L;
    private static final long BLACK_BACKWARD_B5 = 0xA0A0A0A0L;
    private static final long BLACK_BACKWARD_C5 = 0x50505050L;
    private static final long BLACK_BACKWARD_D5 = 0x28282828L;
    private static final long BLACK_BACKWARD_E5 = 0x14141414L;
    private static final long BLACK_BACKWARD_F5 = 0xA0A0A0AL;
    private static final long BLACK_BACKWARD_G5 = 0x5050505L;
    private static final long BLACK_BACKWARD_H5 = 0x2020202L;
    private static final long BLACK_BACKWARD_A6 = 0x404040L;
    private static final long BLACK_BACKWARD_B6 = 0xA0A0A0L;
    private static final long BLACK_BACKWARD_C6 = 0x505050L;
    private static final long BLACK_BACKWARD_D6 = 0x282828L;
    private static final long BLACK_BACKWARD_E6 = 0x141414L;
    private static final long BLACK_BACKWARD_F6 = 657930L;
    private static final long BLACK_BACKWARD_G6 = 328965L;
    private static final long BLACK_BACKWARD_H6 = 131586L;
    private static final long BLACK_BACKWARD_A7 = 16448L;
    private static final long BLACK_BACKWARD_B7 = 41120L;
    private static final long BLACK_BACKWARD_C7 = 20560L;
    private static final long BLACK_BACKWARD_D7 = 10280L;
    private static final long BLACK_BACKWARD_E7 = 5140L;
    private static final long BLACK_BACKWARD_F7 = 2570L;
    private static final long BLACK_BACKWARD_G7 = 1285L;
    private static final long BLACK_BACKWARD_H7 = 514L;
    private static final long BLACK_BACKWARD_A8 = 0L;
    private static final long BLACK_BACKWARD_B8 = 0L;
    private static final long BLACK_BACKWARD_C8 = 0L;
    private static final long BLACK_BACKWARD_D8 = 0L;
    private static final long BLACK_BACKWARD_E8 = 0L;
    private static final long BLACK_BACKWARD_F8 = 0L;
    private static final long BLACK_BACKWARD_G8 = 0L;
    private static final long BLACK_BACKWARD_H8 = 0L;
    private static final long[] BLACK_BACKWARD_ORDERED;
    private static final long WHITE_FRONT_A1 = 0L;
    private static final long WHITE_FRONT_B1 = 0L;
    private static final long WHITE_FRONT_C1 = 0L;
    private static final long WHITE_FRONT_D1 = 0L;
    private static final long WHITE_FRONT_E1 = 0L;
    private static final long WHITE_FRONT_F1 = 0L;
    private static final long WHITE_FRONT_G1 = 0L;
    private static final long WHITE_FRONT_H1 = 0L;
    private static final long WHITE_FRONT_A2 = 0x808080808080L;
    private static final long WHITE_FRONT_B2 = 0x404040404040L;
    private static final long WHITE_FRONT_C2 = 0x202020202020L;
    private static final long WHITE_FRONT_D2 = 0x101010101010L;
    private static final long WHITE_FRONT_E2 = 0x80808080808L;
    private static final long WHITE_FRONT_F2 = 0x40404040404L;
    private static final long WHITE_FRONT_G2 = 0x20202020202L;
    private static final long WHITE_FRONT_H2 = 0x10101010101L;
    private static final long WHITE_FRONT_A3 = 0x8080808080L;
    private static final long WHITE_FRONT_B3 = 0x4040404040L;
    private static final long WHITE_FRONT_C3 = 0x2020202020L;
    private static final long WHITE_FRONT_D3 = 0x1010101010L;
    private static final long WHITE_FRONT_E3 = 0x808080808L;
    private static final long WHITE_FRONT_F3 = 0x404040404L;
    private static final long WHITE_FRONT_G3 = 0x202020202L;
    private static final long WHITE_FRONT_H3 = 0x101010101L;
    private static final long WHITE_FRONT_A4 = 0x80808080L;
    private static final long WHITE_FRONT_B4 = 0x40404040L;
    private static final long WHITE_FRONT_C4 = 0x20202020L;
    private static final long WHITE_FRONT_D4 = 0x10101010L;
    private static final long WHITE_FRONT_E4 = 0x8080808L;
    private static final long WHITE_FRONT_F4 = 0x4040404L;
    private static final long WHITE_FRONT_G4 = 0x2020202L;
    private static final long WHITE_FRONT_H4 = 0x1010101L;
    private static final long WHITE_FRONT_A5 = 0x808080L;
    private static final long WHITE_FRONT_B5 = 0x404040L;
    private static final long WHITE_FRONT_C5 = 0x202020L;
    private static final long WHITE_FRONT_D5 = 0x101010L;
    private static final long WHITE_FRONT_E5 = 526344L;
    private static final long WHITE_FRONT_F5 = 263172L;
    private static final long WHITE_FRONT_G5 = 131586L;
    private static final long WHITE_FRONT_H5 = 65793L;
    private static final long WHITE_FRONT_A6 = 32896L;
    private static final long WHITE_FRONT_B6 = 16448L;
    private static final long WHITE_FRONT_C6 = 8224L;
    private static final long WHITE_FRONT_D6 = 4112L;
    private static final long WHITE_FRONT_E6 = 2056L;
    private static final long WHITE_FRONT_F6 = 1028L;
    private static final long WHITE_FRONT_G6 = 514L;
    private static final long WHITE_FRONT_H6 = 257L;
    private static final long WHITE_FRONT_A7 = 128L;
    private static final long WHITE_FRONT_B7 = 64L;
    private static final long WHITE_FRONT_C7 = 32L;
    private static final long WHITE_FRONT_D7 = 16L;
    private static final long WHITE_FRONT_E7 = 8L;
    private static final long WHITE_FRONT_F7 = 4L;
    private static final long WHITE_FRONT_G7 = 2L;
    private static final long WHITE_FRONT_H7 = 1L;
    private static final long WHITE_FRONT_A8 = 0L;
    private static final long WHITE_FRONT_B8 = 0L;
    private static final long WHITE_FRONT_C8 = 0L;
    private static final long WHITE_FRONT_D8 = 0L;
    private static final long WHITE_FRONT_E8 = 0L;
    private static final long WHITE_FRONT_F8 = 0L;
    private static final long WHITE_FRONT_G8 = 0L;
    private static final long WHITE_FRONT_H8 = 0L;
    private static final long[] WHITE_FRONT_ORDERED;
    private static final long BLACK_FRONT_FULL_A1 = 0L;
    private static final long BLACK_FRONT_FULL_B1 = 0L;
    private static final long BLACK_FRONT_FULL_C1 = 0L;
    private static final long BLACK_FRONT_FULL_D1 = 0L;
    private static final long BLACK_FRONT_FULL_E1 = 0L;
    private static final long BLACK_FRONT_FULL_F1 = 0L;
    private static final long BLACK_FRONT_FULL_G1 = 0L;
    private static final long BLACK_FRONT_FULL_H1 = 0L;
    private static final long BLACK_FRONT_FULL_A2 = Long.MIN_VALUE;
    private static final long BLACK_FRONT_FULL_B2 = 0x4000000000000000L;
    private static final long BLACK_FRONT_FULL_C2 = 0x2000000000000000L;
    private static final long BLACK_FRONT_FULL_D2 = 0x1000000000000000L;
    private static final long BLACK_FRONT_FULL_E2 = 0x800000000000000L;
    private static final long BLACK_FRONT_FULL_F2 = 0x400000000000000L;
    private static final long BLACK_FRONT_FULL_G2 = 0x200000000000000L;
    private static final long BLACK_FRONT_FULL_H2 = 0x100000000000000L;
    private static final long BLACK_FRONT_FULL_A3 = -9187343239835811840L;
    private static final long BLACK_FRONT_FULL_B3 = 0x4040000000000000L;
    private static final long BLACK_FRONT_FULL_C3 = 0x2020000000000000L;
    private static final long BLACK_FRONT_FULL_D3 = 0x1010000000000000L;
    private static final long BLACK_FRONT_FULL_E3 = 0x808000000000000L;
    private static final long BLACK_FRONT_FULL_F3 = 0x404000000000000L;
    private static final long BLACK_FRONT_FULL_G3 = 0x202000000000000L;
    private static final long BLACK_FRONT_FULL_H3 = 0x101000000000000L;
    private static final long BLACK_FRONT_FULL_A4 = -9187202502347456512L;
    private static final long BLACK_FRONT_FULL_B4 = 0x4040400000000000L;
    private static final long BLACK_FRONT_FULL_C4 = 0x2020200000000000L;
    private static final long BLACK_FRONT_FULL_D4 = 0x1010100000000000L;
    private static final long BLACK_FRONT_FULL_E4 = 0x808080000000000L;
    private static final long BLACK_FRONT_FULL_F4 = 0x404040000000000L;
    private static final long BLACK_FRONT_FULL_G4 = 0x202020000000000L;
    private static final long BLACK_FRONT_FULL_H4 = 0x101010000000000L;
    private static final long BLACK_FRONT_FULL_A5 = -9187201952591642624L;
    private static final long BLACK_FRONT_FULL_B5 = 0x4040404000000000L;
    private static final long BLACK_FRONT_FULL_C5 = 0x2020202000000000L;
    private static final long BLACK_FRONT_FULL_D5 = 0x1010101000000000L;
    private static final long BLACK_FRONT_FULL_E5 = 0x808080800000000L;
    private static final long BLACK_FRONT_FULL_F5 = 0x404040400000000L;
    private static final long BLACK_FRONT_FULL_G5 = 0x202020200000000L;
    private static final long BLACK_FRONT_FULL_H5 = 0x101010100000000L;
    private static final long BLACK_FRONT_FULL_A6 = -9187201950444158976L;
    private static final long BLACK_FRONT_FULL_B6 = 0x4040404040000000L;
    private static final long BLACK_FRONT_FULL_C6 = 0x2020202020000000L;
    private static final long BLACK_FRONT_FULL_D6 = 0x1010101010000000L;
    private static final long BLACK_FRONT_FULL_E6 = 0x808080808000000L;
    private static final long BLACK_FRONT_FULL_F6 = 0x404040404000000L;
    private static final long BLACK_FRONT_FULL_G6 = 0x202020202000000L;
    private static final long BLACK_FRONT_FULL_H6 = 0x101010101000000L;
    private static final long BLACK_FRONT_FULL_A7 = -9187201950435770368L;
    private static final long BLACK_FRONT_FULL_B7 = 0x4040404040400000L;
    private static final long BLACK_FRONT_FULL_C7 = 0x2020202020200000L;
    private static final long BLACK_FRONT_FULL_D7 = 0x1010101010100000L;
    private static final long BLACK_FRONT_FULL_E7 = 0x808080808080000L;
    private static final long BLACK_FRONT_FULL_F7 = 0x404040404040000L;
    private static final long BLACK_FRONT_FULL_G7 = 0x202020202020000L;
    private static final long BLACK_FRONT_FULL_H7 = 0x101010101010000L;
    private static final long BLACK_FRONT_FULL_A8 = 0L;
    private static final long BLACK_FRONT_FULL_B8 = 0L;
    private static final long BLACK_FRONT_FULL_C8 = 0L;
    private static final long BLACK_FRONT_FULL_D8 = 0L;
    private static final long BLACK_FRONT_FULL_E8 = 0L;
    private static final long BLACK_FRONT_FULL_F8 = 0L;
    private static final long BLACK_FRONT_FULL_G8 = 0L;
    private static final long BLACK_FRONT_FULL_H8 = 0L;
    private static final long[] BLACK_FRONT_FULL_ORDERED;
    private static final long WHITE_SUPPORT_A1 = 0x4000000000000000L;
    private static final long WHITE_SUPPORT_B1 = -6917529027641081856L;
    private static final long WHITE_SUPPORT_C1 = 0x5000000000000000L;
    private static final long WHITE_SUPPORT_D1 = 0x2800000000000000L;
    private static final long WHITE_SUPPORT_E1 = 0x1400000000000000L;
    private static final long WHITE_SUPPORT_F1 = 0xA00000000000000L;
    private static final long WHITE_SUPPORT_G1 = 0x500000000000000L;
    private static final long WHITE_SUPPORT_H1 = 0x200000000000000L;
    private static final long WHITE_SUPPORT_A2 = 0x4040000000000000L;
    private static final long WHITE_SUPPORT_B2 = -6872493031367376896L;
    private static final long WHITE_SUPPORT_C2 = 0x5050000000000000L;
    private static final long WHITE_SUPPORT_D2 = 0x2828000000000000L;
    private static final long WHITE_SUPPORT_E2 = 0x1414000000000000L;
    private static final long WHITE_SUPPORT_F2 = 0xA0A000000000000L;
    private static final long WHITE_SUPPORT_G2 = 0x505000000000000L;
    private static final long WHITE_SUPPORT_H2 = 0x202000000000000L;
    private static final long WHITE_SUPPORT_A3 = 0x40400000000000L;
    private static final long WHITE_SUPPORT_B3 = 0xA0A00000000000L;
    private static final long WHITE_SUPPORT_C3 = 0x50500000000000L;
    private static final long WHITE_SUPPORT_D3 = 0x28280000000000L;
    private static final long WHITE_SUPPORT_E3 = 0x14140000000000L;
    private static final long WHITE_SUPPORT_F3 = 0xA0A0000000000L;
    private static final long WHITE_SUPPORT_G3 = 0x5050000000000L;
    private static final long WHITE_SUPPORT_H3 = 0x2020000000000L;
    private static final long WHITE_SUPPORT_A4 = 0x404000000000L;
    private static final long WHITE_SUPPORT_B4 = 0xA0A000000000L;
    private static final long WHITE_SUPPORT_C4 = 0x505000000000L;
    private static final long WHITE_SUPPORT_D4 = 0x282800000000L;
    private static final long WHITE_SUPPORT_E4 = 0x141400000000L;
    private static final long WHITE_SUPPORT_F4 = 0xA0A00000000L;
    private static final long WHITE_SUPPORT_G4 = 0x50500000000L;
    private static final long WHITE_SUPPORT_H4 = 0x20200000000L;
    private static final long WHITE_SUPPORT_A5 = 0x4040000000L;
    private static final long WHITE_SUPPORT_B5 = 0xA0A0000000L;
    private static final long WHITE_SUPPORT_C5 = 0x5050000000L;
    private static final long WHITE_SUPPORT_D5 = 0x2828000000L;
    private static final long WHITE_SUPPORT_E5 = 0x1414000000L;
    private static final long WHITE_SUPPORT_F5 = 0xA0A000000L;
    private static final long WHITE_SUPPORT_G5 = 0x505000000L;
    private static final long WHITE_SUPPORT_H5 = 0x202000000L;
    private static final long WHITE_SUPPORT_A6 = 0x40400000L;
    private static final long WHITE_SUPPORT_B6 = 0xA0A00000L;
    private static final long WHITE_SUPPORT_C6 = 0x50500000L;
    private static final long WHITE_SUPPORT_D6 = 0x28280000L;
    private static final long WHITE_SUPPORT_E6 = 0x14140000L;
    private static final long WHITE_SUPPORT_F6 = 0xA0A0000L;
    private static final long WHITE_SUPPORT_G6 = 0x5050000L;
    private static final long WHITE_SUPPORT_H6 = 0x2020000L;
    private static final long WHITE_SUPPORT_A7 = 0x404000L;
    private static final long WHITE_SUPPORT_B7 = 0xA0A000L;
    private static final long WHITE_SUPPORT_C7 = 0x505000L;
    private static final long WHITE_SUPPORT_D7 = 0x282800L;
    private static final long WHITE_SUPPORT_E7 = 0x141400L;
    private static final long WHITE_SUPPORT_F7 = 657920L;
    private static final long WHITE_SUPPORT_G7 = 328960L;
    private static final long WHITE_SUPPORT_H7 = 131584L;
    private static final long WHITE_SUPPORT_A8 = 16448L;
    private static final long WHITE_SUPPORT_B8 = 41120L;
    private static final long WHITE_SUPPORT_C8 = 20560L;
    private static final long WHITE_SUPPORT_D8 = 10280L;
    private static final long WHITE_SUPPORT_E8 = 5140L;
    private static final long WHITE_SUPPORT_F8 = 2570L;
    private static final long WHITE_SUPPORT_G8 = 1285L;
    private static final long WHITE_SUPPORT_H8 = 514L;
    private static final long[] WHITE_SUPPORT_ORDERED;
    private static final long BLACK_SUPPORT_A1 = 0x4040000000000000L;
    private static final long BLACK_SUPPORT_B1 = -6872493031367376896L;
    private static final long BLACK_SUPPORT_C1 = 0x5050000000000000L;
    private static final long BLACK_SUPPORT_D1 = 0x2828000000000000L;
    private static final long BLACK_SUPPORT_E1 = 0x1414000000000000L;
    private static final long BLACK_SUPPORT_F1 = 0xA0A000000000000L;
    private static final long BLACK_SUPPORT_G1 = 0x505000000000000L;
    private static final long BLACK_SUPPORT_H1 = 0x202000000000000L;
    private static final long BLACK_SUPPORT_A2 = 0x40400000000000L;
    private static final long BLACK_SUPPORT_B2 = 0xA0A00000000000L;
    private static final long BLACK_SUPPORT_C2 = 0x50500000000000L;
    private static final long BLACK_SUPPORT_D2 = 0x28280000000000L;
    private static final long BLACK_SUPPORT_E2 = 0x14140000000000L;
    private static final long BLACK_SUPPORT_F2 = 0xA0A0000000000L;
    private static final long BLACK_SUPPORT_G2 = 0x5050000000000L;
    private static final long BLACK_SUPPORT_H2 = 0x2020000000000L;
    private static final long BLACK_SUPPORT_A3 = 0x404000000000L;
    private static final long BLACK_SUPPORT_B3 = 0xA0A000000000L;
    private static final long BLACK_SUPPORT_C3 = 0x505000000000L;
    private static final long BLACK_SUPPORT_D3 = 0x282800000000L;
    private static final long BLACK_SUPPORT_E3 = 0x141400000000L;
    private static final long BLACK_SUPPORT_F3 = 0xA0A00000000L;
    private static final long BLACK_SUPPORT_G3 = 0x50500000000L;
    private static final long BLACK_SUPPORT_H3 = 0x20200000000L;
    private static final long BLACK_SUPPORT_A4 = 0x4040000000L;
    private static final long BLACK_SUPPORT_B4 = 0xA0A0000000L;
    private static final long BLACK_SUPPORT_C4 = 0x5050000000L;
    private static final long BLACK_SUPPORT_D4 = 0x2828000000L;
    private static final long BLACK_SUPPORT_E4 = 0x1414000000L;
    private static final long BLACK_SUPPORT_F4 = 0xA0A000000L;
    private static final long BLACK_SUPPORT_G4 = 0x505000000L;
    private static final long BLACK_SUPPORT_H4 = 0x202000000L;
    private static final long BLACK_SUPPORT_A5 = 0x40400000L;
    private static final long BLACK_SUPPORT_B5 = 0xA0A00000L;
    private static final long BLACK_SUPPORT_C5 = 0x50500000L;
    private static final long BLACK_SUPPORT_D5 = 0x28280000L;
    private static final long BLACK_SUPPORT_E5 = 0x14140000L;
    private static final long BLACK_SUPPORT_F5 = 0xA0A0000L;
    private static final long BLACK_SUPPORT_G5 = 0x5050000L;
    private static final long BLACK_SUPPORT_H5 = 0x2020000L;
    private static final long BLACK_SUPPORT_A6 = 0x404000L;
    private static final long BLACK_SUPPORT_B6 = 0xA0A000L;
    private static final long BLACK_SUPPORT_C6 = 0x505000L;
    private static final long BLACK_SUPPORT_D6 = 0x282800L;
    private static final long BLACK_SUPPORT_E6 = 0x141400L;
    private static final long BLACK_SUPPORT_F6 = 657920L;
    private static final long BLACK_SUPPORT_G6 = 328960L;
    private static final long BLACK_SUPPORT_H6 = 131584L;
    private static final long BLACK_SUPPORT_A7 = 16448L;
    private static final long BLACK_SUPPORT_B7 = 41120L;
    private static final long BLACK_SUPPORT_C7 = 20560L;
    private static final long BLACK_SUPPORT_D7 = 10280L;
    private static final long BLACK_SUPPORT_E7 = 5140L;
    private static final long BLACK_SUPPORT_F7 = 2570L;
    private static final long BLACK_SUPPORT_G7 = 1285L;
    private static final long BLACK_SUPPORT_H7 = 514L;
    private static final long BLACK_SUPPORT_A8 = 64L;
    private static final long BLACK_SUPPORT_B8 = 160L;
    private static final long BLACK_SUPPORT_C8 = 80L;
    private static final long BLACK_SUPPORT_D8 = 40L;
    private static final long BLACK_SUPPORT_E8 = 20L;
    private static final long BLACK_SUPPORT_F8 = 10L;
    private static final long BLACK_SUPPORT_G8 = 5L;
    private static final long BLACK_SUPPORT_H8 = 2L;
    private static final long[] BLACK_SUPPORT_ORDERED;
    private static final long WHITE_POSSIBLE_ATTACKS_A1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_B1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_D1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_E1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_F1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_G1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_H1 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_A2 = 0x404040404040L;
    private static final long WHITE_POSSIBLE_ATTACKS_B2 = 0xA0A0A0A0A0A0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C2 = 0x505050505050L;
    private static final long WHITE_POSSIBLE_ATTACKS_D2 = 0x282828282828L;
    private static final long WHITE_POSSIBLE_ATTACKS_E2 = 0x141414141414L;
    private static final long WHITE_POSSIBLE_ATTACKS_F2 = 0xA0A0A0A0A0AL;
    private static final long WHITE_POSSIBLE_ATTACKS_G2 = 0x50505050505L;
    private static final long WHITE_POSSIBLE_ATTACKS_H2 = 0x20202020202L;
    private static final long WHITE_POSSIBLE_ATTACKS_A3 = 0x4040404040L;
    private static final long WHITE_POSSIBLE_ATTACKS_B3 = 0xA0A0A0A0A0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C3 = 0x5050505050L;
    private static final long WHITE_POSSIBLE_ATTACKS_D3 = 0x2828282828L;
    private static final long WHITE_POSSIBLE_ATTACKS_E3 = 0x1414141414L;
    private static final long WHITE_POSSIBLE_ATTACKS_F3 = 0xA0A0A0A0AL;
    private static final long WHITE_POSSIBLE_ATTACKS_G3 = 0x505050505L;
    private static final long WHITE_POSSIBLE_ATTACKS_H3 = 0x202020202L;
    private static final long WHITE_POSSIBLE_ATTACKS_A4 = 0x40404040L;
    private static final long WHITE_POSSIBLE_ATTACKS_B4 = 0xA0A0A0A0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C4 = 0x50505050L;
    private static final long WHITE_POSSIBLE_ATTACKS_D4 = 0x28282828L;
    private static final long WHITE_POSSIBLE_ATTACKS_E4 = 0x14141414L;
    private static final long WHITE_POSSIBLE_ATTACKS_F4 = 0xA0A0A0AL;
    private static final long WHITE_POSSIBLE_ATTACKS_G4 = 0x5050505L;
    private static final long WHITE_POSSIBLE_ATTACKS_H4 = 0x2020202L;
    private static final long WHITE_POSSIBLE_ATTACKS_A5 = 0x404040L;
    private static final long WHITE_POSSIBLE_ATTACKS_B5 = 0xA0A0A0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C5 = 0x505050L;
    private static final long WHITE_POSSIBLE_ATTACKS_D5 = 0x282828L;
    private static final long WHITE_POSSIBLE_ATTACKS_E5 = 0x141414L;
    private static final long WHITE_POSSIBLE_ATTACKS_F5 = 657930L;
    private static final long WHITE_POSSIBLE_ATTACKS_G5 = 328965L;
    private static final long WHITE_POSSIBLE_ATTACKS_H5 = 131586L;
    private static final long WHITE_POSSIBLE_ATTACKS_A6 = 16448L;
    private static final long WHITE_POSSIBLE_ATTACKS_B6 = 41120L;
    private static final long WHITE_POSSIBLE_ATTACKS_C6 = 20560L;
    private static final long WHITE_POSSIBLE_ATTACKS_D6 = 10280L;
    private static final long WHITE_POSSIBLE_ATTACKS_E6 = 5140L;
    private static final long WHITE_POSSIBLE_ATTACKS_F6 = 2570L;
    private static final long WHITE_POSSIBLE_ATTACKS_G6 = 1285L;
    private static final long WHITE_POSSIBLE_ATTACKS_H6 = 514L;
    private static final long WHITE_POSSIBLE_ATTACKS_A7 = 64L;
    private static final long WHITE_POSSIBLE_ATTACKS_B7 = 160L;
    private static final long WHITE_POSSIBLE_ATTACKS_C7 = 80L;
    private static final long WHITE_POSSIBLE_ATTACKS_D7 = 40L;
    private static final long WHITE_POSSIBLE_ATTACKS_E7 = 20L;
    private static final long WHITE_POSSIBLE_ATTACKS_F7 = 10L;
    private static final long WHITE_POSSIBLE_ATTACKS_G7 = 5L;
    private static final long WHITE_POSSIBLE_ATTACKS_H7 = 2L;
    private static final long WHITE_POSSIBLE_ATTACKS_A8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_B8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_C8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_D8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_E8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_F8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_G8 = 0L;
    private static final long WHITE_POSSIBLE_ATTACKS_H8 = 0L;
    private static final long[] WHITE_POSSIBLE_ATTACKS_ORDERED;
    private static final long BLACK_POSSIBLE_ATTACKS_A1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_B1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_C1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_D1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_E1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_F1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_G1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_H1 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_A2 = 0x4000000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B2 = -6917529027641081856L;
    private static final long BLACK_POSSIBLE_ATTACKS_C2 = 0x5000000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D2 = 0x2800000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E2 = 0x1400000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F2 = 0xA00000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G2 = 0x500000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H2 = 0x200000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A3 = 0x4040000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B3 = -6872493031367376896L;
    private static final long BLACK_POSSIBLE_ATTACKS_C3 = 0x5050000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D3 = 0x2828000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E3 = 0x1414000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F3 = 0xA0A000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G3 = 0x505000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H3 = 0x202000000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A4 = 0x4040400000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B4 = -6872317109506932736L;
    private static final long BLACK_POSSIBLE_ATTACKS_C4 = 0x5050500000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D4 = 0x2828280000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E4 = 0x1414140000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F4 = 0xA0A0A0000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G4 = 0x505050000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H4 = 0x202020000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A5 = 0x4040404000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B5 = -6872316422312165376L;
    private static final long BLACK_POSSIBLE_ATTACKS_C5 = 0x5050505000000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D5 = 0x2828282800000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E5 = 0x1414141400000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F5 = 0xA0A0A0A00000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G5 = 0x505050500000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H5 = 0x202020200000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A6 = 0x4040404040000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B6 = -6872316419627810816L;
    private static final long BLACK_POSSIBLE_ATTACKS_C6 = 0x5050505050000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D6 = 0x2828282828000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E6 = 0x1414141414000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F6 = 0xA0A0A0A0A000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G6 = 0x505050505000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H6 = 0x202020202000000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A7 = 0x4040404040400000L;
    private static final long BLACK_POSSIBLE_ATTACKS_B7 = -6872316419617325056L;
    private static final long BLACK_POSSIBLE_ATTACKS_C7 = 0x5050505050500000L;
    private static final long BLACK_POSSIBLE_ATTACKS_D7 = 0x2828282828280000L;
    private static final long BLACK_POSSIBLE_ATTACKS_E7 = 0x1414141414140000L;
    private static final long BLACK_POSSIBLE_ATTACKS_F7 = 0xA0A0A0A0A0A0000L;
    private static final long BLACK_POSSIBLE_ATTACKS_G7 = 0x505050505050000L;
    private static final long BLACK_POSSIBLE_ATTACKS_H7 = 0x202020202020000L;
    private static final long BLACK_POSSIBLE_ATTACKS_A8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_B8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_C8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_D8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_E8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_F8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_G8 = 0L;
    private static final long BLACK_POSSIBLE_ATTACKS_H8 = 0L;
    private static final long[] BLACK_POSSIBLE_ATTACKS_ORDERED;
    private static final long WHITE_PASSER_PARAM_A1 = -1L;
    private static final long WHITE_PASSER_PARAM_B1 = -1L;
    private static final long WHITE_PASSER_PARAM_C1 = -1L;
    private static final long WHITE_PASSER_PARAM_D1 = -1L;
    private static final long WHITE_PASSER_PARAM_E1 = -1L;
    private static final long WHITE_PASSER_PARAM_F1 = -1L;
    private static final long WHITE_PASSER_PARAM_G1 = -1L;
    private static final long WHITE_PASSER_PARAM_H1 = -1L;
    private static final long WHITE_PASSER_PARAM_A2 = 0xFEFEFEFEFEFEFEL;
    private static final long WHITE_PASSER_PARAM_B2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_C2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_D2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_E2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_F2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_G2 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_H2 = 0x7F7F7F7F7F7F7FL;
    private static final long WHITE_PASSER_PARAM_A3 = 0xFCFCFCFCFCFCL;
    private static final long WHITE_PASSER_PARAM_B3 = 0xFEFEFEFEFEFEL;
    private static final long WHITE_PASSER_PARAM_C3 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_D3 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_E3 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_F3 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_G3 = 0x7F7F7F7F7F7FL;
    private static final long WHITE_PASSER_PARAM_H3 = 0x3F3F3F3F3F3FL;
    private static final long WHITE_PASSER_PARAM_A4 = 0xF8F8F8F8F8L;
    private static final long WHITE_PASSER_PARAM_B4 = 0xFCFCFCFCFCL;
    private static final long WHITE_PASSER_PARAM_C4 = 0xFEFEFEFEFEL;
    private static final long WHITE_PASSER_PARAM_D4 = 0xFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_E4 = 0xFFFFFFFFFFL;
    private static final long WHITE_PASSER_PARAM_F4 = 0x7F7F7F7F7FL;
    private static final long WHITE_PASSER_PARAM_G4 = 0x3F3F3F3F3FL;
    private static final long WHITE_PASSER_PARAM_H4 = 0x1F1F1F1F1FL;
    private static final long WHITE_PASSER_PARAM_A5 = 0xF0F0F0F0L;
    private static final long WHITE_PASSER_PARAM_B5 = 0xF8F8F8F8L;
    private static final long WHITE_PASSER_PARAM_C5 = 0xFCFCFCFCL;
    private static final long WHITE_PASSER_PARAM_D5 = 0xFEFEFEFEL;
    private static final long WHITE_PASSER_PARAM_E5 = 0x7F7F7F7FL;
    private static final long WHITE_PASSER_PARAM_F5 = 0x3F3F3F3FL;
    private static final long WHITE_PASSER_PARAM_G5 = 0x1F1F1F1FL;
    private static final long WHITE_PASSER_PARAM_H5 = 0xF0F0F0FL;
    private static final long WHITE_PASSER_PARAM_A6 = 0xE0E0E0L;
    private static final long WHITE_PASSER_PARAM_B6 = 0xF0F0F0L;
    private static final long WHITE_PASSER_PARAM_C6 = 0xF8F8F8L;
    private static final long WHITE_PASSER_PARAM_D6 = 0x7C7C7CL;
    private static final long WHITE_PASSER_PARAM_E6 = 0x3E3E3EL;
    private static final long WHITE_PASSER_PARAM_F6 = 0x1F1F1FL;
    private static final long WHITE_PASSER_PARAM_G6 = 986895L;
    private static final long WHITE_PASSER_PARAM_H6 = 460551L;
    private static final long WHITE_PASSER_PARAM_A7 = 49344L;
    private static final long WHITE_PASSER_PARAM_B7 = 57568L;
    private static final long WHITE_PASSER_PARAM_C7 = 28784L;
    private static final long WHITE_PASSER_PARAM_D7 = 14392L;
    private static final long WHITE_PASSER_PARAM_E7 = 7196L;
    private static final long WHITE_PASSER_PARAM_F7 = 3598L;
    private static final long WHITE_PASSER_PARAM_G7 = 1799L;
    private static final long WHITE_PASSER_PARAM_H7 = 771L;
    private static final long WHITE_PASSER_PARAM_A8 = 128L;
    private static final long WHITE_PASSER_PARAM_B8 = 64L;
    private static final long WHITE_PASSER_PARAM_C8 = 32L;
    private static final long WHITE_PASSER_PARAM_D8 = 16L;
    private static final long WHITE_PASSER_PARAM_E8 = 8L;
    private static final long WHITE_PASSER_PARAM_F8 = 4L;
    private static final long WHITE_PASSER_PARAM_G8 = 2L;
    private static final long WHITE_PASSER_PARAM_H8 = 1L;
    private static final long[] WHITE_PASSER_PARAM_ORDERED;
    private static final long BLACK_PASSER_PARAM_A1 = Long.MIN_VALUE;
    private static final long BLACK_PASSER_PARAM_B1 = 0x4000000000000000L;
    private static final long BLACK_PASSER_PARAM_C1 = 0x2000000000000000L;
    private static final long BLACK_PASSER_PARAM_D1 = 0x1000000000000000L;
    private static final long BLACK_PASSER_PARAM_E1 = 0x800000000000000L;
    private static final long BLACK_PASSER_PARAM_F1 = 0x400000000000000L;
    private static final long BLACK_PASSER_PARAM_G1 = 0x200000000000000L;
    private static final long BLACK_PASSER_PARAM_H1 = 0x100000000000000L;
    private static final long BLACK_PASSER_PARAM_A2 = -4557642822898941952L;
    private static final long BLACK_PASSER_PARAM_B2 = -2242792614430507008L;
    private static final long BLACK_PASSER_PARAM_C2 = 0x7070000000000000L;
    private static final long BLACK_PASSER_PARAM_D2 = 0x3838000000000000L;
    private static final long BLACK_PASSER_PARAM_E2 = 0x1C1C000000000000L;
    private static final long BLACK_PASSER_PARAM_F2 = 0xE0E000000000000L;
    private static final long BLACK_PASSER_PARAM_G2 = 0x707000000000000L;
    private static final long BLACK_PASSER_PARAM_H2 = 0x303000000000000L;
    private static final long BLACK_PASSER_PARAM_A3 = -2242546323825885184L;
    private static final long BLACK_PASSER_PARAM_B3 = -1085103627405623296L;
    private static final long BLACK_PASSER_PARAM_C3 = -506382279195492352L;
    private static final long BLACK_PASSER_PARAM_D3 = 0x7C7C7C0000000000L;
    private static final long BLACK_PASSER_PARAM_E3 = 0x3E3E3E0000000000L;
    private static final long BLACK_PASSER_PARAM_F3 = 0x1F1F1F0000000000L;
    private static final long BLACK_PASSER_PARAM_G3 = 0xF0F0F0000000000L;
    private static final long BLACK_PASSER_PARAM_H3 = 0x707070000000000L;
    private static final long BLACK_PASSER_PARAM_A4 = -1085102596613472256L;
    private static final long BLACK_PASSER_PARAM_B4 = -506381214043602944L;
    private static final long BLACK_PASSER_PARAM_C4 = -217020522758668288L;
    private static final long BLACK_PASSER_PARAM_D4 = -72340177116200960L;
    private static final long BLACK_PASSER_PARAM_E4 = 0x7F7F7F7F00000000L;
    private static final long BLACK_PASSER_PARAM_F4 = 0x3F3F3F3F00000000L;
    private static final long BLACK_PASSER_PARAM_G4 = 0x1F1F1F1F00000000L;
    private static final long BLACK_PASSER_PARAM_H4 = 0xF0F0F0F00000000L;
    private static final long BLACK_PASSER_PARAM_A5 = -506381209882853376L;
    private static final long BLACK_PASSER_PARAM_B5 = -217020518530809856L;
    private static final long BLACK_PASSER_PARAM_C5 = -72340172854788096L;
    private static final long BLACK_PASSER_PARAM_D5 = -16777216L;
    private static final long BLACK_PASSER_PARAM_E5 = -16777216L;
    private static final long BLACK_PASSER_PARAM_F5 = 0x7F7F7F7F7F000000L;
    private static final long BLACK_PASSER_PARAM_G5 = 0x3F3F3F3F3F000000L;
    private static final long BLACK_PASSER_PARAM_H5 = 0x1F1F1F1F1F000000L;
    private static final long BLACK_PASSER_PARAM_A6 = -217020518514294784L;
    private static final long BLACK_PASSER_PARAM_B6 = -72340172838141952L;
    private static final long BLACK_PASSER_PARAM_C6 = -65536L;
    private static final long BLACK_PASSER_PARAM_D6 = -65536L;
    private static final long BLACK_PASSER_PARAM_E6 = -65536L;
    private static final long BLACK_PASSER_PARAM_F6 = -65536L;
    private static final long BLACK_PASSER_PARAM_G6 = 0x7F7F7F7F7F7F0000L;
    private static final long BLACK_PASSER_PARAM_H6 = 0x3F3F3F3F3F3F0000L;
    private static final long BLACK_PASSER_PARAM_A7 = -72340172838076928L;
    private static final long BLACK_PASSER_PARAM_B7 = -256L;
    private static final long BLACK_PASSER_PARAM_C7 = -256L;
    private static final long BLACK_PASSER_PARAM_D7 = -256L;
    private static final long BLACK_PASSER_PARAM_E7 = -256L;
    private static final long BLACK_PASSER_PARAM_F7 = -256L;
    private static final long BLACK_PASSER_PARAM_G7 = -256L;
    private static final long BLACK_PASSER_PARAM_H7 = 0x7F7F7F7F7F7F7F00L;
    private static final long BLACK_PASSER_PARAM_A8 = -1L;
    private static final long BLACK_PASSER_PARAM_B8 = -1L;
    private static final long BLACK_PASSER_PARAM_C8 = -1L;
    private static final long BLACK_PASSER_PARAM_D8 = -1L;
    private static final long BLACK_PASSER_PARAM_E8 = -1L;
    private static final long BLACK_PASSER_PARAM_F8 = -1L;
    private static final long BLACK_PASSER_PARAM_G8 = -1L;
    private static final long BLACK_PASSER_PARAM_H8 = -1L;
    private static final long[] BLACK_PASSER_PARAM_ORDERED;
    private static final long WHITE_PASSER_EXT_PARAM_A1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_B1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_C1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_D1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_E1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_F1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_G1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_H1 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_A2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_B2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_C2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_D2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_E2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_F2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_G2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_H2 = -1L;
    private static final long WHITE_PASSER_EXT_PARAM_A3 = 0xFEFEFEFEFEFEFEL;
    private static final long WHITE_PASSER_EXT_PARAM_B3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_C3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_D3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_E3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_F3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_G3 = 0xFFFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_H3 = 0x7F7F7F7F7F7F7FL;
    private static final long WHITE_PASSER_EXT_PARAM_A4 = 0xFCFCFCFCFCFCL;
    private static final long WHITE_PASSER_EXT_PARAM_B4 = 0xFEFEFEFEFEFEL;
    private static final long WHITE_PASSER_EXT_PARAM_C4 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_D4 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_E4 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_F4 = 0xFFFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_G4 = 0x7F7F7F7F7F7FL;
    private static final long WHITE_PASSER_EXT_PARAM_H4 = 0x3F3F3F3F3F3FL;
    private static final long WHITE_PASSER_EXT_PARAM_A5 = 0xF8F8F8F8F8L;
    private static final long WHITE_PASSER_EXT_PARAM_B5 = 0xFCFCFCFCFCL;
    private static final long WHITE_PASSER_EXT_PARAM_C5 = 0xFEFEFEFEFEL;
    private static final long WHITE_PASSER_EXT_PARAM_D5 = 0xFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_E5 = 0xFFFFFFFFFFL;
    private static final long WHITE_PASSER_EXT_PARAM_F5 = 0x7F7F7F7F7FL;
    private static final long WHITE_PASSER_EXT_PARAM_G5 = 0x3F3F3F3F3FL;
    private static final long WHITE_PASSER_EXT_PARAM_H5 = 0x1F1F1F1F1FL;
    private static final long WHITE_PASSER_EXT_PARAM_A6 = 0xF0F0F0F0L;
    private static final long WHITE_PASSER_EXT_PARAM_B6 = 0xF8F8F8F8L;
    private static final long WHITE_PASSER_EXT_PARAM_C6 = 0xFCFCFCFCL;
    private static final long WHITE_PASSER_EXT_PARAM_D6 = 0xFEFEFEFEL;
    private static final long WHITE_PASSER_EXT_PARAM_E6 = 0x7F7F7F7FL;
    private static final long WHITE_PASSER_EXT_PARAM_F6 = 0x3F3F3F3FL;
    private static final long WHITE_PASSER_EXT_PARAM_G6 = 0x1F1F1F1FL;
    private static final long WHITE_PASSER_EXT_PARAM_H6 = 0xF0F0F0FL;
    private static final long WHITE_PASSER_EXT_PARAM_A7 = 0xE0E0E0L;
    private static final long WHITE_PASSER_EXT_PARAM_B7 = 0xF0F0F0L;
    private static final long WHITE_PASSER_EXT_PARAM_C7 = 0xF8F8F8L;
    private static final long WHITE_PASSER_EXT_PARAM_D7 = 0x7C7C7CL;
    private static final long WHITE_PASSER_EXT_PARAM_E7 = 0x3E3E3EL;
    private static final long WHITE_PASSER_EXT_PARAM_F7 = 0x1F1F1FL;
    private static final long WHITE_PASSER_EXT_PARAM_G7 = 986895L;
    private static final long WHITE_PASSER_EXT_PARAM_H7 = 460551L;
    private static final long WHITE_PASSER_EXT_PARAM_A8 = 49344L;
    private static final long WHITE_PASSER_EXT_PARAM_B8 = 57568L;
    private static final long WHITE_PASSER_EXT_PARAM_C8 = 28784L;
    private static final long WHITE_PASSER_EXT_PARAM_D8 = 14392L;
    private static final long WHITE_PASSER_EXT_PARAM_E8 = 7196L;
    private static final long WHITE_PASSER_EXT_PARAM_F8 = 3598L;
    private static final long WHITE_PASSER_EXT_PARAM_G8 = 1799L;
    private static final long WHITE_PASSER_EXT_PARAM_H8 = 771L;
    private static final long[] WHITE_PASSER_EXT_PARAM_ORDERED;
    private static final long BLACK_PASSER_EXT_PARAM_A1 = -4557642822898941952L;
    private static final long BLACK_PASSER_EXT_PARAM_B1 = -2242792614430507008L;
    private static final long BLACK_PASSER_EXT_PARAM_C1 = 0x7070000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_D1 = 0x3838000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_E1 = 0x1C1C000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_F1 = 0xE0E000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_G1 = 0x707000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_H1 = 0x303000000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_A2 = -2242546323825885184L;
    private static final long BLACK_PASSER_EXT_PARAM_B2 = -1085103627405623296L;
    private static final long BLACK_PASSER_EXT_PARAM_C2 = -506382279195492352L;
    private static final long BLACK_PASSER_EXT_PARAM_D2 = 0x7C7C7C0000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_E2 = 0x3E3E3E0000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_F2 = 0x1F1F1F0000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_G2 = 0xF0F0F0000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_H2 = 0x707070000000000L;
    private static final long BLACK_PASSER_EXT_PARAM_A3 = -1085102596613472256L;
    private static final long BLACK_PASSER_EXT_PARAM_B3 = -506381214043602944L;
    private static final long BLACK_PASSER_EXT_PARAM_C3 = -217020522758668288L;
    private static final long BLACK_PASSER_EXT_PARAM_D3 = -72340177116200960L;
    private static final long BLACK_PASSER_EXT_PARAM_E3 = 0x7F7F7F7F00000000L;
    private static final long BLACK_PASSER_EXT_PARAM_F3 = 0x3F3F3F3F00000000L;
    private static final long BLACK_PASSER_EXT_PARAM_G3 = 0x1F1F1F1F00000000L;
    private static final long BLACK_PASSER_EXT_PARAM_H3 = 0xF0F0F0F00000000L;
    private static final long BLACK_PASSER_EXT_PARAM_A4 = -506381209882853376L;
    private static final long BLACK_PASSER_EXT_PARAM_B4 = -217020518530809856L;
    private static final long BLACK_PASSER_EXT_PARAM_C4 = -72340172854788096L;
    private static final long BLACK_PASSER_EXT_PARAM_D4 = -16777216L;
    private static final long BLACK_PASSER_EXT_PARAM_E4 = -16777216L;
    private static final long BLACK_PASSER_EXT_PARAM_F4 = 0x7F7F7F7F7F000000L;
    private static final long BLACK_PASSER_EXT_PARAM_G4 = 0x3F3F3F3F3F000000L;
    private static final long BLACK_PASSER_EXT_PARAM_H4 = 0x1F1F1F1F1F000000L;
    private static final long BLACK_PASSER_EXT_PARAM_A5 = -217020518514294784L;
    private static final long BLACK_PASSER_EXT_PARAM_B5 = -72340172838141952L;
    private static final long BLACK_PASSER_EXT_PARAM_C5 = -65536L;
    private static final long BLACK_PASSER_EXT_PARAM_D5 = -65536L;
    private static final long BLACK_PASSER_EXT_PARAM_E5 = -65536L;
    private static final long BLACK_PASSER_EXT_PARAM_F5 = -65536L;
    private static final long BLACK_PASSER_EXT_PARAM_G5 = 0x7F7F7F7F7F7F0000L;
    private static final long BLACK_PASSER_EXT_PARAM_H5 = 0x3F3F3F3F3F3F0000L;
    private static final long BLACK_PASSER_EXT_PARAM_A6 = -72340172838076928L;
    private static final long BLACK_PASSER_EXT_PARAM_B6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_C6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_D6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_E6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_F6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_G6 = -256L;
    private static final long BLACK_PASSER_EXT_PARAM_H6 = 0x7F7F7F7F7F7F7F00L;
    private static final long BLACK_PASSER_EXT_PARAM_A7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_B7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_C7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_D7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_E7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_F7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_G7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_H7 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_A8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_B8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_C8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_D8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_E8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_F8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_G8 = -1L;
    private static final long BLACK_PASSER_EXT_PARAM_H8 = -1L;
    private static final long[] BLACK_PASSER_EXT_PARAM_ORDERED;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H1 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A2 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A2 = 0x40000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B2 = 0x80000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B2 = 0x20000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C2 = 0x40000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C2 = 0x10000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D2 = 0x20000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D2 = 0x8000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E2 = 0x10000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E2 = 0x4000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F2 = 0x8000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F2 = 0x2000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G2 = 0x4000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G2 = 0x1000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H2 = 0x2000000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H2 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A3 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A3 = 0x40400000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B3 = 0x80800000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B3 = 0x20200000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C3 = 0x40400000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C3 = 0x10100000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D3 = 0x20200000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D3 = 0x8080000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E3 = 0x10100000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E3 = 0x4040000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F3 = 0x8080000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F3 = 0x2020000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G3 = 0x4040000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G3 = 0x1010000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H3 = 0x2020000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H3 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A4 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A4 = 0x40404000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B4 = 0x80808000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B4 = 0x20202000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C4 = 0x40404000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C4 = 0x10101000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D4 = 0x20202000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D4 = 0x8080800000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E4 = 0x10101000000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E4 = 0x4040400000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F4 = 0x8080800000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F4 = 0x2020200000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G4 = 0x4040400000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G4 = 0x1010100000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H4 = 0x2020200000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H4 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A5 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A5 = 0x40404040000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B5 = 0x80808080000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B5 = 0x20202020000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C5 = 0x40404040000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C5 = 0x10101010000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D5 = 0x20202020000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D5 = 0x8080808000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E5 = 0x10101010000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E5 = 0x4040404000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F5 = 0x8080808000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F5 = 0x2020202000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G5 = 0x4040404000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G5 = 0x1010101000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H5 = 0x2020202000000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H5 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A6 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A6 = 0x4040400000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B6 = 0x8080800000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B6 = 0x2020200000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C6 = 0x4040400000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C6 = 0x1010100000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D6 = 0x2020200000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D6 = 0x808080000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E6 = 0x1010100000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E6 = 0x404040000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F6 = 0x808080000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F6 = 0x202020000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G6 = 0x404040000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G6 = 0x101010000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H6 = 0x202020000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H6 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A7 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A7 = 0x40404000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B7 = 0x80808000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B7 = 0x20202000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C7 = 0x40404000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C7 = 0x10101000L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D7 = 0x20202000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D7 = 0x8080800L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E7 = 0x10101000L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E7 = 0x4040400L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F7 = 0x8080800L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F7 = 0x2020200L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G7 = 0x4040400L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G7 = 0x1010100L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H7 = 0x2020200L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H7 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_A8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_A8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_B8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_B8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_C8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_C8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_D8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_D8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_E8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_E8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_F8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_F8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_G8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_G8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_LEFT_H8 = 0L;
    private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_H8 = 0L;
    private static final long[] WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED;
    private static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H1 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A2 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A2 = 0x40404000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B2 = 0x80808000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B2 = 0x20202000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C2 = 0x40404000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C2 = 0x10101000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D2 = 0x20202000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D2 = 0x8080800000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E2 = 0x10101000000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E2 = 0x4040400000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F2 = 0x8080800000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F2 = 0x2020200000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G2 = 0x4040400000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G2 = 0x1010100000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H2 = 0x2020200000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H2 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A3 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A3 = 0x404040000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B3 = 0x808080000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B3 = 0x202020000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C3 = 0x404040000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C3 = 0x101010000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D3 = 0x202020000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D3 = 0x80808000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E3 = 0x101010000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E3 = 0x40404000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F3 = 0x80808000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F3 = 0x20202000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G3 = 0x40404000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G3 = 0x10101000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H3 = 0x20202000000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H3 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A4 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A4 = 0x4040404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B4 = 0x8080808000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B4 = 0x2020202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C4 = 0x4040404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C4 = 0x1010101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D4 = 0x2020202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D4 = 0x808080800L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E4 = 0x1010101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E4 = 0x404040400L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F4 = 0x808080800L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F4 = 0x202020200L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G4 = 0x404040400L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G4 = 0x101010100L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H4 = 0x202020200L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H4 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A5 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A5 = 0x40404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B5 = 0x80808000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B5 = 0x20202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C5 = 0x40404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C5 = 0x10101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D5 = 0x20202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D5 = 0x8080800L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E5 = 0x10101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E5 = 0x4040400L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F5 = 0x8080800L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F5 = 0x2020200L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G5 = 0x4040400L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G5 = 0x1010100L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H5 = 0x2020200L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H5 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A6 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A6 = 0x404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B6 = 0x808000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B6 = 0x202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C6 = 0x404000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C6 = 0x101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D6 = 0x202000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D6 = 526336L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E6 = 0x101000L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E6 = 263168L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F6 = 526336L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F6 = 131584L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G6 = 263168L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G6 = 65792L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H6 = 131584L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H6 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A7 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A7 = 16384L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B7 = 32768L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B7 = 8192L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C7 = 16384L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C7 = 4096L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D7 = 8192L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D7 = 2048L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E7 = 4096L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E7 = 1024L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F7 = 2048L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F7 = 512L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G7 = 1024L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G7 = 256L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H7 = 512L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H7 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_A8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_A8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_B8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_B8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_C8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_C8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_D8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_D8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_E8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_E8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_F8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_F8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_G8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_G8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_LEFT_H8 = 0L;
    private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_H8 = 0L;
    private static final long[] BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED;
    private static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED;
    public static final long CENTRAL_PAWNS = 0x7E7E7E7E7E7E7E7EL;
    public static final long[] WHITE_PASSED;
    public static final long[] BLACK_PASSED;
    public static final long[] WHITE_BACKWARD;
    public static final long[] BLACK_BACKWARD;
    public static final long[] WHITE_FRONT_FULL;
    public static final long[] BLACK_FRONT_FULL;
    public static final long[] WHITE_SUPPORT;
    public static final long[] BLACK_SUPPORT;
    public static final long[] WHITE_POSSIBLE_ATTACKS;
    public static final long[] BLACK_POSSIBLE_ATTACKS;
    public static final long[] WHITE_PASSER_PARAM;
    public static final long[] BLACK_PASSER_PARAM;
    public static final long[] WHITE_PASSER_EXT_PARAM;
    public static final long[] BLACK_PASSER_EXT_PARAM;
    public static final long[] WHITE_CAN_BE_SUPPORTED_LEFT;
    public static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT;
    public static final long[] BLACK_CAN_BE_SUPPORTED_LEFT;
    public static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT;
    public static final long[] WHITE_KEY_SQUARES;
    public static final long[] BLACK_KEY_SQUARES;

    public static final boolean isPasser(int colour, int pawnFieldID, long opPawns) {
        if (colour == 0) {
            long passed = WHITE_PASSED[pawnFieldID];
            long passedBoard = passed & opPawns;
            int passedHits = Utils.countBits(passedBoard);
            return passedHits == 0;
        }
        long passed = BLACK_PASSED[pawnFieldID];
        long passedBoard = passed & opPawns;
        int passedHits = Utils.countBits(passedBoard);
        return passedHits == 0;
    }

    public static final int getPasserRank(int colour, int pawnFieldID) {
        int rank = 0;
        rank = colour == 0 ? DIGITS[pawnFieldID] : 7 - DIGITS[pawnFieldID];
        return rank;
    }

    private static void genMembers_WhitePassed() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long WHITE_PASSED_" + curLetter + curDigit + " = ";
            if (i / 8 >= 0 && i / 8 < 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8 + 1; j < digits.length; ++j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                for (j = i / 8 + 1; j < digits.length; ++j) {
                    result = (String)result + letters[i % 8] + digits[j] + " | ";
                }
                if (i % 8 < 7) {
                    for (j = i / 8 + 1; j < digits.length; ++j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_PASSED_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "WHITE_PASSED_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackPassed() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 63; i >= 0; --i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long BLACK_PASSED_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 <= 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8 - 1; j >= 0; --j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                for (j = i / 8 - 1; j >= 0; --j) {
                    result = (String)result + letters[i % 8] + digits[j] + " | ";
                }
                if (i % 8 < 7) {
                    for (j = i / 8 - 1; j >= 0; --j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_PASSED_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "BLACK_PASSED_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhiteBackward() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long WHITE_BACKWARD_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8; j >= 0; --j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                if (i % 8 < 7) {
                    for (j = i / 8; j >= 0; --j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_BACKWARD_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "WHITE_BACKWARD_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackBackward() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long BLACK_BACKWARD_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8; j < 8; ++j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                if (i % 8 < 7) {
                    for (j = i / 8; j < 8; ++j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_BACKWARD_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "BLACK_BACKWARD_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhiteDoubled() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long WHITE_FRONT_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                for (int j = i / 8; j < 7; ++j) {
                    result = (String)result + letters[i % 8] + digits[j + 1] + " | ";
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_FRONT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "WHITE_FRONT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackDoubled() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long BLACK_FRONT_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                for (int j = i / 8; j > 0; --j) {
                    result = (String)result + letters[i % 8] + digits[j - 1] + " | ";
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_FRONT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "BLACK_FRONT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhiteSupport() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long WHITE_SUPPORT_" + curLetter + curDigit + " = ";
            if (i % 8 > 0) {
                result = (String)result + letters[i % 8 - 1] + digits[i / 8] + " | ";
                if (i / 8 > 0) {
                    result = (String)result + letters[i % 8 - 1] + digits[i / 8 - 1] + " | ";
                }
            }
            if (i % 8 < 7) {
                result = (String)result + letters[i % 8 + 1] + digits[i / 8] + " | ";
                if (i / 8 > 0) {
                    result = (String)result + letters[i % 8 + 1] + digits[i / 8 - 1] + " | ";
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_SUPPORT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "WHITE_SUPPORT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackSupport() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long BLACK_SUPPORT_" + curLetter + curDigit + " = ";
            if (i % 8 > 0) {
                result = (String)result + letters[i % 8 - 1] + digits[i / 8] + " | ";
                if (i / 8 < 7) {
                    result = (String)result + letters[i % 8 - 1] + digits[i / 8 + 1] + " | ";
                }
            }
            if (i % 8 < 7) {
                result = (String)result + letters[i % 8 + 1] + digits[i / 8] + " | ";
                if (i / 8 < 7) {
                    result = (String)result + letters[i % 8 + 1] + digits[i / 8 + 1] + " | ";
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_SUPPORT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "BLACK_SUPPORT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhitePossibleAttacks() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long WHITE_POSSIBLE_ATTACKS_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8 + 1; j < 8; ++j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                if (i % 8 < 7) {
                    for (j = i / 8 + 1; j < 8; ++j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_POSSIBLE_ATTACKS_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "WHITE_POSSIBLE_ATTACKS_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackPossibleAttacks() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = "private static final long BLACK_POSSIBLE_ATTACKS_" + curLetter + curDigit + " = ";
            if (i / 8 > 0 && i / 8 < 7) {
                int j;
                if (i % 8 > 0) {
                    for (j = i / 8 - 1; j >= 0; --j) {
                        result = (String)result + letters[i % 8 - 1] + digits[j] + " | ";
                    }
                }
                if (i % 8 < 7) {
                    for (j = i / 8 - 1; j >= 0; --j) {
                        result = (String)result + letters[i % 8 + 1] + digits[j] + " | ";
                    }
                }
            }
            if (((String)result).endsWith(" = ")) {
                result = (String)result + "0L";
            }
            if (((String)result).endsWith(" | ")) {
                result = ((String)result).substring(0, ((String)result).length() - 3);
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_POSSIBLE_ATTACKS_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result = (String)result + "BLACK_POSSIBLE_ATTACKS_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhitePasserPerimeter() {
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            int k;
            int j;
            int letter = i % 8;
            int digit = i / 8;
            String curLetter = letters[letter];
            String curDigit = digits[digit];
            result = "private static final long WHITE_PASSER_PARAM_" + curLetter + curDigit + " = ";
            int to = letter + (7 - digit);
            if (to > 7) {
                to = 7;
            }
            for (j = letter; j <= to; ++j) {
                for (k = digit; k <= 7; ++k) {
                    if (j != letter || k != digit) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                }
            }
            to = letter - (7 - digit);
            if (to < 0) {
                to = 0;
            }
            for (j = letter - 1; j >= to; --j) {
                for (k = digit; k <= 7; ++k) {
                    if (j != letter || k != digit) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                }
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_PASSER_PARAM_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            String curLetter = letters[i % 8];
            String curDigit = digits[i / 8];
            result = (String)result + "WHITE_PASSER_PARAM_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackPasserPerimeter() {
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            int k;
            int j;
            int letter = i % 8;
            int digit = i / 8;
            String curLetter = letters[letter];
            String curDigit = digits[digit];
            result = "private static final long BLACK_PASSER_PARAM_" + curLetter + curDigit + " = ";
            int to = letter + digit;
            if (to > 7) {
                to = 7;
            }
            for (j = letter; j <= to; ++j) {
                for (k = digit; k >= 0; --k) {
                    if (j != letter || k != digit) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                }
            }
            to = letter - digit;
            if (to < 0) {
                to = 0;
            }
            for (j = letter - 1; j >= to; --j) {
                for (k = digit; k >= 0; --k) {
                    if (j != letter || k != digit) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                }
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_PASSER_PARAM_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            String curLetter = letters[i % 8];
            String curDigit = digits[i / 8];
            result = (String)result + "BLACK_PASSER_PARAM_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhitePasserExtPerimeter() {
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            int k;
            int j;
            int letter = i % 8;
            int digit = i / 8;
            String curLetter = letters[letter];
            String curDigit = digits[digit];
            result = "private static final long WHITE_PASSER_EXT_PARAM_" + curLetter + curDigit + " = ";
            int to = letter + (7 - digit) + 1;
            if (to > 7) {
                to = 7;
            }
            for (j = letter; j <= to; ++j) {
                int n = k = digit > 0 ? digit - 1 : digit;
                while (k <= 7) {
                    if (j != letter || k != (digit > 0 ? digit - 1 : digit)) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                    ++k;
                }
            }
            to = letter - (7 - digit) - 1;
            if (to < 0) {
                to = 0;
            }
            for (j = letter - 1; j >= to; --j) {
                int n = k = digit > 0 ? digit - 1 : digit;
                while (k <= 7) {
                    if (j != letter || k != (digit > 0 ? digit - 1 : digit)) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                    ++k;
                }
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] WHITE_PASSER_EXT_PARAM_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            String curLetter = letters[i % 8];
            String curDigit = digits[i / 8];
            result = (String)result + "WHITE_PASSER_EXT_PARAM_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_BlackPasserExtPerimeter() {
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            int k;
            int j;
            int letter = i % 8;
            int digit = i / 8;
            String curLetter = letters[letter];
            String curDigit = digits[digit];
            result = "private static final long BLACK_PASSER_EXT_PARAM_" + curLetter + curDigit + " = ";
            int to = letter + digit + 1;
            if (to > 7) {
                to = 7;
            }
            for (j = letter; j <= to; ++j) {
                int n = k = digit < 7 ? digit + 1 : digit;
                while (k >= 0) {
                    if (j != letter || k != (digit < 7 ? digit + 1 : digit)) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                    --k;
                }
            }
            to = letter - digit - 1;
            if (to < 0) {
                to = 0;
            }
            for (j = letter - 1; j >= to; --j) {
                int n = k = digit < 7 ? digit + 1 : digit;
                while (k >= 0) {
                    if (j != letter || k != (digit < 7 ? digit + 1 : digit)) {
                        result = (String)result + " | ";
                    }
                    result = (String)result + letters[j] + digits[k];
                    --k;
                }
            }
            result = (String)result + ";";
            System.out.println((String)result);
        }
        result = "private static final long[] BLACK_PASSER_EXT_PARAM_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            String curLetter = letters[i % 8];
            String curDigit = digits[i / 8];
            result = (String)result + "BLACK_PASSER_EXT_PARAM_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_WhiteCanBeSupported() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result_left = "";
        Object result_right = "";
        for (i = 0; i < 64; ++i) {
            int cur;
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_left = "private static final long WHITE_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + " = ";
            result_right = "private static final long WHITE_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + " = ";
            if (i % 8 > 0) {
                for (cur = 0; (cur < 3 && i / 8 != 4 || cur <= 3 && i / 8 == 4) && i / 8 - cur > 0 && i / 8 - cur < 7; ++cur) {
                    result_left = (String)result_left + letters[i % 8 - 1] + digits[i / 8 - cur] + " | ";
                }
            }
            if (i % 8 < 7) {
                for (cur = 0; (cur < 3 && i / 8 != 4 || cur <= 3 && i / 8 == 4) && i / 8 - cur > 0 && i / 8 - cur < 7; ++cur) {
                    result_right = (String)result_right + letters[i % 8 + 1] + digits[i / 8 - cur] + " | ";
                }
            }
            if (((String)result_left).endsWith(" = ")) {
                result_left = (String)result_left + "0L";
            }
            if (((String)result_left).endsWith(" | ")) {
                result_left = ((String)result_left).substring(0, ((String)result_left).length() - 3);
            }
            result_left = (String)result_left + ";";
            System.out.println((String)result_left);
            if (((String)result_right).endsWith(" = ")) {
                result_right = (String)result_right + "0L";
            }
            if (((String)result_right).endsWith(" | ")) {
                result_right = ((String)result_right).substring(0, ((String)result_right).length() - 3);
            }
            result_right = (String)result_right + ";";
            System.out.println((String)result_right);
        }
        result_left = "private static final long[] WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_left = (String)result_left + "WHITE_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result_left = (String)result_left + "};";
        }
        System.out.println((String)result_left);
        result_right = "private static final long[] WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_right = (String)result_right + "WHITE_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result_right = (String)result_right + "};";
        }
        System.out.println((String)result_right);
    }

    private static void genMembers_BlackCanBeSupported() {
        String curDigit;
        String curLetter;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result_left = "";
        Object result_right = "";
        for (i = 0; i < 64; ++i) {
            int cur;
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_left = "private static final long BLACK_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + " = ";
            result_right = "private static final long BLACK_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + " = ";
            if (i % 8 > 0) {
                for (cur = 0; (cur < 3 && i / 8 != 3 || cur <= 3 && i / 8 == 3) && i / 8 + cur > 0 && i / 8 + cur < 7; ++cur) {
                    result_left = (String)result_left + letters[i % 8 - 1] + digits[i / 8 + cur] + " | ";
                }
            }
            if (i % 8 < 7) {
                for (cur = 0; (cur < 3 && i / 8 != 3 || cur <= 3 && i / 8 == 3) && i / 8 + cur > 0 && i / 8 + cur < 7; ++cur) {
                    result_right = (String)result_right + letters[i % 8 + 1] + digits[i / 8 + cur] + " | ";
                }
            }
            if (((String)result_left).endsWith(" = ")) {
                result_left = (String)result_left + "0L";
            }
            if (((String)result_left).endsWith(" | ")) {
                result_left = ((String)result_left).substring(0, ((String)result_left).length() - 3);
            }
            result_left = (String)result_left + ";";
            System.out.println((String)result_left);
            if (((String)result_right).endsWith(" = ")) {
                result_right = (String)result_right + "0L";
            }
            if (((String)result_right).endsWith(" | ")) {
                result_right = ((String)result_right).substring(0, ((String)result_right).length() - 3);
            }
            result_right = (String)result_right + ";";
            System.out.println((String)result_right);
        }
        result_left = "private static final long[] BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_left = (String)result_left + "BLACK_CAN_BE_SUPPORTED_LEFT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result_left = (String)result_left + "};";
        }
        System.out.println((String)result_left);
        result_right = "private static final long[] BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[] {";
        for (i = 0; i < 64; ++i) {
            curLetter = letters[i % 8];
            curDigit = digits[i / 8];
            result_right = (String)result_right + "BLACK_CAN_BE_SUPPORTED_RIGHT_" + curLetter + curDigit + ", ";
            if (i != 63) continue;
            result_right = (String)result_right + "};";
        }
        System.out.println((String)result_right);
    }

    public static void main(String[] args) {
        PawnStructureConstants.genMembers_WhitePassed();
        PawnStructureConstants.genMembers_BlackPassed();
    }

    static {
        int idx;
        int i;
        WHITE_PASSED_ORDERED = new long[]{0xC0C0C0C0C0C0C0L, 0xE0E0E0E0E0E0E0L, 0x70707070707070L, 0x38383838383838L, 0x1C1C1C1C1C1C1CL, 0xE0E0E0E0E0E0EL, 0x7070707070707L, 0x3030303030303L, 0xC0C0C0C0C0C0L, 0xE0E0E0E0E0E0L, 0x707070707070L, 0x383838383838L, 0x1C1C1C1C1C1CL, 0xE0E0E0E0E0EL, 0x70707070707L, 0x30303030303L, 0xC0C0C0C0C0L, 0xE0E0E0E0E0L, 0x7070707070L, 0x3838383838L, 0x1C1C1C1C1CL, 0xE0E0E0E0EL, 0x707070707L, 0x303030303L, 0xC0C0C0C0L, 0xE0E0E0E0L, 0x70707070L, 0x38383838L, 0x1C1C1C1CL, 0xE0E0E0EL, 0x7070707L, 0x3030303L, 0xC0C0C0L, 0xE0E0E0L, 0x707070L, 0x383838L, 0x1C1C1CL, 921102L, 460551L, 197379L, 49344L, 57568L, 28784L, 14392L, 7196L, 3598L, 1799L, 771L, 192L, 224L, 112L, 56L, 28L, 14L, 7L, 3L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_PASSED_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, -4611686018427387904L, -2305843009213693952L, 0x7000000000000000L, 0x3800000000000000L, 0x1C00000000000000L, 0xE00000000000000L, 0x700000000000000L, 0x300000000000000L, -4557642822898941952L, -2242792614430507008L, 0x7070000000000000L, 0x3838000000000000L, 0x1C1C000000000000L, 0xE0E000000000000L, 0x707000000000000L, 0x303000000000000L, -4557431716666408960L, -2242546323825885184L, 0x7070700000000000L, 0x3838380000000000L, 0x1C1C1C0000000000L, 0xE0E0E0000000000L, 0x707070000000000L, 0x303030000000000L, -4557430892032688128L, -2242545361753210880L, 0x7070707000000000L, 0x3838383800000000L, 0x1C1C1C1C00000000L, 0xE0E0E0E00000000L, 0x707070700000000L, 0x303030300000000L, -4557430888811462656L, -2242545357995114496L, 0x7070707070000000L, 0x3838383838000000L, 0x1C1C1C1C1C000000L, 0xE0E0E0E0E000000L, 0x707070707000000L, 0x303030303000000L, -4557430888798879744L, -2242545357980434432L, 0x7070707070700000L, 0x3838383838380000L, 0x1C1C1C1C1C1C0000L, 0xE0E0E0E0E0E0000L, 0x707070707070000L, 0x303030303030000L, -4557430888798830592L, -2242545357980377088L, 0x7070707070707000L, 0x3838383838383800L, 0x1C1C1C1C1C1C1C00L, 0xE0E0E0E0E0E0E00L, 0x707070707070700L, 0x303030303030300L};
        WHITE_BACKWARD_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4040000000000000L, -6872493031367376896L, 0x5050000000000000L, 0x2828000000000000L, 0x1414000000000000L, 0xA0A000000000000L, 0x505000000000000L, 0x202000000000000L, 0x4040400000000000L, -6872317109506932736L, 0x5050500000000000L, 0x2828280000000000L, 0x1414140000000000L, 0xA0A0A0000000000L, 0x505050000000000L, 0x202020000000000L, 0x4040404000000000L, -6872316422312165376L, 0x5050505000000000L, 0x2828282800000000L, 0x1414141400000000L, 0xA0A0A0A00000000L, 0x505050500000000L, 0x202020200000000L, 0x4040404040000000L, -6872316419627810816L, 0x5050505050000000L, 0x2828282828000000L, 0x1414141414000000L, 0xA0A0A0A0A000000L, 0x505050505000000L, 0x202020202000000L, 0x4040404040400000L, -6872316419617325056L, 0x5050505050500000L, 0x2828282828280000L, 0x1414141414140000L, 0xA0A0A0A0A0A0000L, 0x505050505050000L, 0x202020202020000L, 0x4040404040404000L, -6872316419617284096L, 0x5050505050505000L, 0x2828282828282800L, 0x1414141414141400L, 0xA0A0A0A0A0A0A00L, 0x505050505050500L, 0x202020202020200L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_BACKWARD_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x40404040404040L, 0xA0A0A0A0A0A0A0L, 0x50505050505050L, 0x28282828282828L, 0x14141414141414L, 0xA0A0A0A0A0A0AL, 0x5050505050505L, 0x2020202020202L, 0x404040404040L, 0xA0A0A0A0A0A0L, 0x505050505050L, 0x282828282828L, 0x141414141414L, 0xA0A0A0A0A0AL, 0x50505050505L, 0x20202020202L, 0x4040404040L, 0xA0A0A0A0A0L, 0x5050505050L, 0x2828282828L, 0x1414141414L, 0xA0A0A0A0AL, 0x505050505L, 0x202020202L, 0x40404040L, 0xA0A0A0A0L, 0x50505050L, 0x28282828L, 0x14141414L, 0xA0A0A0AL, 0x5050505L, 0x2020202L, 0x404040L, 0xA0A0A0L, 0x505050L, 0x282828L, 0x141414L, 657930L, 328965L, 131586L, 16448L, 41120L, 20560L, 10280L, 5140L, 2570L, 1285L, 514L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        WHITE_FRONT_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x808080808080L, 0x404040404040L, 0x202020202020L, 0x101010101010L, 0x80808080808L, 0x40404040404L, 0x20202020202L, 0x10101010101L, 0x8080808080L, 0x4040404040L, 0x2020202020L, 0x1010101010L, 0x808080808L, 0x404040404L, 0x202020202L, 0x101010101L, 0x80808080L, 0x40404040L, 0x20202020L, 0x10101010L, 0x8080808L, 0x4040404L, 0x2020202L, 0x1010101L, 0x808080L, 0x404040L, 0x202020L, 0x101010L, 526344L, 263172L, 131586L, 65793L, 32896L, 16448L, 8224L, 4112L, 2056L, 1028L, 514L, 257L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_FRONT_FULL_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, -9187343239835811840L, 0x4040000000000000L, 0x2020000000000000L, 0x1010000000000000L, 0x808000000000000L, 0x404000000000000L, 0x202000000000000L, 0x101000000000000L, -9187202502347456512L, 0x4040400000000000L, 0x2020200000000000L, 0x1010100000000000L, 0x808080000000000L, 0x404040000000000L, 0x202020000000000L, 0x101010000000000L, -9187201952591642624L, 0x4040404000000000L, 0x2020202000000000L, 0x1010101000000000L, 0x808080800000000L, 0x404040400000000L, 0x202020200000000L, 0x101010100000000L, -9187201950444158976L, 0x4040404040000000L, 0x2020202020000000L, 0x1010101010000000L, 0x808080808000000L, 0x404040404000000L, 0x202020202000000L, 0x101010101000000L, -9187201950435770368L, 0x4040404040400000L, 0x2020202020200000L, 0x1010101010100000L, 0x808080808080000L, 0x404040404040000L, 0x202020202020000L, 0x101010101010000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        WHITE_SUPPORT_ORDERED = new long[]{0x4000000000000000L, -6917529027641081856L, 0x5000000000000000L, 0x2800000000000000L, 0x1400000000000000L, 0xA00000000000000L, 0x500000000000000L, 0x200000000000000L, 0x4040000000000000L, -6872493031367376896L, 0x5050000000000000L, 0x2828000000000000L, 0x1414000000000000L, 0xA0A000000000000L, 0x505000000000000L, 0x202000000000000L, 0x40400000000000L, 0xA0A00000000000L, 0x50500000000000L, 0x28280000000000L, 0x14140000000000L, 0xA0A0000000000L, 0x5050000000000L, 0x2020000000000L, 0x404000000000L, 0xA0A000000000L, 0x505000000000L, 0x282800000000L, 0x141400000000L, 0xA0A00000000L, 0x50500000000L, 0x20200000000L, 0x4040000000L, 0xA0A0000000L, 0x5050000000L, 0x2828000000L, 0x1414000000L, 0xA0A000000L, 0x505000000L, 0x202000000L, 0x40400000L, 0xA0A00000L, 0x50500000L, 0x28280000L, 0x14140000L, 0xA0A0000L, 0x5050000L, 0x2020000L, 0x404000L, 0xA0A000L, 0x505000L, 0x282800L, 0x141400L, 657920L, 328960L, 131584L, 16448L, 41120L, 20560L, 10280L, 5140L, 2570L, 1285L, 514L};
        BLACK_SUPPORT_ORDERED = new long[]{0x4040000000000000L, -6872493031367376896L, 0x5050000000000000L, 0x2828000000000000L, 0x1414000000000000L, 0xA0A000000000000L, 0x505000000000000L, 0x202000000000000L, 0x40400000000000L, 0xA0A00000000000L, 0x50500000000000L, 0x28280000000000L, 0x14140000000000L, 0xA0A0000000000L, 0x5050000000000L, 0x2020000000000L, 0x404000000000L, 0xA0A000000000L, 0x505000000000L, 0x282800000000L, 0x141400000000L, 0xA0A00000000L, 0x50500000000L, 0x20200000000L, 0x4040000000L, 0xA0A0000000L, 0x5050000000L, 0x2828000000L, 0x1414000000L, 0xA0A000000L, 0x505000000L, 0x202000000L, 0x40400000L, 0xA0A00000L, 0x50500000L, 0x28280000L, 0x14140000L, 0xA0A0000L, 0x5050000L, 0x2020000L, 0x404000L, 0xA0A000L, 0x505000L, 0x282800L, 0x141400L, 657920L, 328960L, 131584L, 16448L, 41120L, 20560L, 10280L, 5140L, 2570L, 1285L, 514L, 64L, 160L, 80L, 40L, 20L, 10L, 5L, 2L};
        WHITE_POSSIBLE_ATTACKS_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x404040404040L, 0xA0A0A0A0A0A0L, 0x505050505050L, 0x282828282828L, 0x141414141414L, 0xA0A0A0A0A0AL, 0x50505050505L, 0x20202020202L, 0x4040404040L, 0xA0A0A0A0A0L, 0x5050505050L, 0x2828282828L, 0x1414141414L, 0xA0A0A0A0AL, 0x505050505L, 0x202020202L, 0x40404040L, 0xA0A0A0A0L, 0x50505050L, 0x28282828L, 0x14141414L, 0xA0A0A0AL, 0x5050505L, 0x2020202L, 0x404040L, 0xA0A0A0L, 0x505050L, 0x282828L, 0x141414L, 657930L, 328965L, 131586L, 16448L, 41120L, 20560L, 10280L, 5140L, 2570L, 1285L, 514L, 64L, 160L, 80L, 40L, 20L, 10L, 5L, 2L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_POSSIBLE_ATTACKS_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4000000000000000L, -6917529027641081856L, 0x5000000000000000L, 0x2800000000000000L, 0x1400000000000000L, 0xA00000000000000L, 0x500000000000000L, 0x200000000000000L, 0x4040000000000000L, -6872493031367376896L, 0x5050000000000000L, 0x2828000000000000L, 0x1414000000000000L, 0xA0A000000000000L, 0x505000000000000L, 0x202000000000000L, 0x4040400000000000L, -6872317109506932736L, 0x5050500000000000L, 0x2828280000000000L, 0x1414140000000000L, 0xA0A0A0000000000L, 0x505050000000000L, 0x202020000000000L, 0x4040404000000000L, -6872316422312165376L, 0x5050505000000000L, 0x2828282800000000L, 0x1414141400000000L, 0xA0A0A0A00000000L, 0x505050500000000L, 0x202020200000000L, 0x4040404040000000L, -6872316419627810816L, 0x5050505050000000L, 0x2828282828000000L, 0x1414141414000000L, 0xA0A0A0A0A000000L, 0x505050505000000L, 0x202020202000000L, 0x4040404040400000L, -6872316419617325056L, 0x5050505050500000L, 0x2828282828280000L, 0x1414141414140000L, 0xA0A0A0A0A0A0000L, 0x505050505050000L, 0x202020202020000L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        WHITE_PASSER_PARAM_ORDERED = new long[]{-1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, 0xFEFEFEFEFEFEFEL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0x7F7F7F7F7F7F7FL, 0xFCFCFCFCFCFCL, 0xFEFEFEFEFEFEL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0x7F7F7F7F7F7FL, 0x3F3F3F3F3F3FL, 0xF8F8F8F8F8L, 0xFCFCFCFCFCL, 0xFEFEFEFEFEL, 0xFFFFFFFFFFL, 0xFFFFFFFFFFL, 0x7F7F7F7F7FL, 0x3F3F3F3F3FL, 0x1F1F1F1F1FL, 0xF0F0F0F0L, 0xF8F8F8F8L, 0xFCFCFCFCL, 0xFEFEFEFEL, 0x7F7F7F7FL, 0x3F3F3F3FL, 0x1F1F1F1FL, 0xF0F0F0FL, 0xE0E0E0L, 0xF0F0F0L, 0xF8F8F8L, 0x7C7C7CL, 0x3E3E3EL, 0x1F1F1FL, 986895L, 460551L, 49344L, 57568L, 28784L, 14392L, 7196L, 3598L, 1799L, 771L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L};
        BLACK_PASSER_PARAM_ORDERED = new long[]{Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, -4557642822898941952L, -2242792614430507008L, 0x7070000000000000L, 0x3838000000000000L, 0x1C1C000000000000L, 0xE0E000000000000L, 0x707000000000000L, 0x303000000000000L, -2242546323825885184L, -1085103627405623296L, -506382279195492352L, 0x7C7C7C0000000000L, 0x3E3E3E0000000000L, 0x1F1F1F0000000000L, 0xF0F0F0000000000L, 0x707070000000000L, -1085102596613472256L, -506381214043602944L, -217020522758668288L, -72340177116200960L, 0x7F7F7F7F00000000L, 0x3F3F3F3F00000000L, 0x1F1F1F1F00000000L, 0xF0F0F0F00000000L, -506381209882853376L, -217020518530809856L, -72340172854788096L, -16777216L, -16777216L, 0x7F7F7F7F7F000000L, 0x3F3F3F3F3F000000L, 0x1F1F1F1F1F000000L, -217020518514294784L, -72340172838141952L, -65536L, -65536L, -65536L, -65536L, 0x7F7F7F7F7F7F0000L, 0x3F3F3F3F3F3F0000L, -72340172838076928L, -256L, -256L, -256L, -256L, -256L, -256L, 0x7F7F7F7F7F7F7F00L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L};
        WHITE_PASSER_EXT_PARAM_ORDERED = new long[]{-1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, 0xFEFEFEFEFEFEFEL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0xFFFFFFFFFFFFFFL, 0x7F7F7F7F7F7F7FL, 0xFCFCFCFCFCFCL, 0xFEFEFEFEFEFEL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0xFFFFFFFFFFFFL, 0x7F7F7F7F7F7FL, 0x3F3F3F3F3F3FL, 0xF8F8F8F8F8L, 0xFCFCFCFCFCL, 0xFEFEFEFEFEL, 0xFFFFFFFFFFL, 0xFFFFFFFFFFL, 0x7F7F7F7F7FL, 0x3F3F3F3F3FL, 0x1F1F1F1F1FL, 0xF0F0F0F0L, 0xF8F8F8F8L, 0xFCFCFCFCL, 0xFEFEFEFEL, 0x7F7F7F7FL, 0x3F3F3F3FL, 0x1F1F1F1FL, 0xF0F0F0FL, 0xE0E0E0L, 0xF0F0F0L, 0xF8F8F8L, 0x7C7C7CL, 0x3E3E3EL, 0x1F1F1FL, 986895L, 460551L, 49344L, 57568L, 28784L, 14392L, 7196L, 3598L, 1799L, 771L};
        BLACK_PASSER_EXT_PARAM_ORDERED = new long[]{-4557642822898941952L, -2242792614430507008L, 0x7070000000000000L, 0x3838000000000000L, 0x1C1C000000000000L, 0xE0E000000000000L, 0x707000000000000L, 0x303000000000000L, -2242546323825885184L, -1085103627405623296L, -506382279195492352L, 0x7C7C7C0000000000L, 0x3E3E3E0000000000L, 0x1F1F1F0000000000L, 0xF0F0F0000000000L, 0x707070000000000L, -1085102596613472256L, -506381214043602944L, -217020522758668288L, -72340177116200960L, 0x7F7F7F7F00000000L, 0x3F3F3F3F00000000L, 0x1F1F1F1F00000000L, 0xF0F0F0F00000000L, -506381209882853376L, -217020518530809856L, -72340172854788096L, -16777216L, -16777216L, 0x7F7F7F7F7F000000L, 0x3F3F3F3F3F000000L, 0x1F1F1F1F1F000000L, -217020518514294784L, -72340172838141952L, -65536L, -65536L, -65536L, -65536L, 0x7F7F7F7F7F7F0000L, 0x3F3F3F3F3F3F0000L, -72340172838076928L, -256L, -256L, -256L, -256L, -256L, -256L, 0x7F7F7F7F7F7F7F00L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L, -1L};
        WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x80000000000000L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0L, 0x80800000000000L, 0x40400000000000L, 0x20200000000000L, 0x10100000000000L, 0x8080000000000L, 0x4040000000000L, 0x2020000000000L, 0L, 0x80808000000000L, 0x40404000000000L, 0x20202000000000L, 0x10101000000000L, 0x8080800000000L, 0x4040400000000L, 0x2020200000000L, 0L, 0x80808080000000L, 0x40404040000000L, 0x20202020000000L, 0x10101010000000L, 0x8080808000000L, 0x4040404000000L, 0x2020202000000L, 0L, 0x8080800000L, 0x4040400000L, 0x2020200000L, 0x1010100000L, 0x808080000L, 0x404040000L, 0x202020000L, 0L, 0x80808000L, 0x40404000L, 0x20202000L, 0x10101000L, 0x8080800L, 0x4040400L, 0x2020200L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L, 0L, 0x40400000000000L, 0x20200000000000L, 0x10100000000000L, 0x8080000000000L, 0x4040000000000L, 0x2020000000000L, 0x1010000000000L, 0L, 0x40404000000000L, 0x20202000000000L, 0x10101000000000L, 0x8080800000000L, 0x4040400000000L, 0x2020200000000L, 0x1010100000000L, 0L, 0x40404040000000L, 0x20202020000000L, 0x10101010000000L, 0x8080808000000L, 0x4040404000000L, 0x2020202000000L, 0x1010101000000L, 0L, 0x4040400000L, 0x2020200000L, 0x1010100000L, 0x808080000L, 0x404040000L, 0x202020000L, 0x101010000L, 0L, 0x40404000L, 0x20202000L, 0x10101000L, 0x8080800L, 0x4040400L, 0x2020200L, 0x1010100L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x80808000000000L, 0x40404000000000L, 0x20202000000000L, 0x10101000000000L, 0x8080800000000L, 0x4040400000000L, 0x2020200000000L, 0L, 0x808080000000L, 0x404040000000L, 0x202020000000L, 0x101010000000L, 0x80808000000L, 0x40404000000L, 0x20202000000L, 0L, 0x8080808000L, 0x4040404000L, 0x2020202000L, 0x1010101000L, 0x808080800L, 0x404040400L, 0x202020200L, 0L, 0x80808000L, 0x40404000L, 0x20202000L, 0x10101000L, 0x8080800L, 0x4040400L, 0x2020200L, 0L, 0x808000L, 0x404000L, 0x202000L, 0x101000L, 526336L, 263168L, 131584L, 0L, 32768L, 16384L, 8192L, 4096L, 2048L, 1024L, 512L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x40404000000000L, 0x20202000000000L, 0x10101000000000L, 0x8080800000000L, 0x4040400000000L, 0x2020200000000L, 0x1010100000000L, 0L, 0x404040000000L, 0x202020000000L, 0x101010000000L, 0x80808000000L, 0x40404000000L, 0x20202000000L, 0x10101000000L, 0L, 0x4040404000L, 0x2020202000L, 0x1010101000L, 0x808080800L, 0x404040400L, 0x202020200L, 0x101010100L, 0L, 0x40404000L, 0x20202000L, 0x10101000L, 0x8080800L, 0x4040400L, 0x2020200L, 0x1010100L, 0L, 0x404000L, 0x202000L, 0x101000L, 526336L, 263168L, 131584L, 65792L, 0L, 16384L, 8192L, 4096L, 2048L, 1024L, 512L, 256L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        WHITE_PASSED = new long[64];
        BLACK_PASSED = new long[64];
        WHITE_BACKWARD = new long[64];
        BLACK_BACKWARD = new long[64];
        WHITE_FRONT_FULL = new long[64];
        BLACK_FRONT_FULL = new long[64];
        WHITE_SUPPORT = new long[64];
        BLACK_SUPPORT = new long[64];
        WHITE_POSSIBLE_ATTACKS = new long[64];
        BLACK_POSSIBLE_ATTACKS = new long[64];
        WHITE_PASSER_PARAM = new long[64];
        BLACK_PASSER_PARAM = new long[64];
        WHITE_PASSER_EXT_PARAM = new long[64];
        BLACK_PASSER_EXT_PARAM = new long[64];
        WHITE_CAN_BE_SUPPORTED_LEFT = new long[64];
        WHITE_CAN_BE_SUPPORTED_RIGHT = new long[64];
        BLACK_CAN_BE_SUPPORTED_LEFT = new long[64];
        BLACK_CAN_BE_SUPPORTED_RIGHT = new long[64];
        WHITE_KEY_SQUARES = new long[64];
        BLACK_KEY_SQUARES = new long[64];
        PawnStructureConstants.WHITE_KEY_SQUARES[8] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[16] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[24] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[32] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[40] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[48] = 16448L;
        PawnStructureConstants.WHITE_KEY_SQUARES[9] = 0xE000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[17] = 0xE0000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[25] = 0xE00000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[33] = 0xE0E000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[41] = 57568L;
        PawnStructureConstants.WHITE_KEY_SQUARES[49] = 41184L;
        PawnStructureConstants.WHITE_KEY_SQUARES[10] = 0x7000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[18] = 0x70000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[26] = 0x700000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[34] = 0x707000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[42] = 28784L;
        PawnStructureConstants.WHITE_KEY_SQUARES[50] = 20592L;
        PawnStructureConstants.WHITE_KEY_SQUARES[11] = 0x3800000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[19] = 0x38000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[27] = 0x380000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[35] = 0x383800L;
        PawnStructureConstants.WHITE_KEY_SQUARES[43] = 14392L;
        PawnStructureConstants.WHITE_KEY_SQUARES[51] = 10296L;
        PawnStructureConstants.WHITE_KEY_SQUARES[12] = 0x1C00000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[20] = 0x1C000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[28] = 0x1C0000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[36] = 0x1C1C00L;
        PawnStructureConstants.WHITE_KEY_SQUARES[44] = 7196L;
        PawnStructureConstants.WHITE_KEY_SQUARES[52] = 5148L;
        PawnStructureConstants.WHITE_KEY_SQUARES[13] = 0xE00000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[21] = 0xE000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[29] = 917504L;
        PawnStructureConstants.WHITE_KEY_SQUARES[37] = 921088L;
        PawnStructureConstants.WHITE_KEY_SQUARES[45] = 3598L;
        PawnStructureConstants.WHITE_KEY_SQUARES[53] = 2574L;
        PawnStructureConstants.WHITE_KEY_SQUARES[14] = 0x700000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[22] = 0x7000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[30] = 458752L;
        PawnStructureConstants.WHITE_KEY_SQUARES[38] = 460544L;
        PawnStructureConstants.WHITE_KEY_SQUARES[46] = 1799L;
        PawnStructureConstants.WHITE_KEY_SQUARES[54] = 1287L;
        PawnStructureConstants.WHITE_KEY_SQUARES[15] = 514L;
        PawnStructureConstants.WHITE_KEY_SQUARES[23] = 514L;
        PawnStructureConstants.WHITE_KEY_SQUARES[31] = 514L;
        PawnStructureConstants.WHITE_KEY_SQUARES[39] = 514L;
        PawnStructureConstants.WHITE_KEY_SQUARES[47] = 514L;
        PawnStructureConstants.WHITE_KEY_SQUARES[55] = 514L;
        PawnStructureConstants.BLACK_KEY_SQUARES[48] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[40] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[32] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[24] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[16] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[8] = 0x4040000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[49] = 0xE0000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[41] = 0xE000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[33] = 0xE00000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[25] = 0xE0E00000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[17] = -2242792614430507008L;
        PawnStructureConstants.BLACK_KEY_SQUARES[9] = -2260807012939988992L;
        PawnStructureConstants.BLACK_KEY_SQUARES[50] = 0x70000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[42] = 0x7000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[34] = 0x700000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[26] = 0x70700000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[18] = 0x7070000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[10] = 0x7050000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[51] = 0x38000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[43] = 0x3800000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[35] = 0x380000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[27] = 0x38380000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[19] = 0x3838000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[11] = 4046484265192390656L;
        PawnStructureConstants.BLACK_KEY_SQUARES[52] = 0x1C000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[44] = 0x1C00000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[36] = 0x1C0000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[28] = 0x1C1C0000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[20] = 0x1C1C000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[12] = 2023242132596195328L;
        PawnStructureConstants.BLACK_KEY_SQUARES[53] = 0xE000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[45] = 0xE00000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[37] = 0xE0000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[29] = 0xE0E0000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[21] = 0xE0E000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[13] = 0xE0A000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[54] = 0x7000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[46] = 0x700000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[38] = 0x70000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[30] = 0x7070000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[22] = 0x707000000000000L;
        PawnStructureConstants.BLACK_KEY_SQUARES[14] = 0x705000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[55] = 0x202000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[47] = 0x202000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[39] = 0x202000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[31] = 0x202000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[23] = 0x202000000000000L;
        PawnStructureConstants.WHITE_KEY_SQUARES[15] = 0x202000000000000L;
        for (i = 0; i < WHITE_PASSED_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_PASSED[idx] = WHITE_PASSED_ORDERED[i];
        }
        for (i = 0; i < BLACK_PASSED_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_PASSED[idx] = BLACK_PASSED_ORDERED[i];
        }
        for (i = 0; i < WHITE_BACKWARD_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_BACKWARD[idx] = WHITE_BACKWARD_ORDERED[i];
        }
        for (i = 0; i < BLACK_BACKWARD_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_BACKWARD[idx] = BLACK_BACKWARD_ORDERED[i];
        }
        for (i = 0; i < WHITE_FRONT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_FRONT_FULL[idx] = WHITE_FRONT_ORDERED[i];
        }
        for (i = 0; i < BLACK_FRONT_FULL_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_FRONT_FULL[idx] = BLACK_FRONT_FULL_ORDERED[i];
        }
        for (i = 0; i < WHITE_SUPPORT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_SUPPORT[idx] = WHITE_SUPPORT_ORDERED[i];
        }
        for (i = 0; i < BLACK_SUPPORT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_SUPPORT[idx] = BLACK_SUPPORT_ORDERED[i];
        }
        for (i = 0; i < WHITE_POSSIBLE_ATTACKS_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_POSSIBLE_ATTACKS[idx] = WHITE_POSSIBLE_ATTACKS_ORDERED[i];
        }
        for (i = 0; i < BLACK_POSSIBLE_ATTACKS_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_POSSIBLE_ATTACKS[idx] = BLACK_POSSIBLE_ATTACKS_ORDERED[i];
        }
        for (i = 0; i < WHITE_PASSER_PARAM_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_PASSER_PARAM[idx] = WHITE_PASSER_PARAM_ORDERED[i];
        }
        for (i = 0; i < BLACK_PASSER_PARAM_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_PASSER_PARAM[idx] = BLACK_PASSER_PARAM_ORDERED[i];
        }
        for (i = 0; i < WHITE_PASSER_EXT_PARAM_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_PASSER_EXT_PARAM[idx] = WHITE_PASSER_EXT_PARAM_ORDERED[i];
        }
        for (i = 0; i < BLACK_PASSER_EXT_PARAM_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_PASSER_EXT_PARAM[idx] = BLACK_PASSER_EXT_PARAM_ORDERED[i];
        }
        for (i = 0; i < WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_CAN_BE_SUPPORTED_LEFT[idx] = WHITE_CAN_BE_SUPPORTED_LEFT_ORDERED[i];
        }
        for (i = 0; i < WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.WHITE_CAN_BE_SUPPORTED_RIGHT[idx] = WHITE_CAN_BE_SUPPORTED_RIGHT_ORDERED[i];
        }
        for (i = 0; i < BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_CAN_BE_SUPPORTED_LEFT[idx] = BLACK_CAN_BE_SUPPORTED_LEFT_ORDERED[i];
        }
        for (i = 0; i < BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED.length; ++i) {
            idx = IDX_ORDERED_2_A1H1[i];
            PawnStructureConstants.BLACK_CAN_BE_SUPPORTED_RIGHT[idx] = BLACK_CAN_BE_SUPPORTED_RIGHT_ORDERED[i];
        }
    }
}

