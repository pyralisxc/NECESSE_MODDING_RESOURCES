/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;

public class OfficerPlies
extends Fields {
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A1 = 18049651735527937L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A1 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A1;
    public static final long ALL_OFFICER_MOVES_FROM_A1 = 18049651735527937L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B1 = 9024825867763968L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B1 = 0x80000000000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B1;
    public static final long ALL_OFFICER_MOVES_FROM_B1 = 45053622886727936L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C1 = 4512412933881856L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C1 = 0x40800000000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C1;
    public static final long ALL_OFFICER_MOVES_FROM_C1 = 22667548931719168L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D1 = 2256206466908160L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D1 = 9078117754732544L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D1;
    public static final long ALL_OFFICER_MOVES_FROM_D1 = 11334324221640704L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E1 = 1128103225065472L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E1 = 4539061024849920L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E1;
    public static final long ALL_OFFICER_MOVES_FROM_E1 = 5667164249915392L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F1 = 0x2010000000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F1 = 2269530520813568L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F1;
    public static final long ALL_OFFICER_MOVES_FROM_F1 = 2833579985862656L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G1 = 0x1000000000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G1 = 1134765260439552L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G1;
    public static final long ALL_OFFICER_MOVES_FROM_G1 = 1416240237150208L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H1 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H1 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H1 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H1 = 567382630219904L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H1;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H1;
    public static final long ALL_OFFICER_MOVES_FROM_H1 = 567382630219904L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A2 = 70506452091906L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A2 = 0x4000000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A2 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A2 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A2;
    public static final long ALL_OFFICER_MOVES_FROM_A2 = 4611756524879479810L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B2 = 35253226045953L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B2 = 0x2000000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B2 = Long.MIN_VALUE;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B2 = 0x800000000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B2;
    public static final long ALL_OFFICER_MOVES_FROM_B2 = -6917353036926680575L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C2 = 17626613022976L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C2 = 0x1000000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C2 = 0x4000000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C2 = 0x408000000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C2;
    public static final long ALL_OFFICER_MOVES_FROM_C2 = 5764696068147249408L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D2 = 8813306511360L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D2 = 0x800000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D2 = 0x2000000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D2 = 35461397479424L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D2;
    public static final long ALL_OFFICER_MOVES_FROM_D2 = 2882348036221108224L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E2 = 4406653222912L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E2 = 0x400000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E2 = 0x1000000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E2 = 17730707128320L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E2;
    public static final long ALL_OFFICER_MOVES_FROM_E2 = 1441174018118909952L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F2 = 0x20100000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F2 = 0x200000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F2 = 0x800000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F2 = 8865353596928L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F2;
    public static final long ALL_OFFICER_MOVES_FROM_F2 = 720587009051099136L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G2 = 0x10000000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G2 = 0x100000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G2 = 0x400000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G2 = 4432676798592L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G2;
    public static final long ALL_OFFICER_MOVES_FROM_G2 = 360293502378066048L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H2 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H2 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H2 = 0x200000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H2 = 2216338399296L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H2;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H2;
    public static final long ALL_OFFICER_MOVES_FROM_H2 = 144117404414255168L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A3 = 275415828484L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A3 = 0x2040000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A3 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A3 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A3;
    public static final long ALL_OFFICER_MOVES_FROM_A3 = 2323857683139004420L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B3 = 137707914242L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B3 = 0x1020000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B3 = 0x80000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B3 = 0x8000000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B3;
    public static final long ALL_OFFICER_MOVES_FROM_B3 = 1197958188344280066L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C3 = 68853957121L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C3 = 0x810000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C3 = -9205357638345293824L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C3 = 0x4080000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C3;
    public static final long ALL_OFFICER_MOVES_FROM_C3 = -8624392940535152127L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D3 = 34426978560L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D3 = 0x408000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D3 = 0x4020000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D3 = 138521083904L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D3;
    public static final long ALL_OFFICER_MOVES_FROM_D3 = 4911175566595588352L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E3 = 17213489152L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E3 = 0x204000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E3 = 0x2010000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E3 = 69260574720L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E3;
    public static final long ALL_OFFICER_MOVES_FROM_E3 = 2455587783297826816L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F3 = 0x201000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F3 = 0x102000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F3 = 0x1008000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F3 = 34630287488L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F3;
    public static final long ALL_OFFICER_MOVES_FROM_F3 = 1227793891648880768L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G3 = 0x100000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G3 = 0x1000000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G3 = 0x804000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G3 = 17315143744L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G3;
    public static final long ALL_OFFICER_MOVES_FROM_G3 = 577868148797087808L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H3 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H3 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H3 = 0x402000000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H3 = 8657571872L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H3;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H3;
    public static final long ALL_OFFICER_MOVES_FROM_H3 = 288793334762704928L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A4 = 1075843080L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A4 = 1161999072605765632L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A4 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A4 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A4;
    public static final long ALL_OFFICER_MOVES_FROM_A4 = 1161999073681608712L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B4 = 537921540L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B4 = 580999536302882816L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B4 = 0x800000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B4 = 0x80000000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B4;
    public static final long ALL_OFFICER_MOVES_FROM_B4 = 581140276476643332L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C4 = 268960770L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C4 = 290499768151441408L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C4 = 0x80400000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C4 = 0x40800000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C4;
    public static final long ALL_OFFICER_MOVES_FROM_C4 = 326598935265674242L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D4 = 134480385L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D4 = 145249884075720704L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D4 = -9205322453973204992L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D4 = 541097984L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D4;
    public static final long ALL_OFFICER_MOVES_FROM_D4 = -9060072569221905919L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E4 = 67240192L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E4 = 72624942037860352L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E4 = 4620710809868173312L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E4 = 270549120L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E4;
    public static final long ALL_OFFICER_MOVES_FROM_E4 = 4693335752243822976L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F4 = 0x2010000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F4 = 0x1020000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F4 = 2310355404934086656L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F4 = 135274560L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F4;
    public static final long ALL_OFFICER_MOVES_FROM_F4 = 2310639079102947392L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G4 = 0x1000000L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G4 = 0x10000000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G4 = 1155177702467043328L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G4 = 67637280L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G4;
    public static final long ALL_OFFICER_MOVES_FROM_G4 = 1155178802063085600L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H4 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H4 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H4 = 577588851233521664L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H4 = 33818640L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H4;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H4;
    public static final long ALL_OFFICER_MOVES_FROM_H4 = 577588851267340304L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A5 = 4202512L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A5 = 580999811180789760L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A5 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A5 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A5;
    public static final long ALL_OFFICER_MOVES_FROM_A5 = 580999811184992272L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B5 = 2101256L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B5 = 290499905590394880L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B5 = 0x8000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B5 = 0x800000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B5;
    public static final long ALL_OFFICER_MOVES_FROM_B5 = 290500455356698632L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C5 = 1050628L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C5 = 145249952795197440L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C5 = 0x804000000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C5 = 0x408000L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C5;
    public static final long ALL_OFFICER_MOVES_FROM_C5 = 145390965166737412L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D5 = 525314L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D5 = 72624976397598720L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D5 = 36099303202095104L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D5 = 2113664L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D5;
    public static final long ALL_OFFICER_MOVES_FROM_D5 = 108724279602332802L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E5 = 262657L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E5 = 283691179835392L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E5 = -9205322385253728256L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E5 = 1056832L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E5;
    public static final long ALL_OFFICER_MOVES_FROM_E5 = -9205038694072573375L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F5 = 131328L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F5 = 0x10200000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F5 = 4620710844227911680L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F5 = 528416L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F5;
    public static final long ALL_OFFICER_MOVES_FROM_F5 = 4620711952330133792L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G5 = 65536L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G5 = 0x100000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G5 = 2310355422113955840L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G5 = 264208L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G5;
    public static final long ALL_OFFICER_MOVES_FROM_G5 = 2310355426409252880L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H5 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H5 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H5 = 1155177711056977920L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H5 = 132104L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H5;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H5;
    public static final long ALL_OFFICER_MOVES_FROM_H5 = 1155177711057110024L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A6 = 16416L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A6 = 290499906664136704L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A6 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A6 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A6;
    public static final long ALL_OFFICER_MOVES_FROM_A6 = 290499906664153120L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B6 = 8208L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B6 = 145249953332068352L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B6 = 0x80000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B6 = 32768L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B6;
    public static final long ALL_OFFICER_MOVES_FROM_B6 = 145249955479592976L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C6 = 4104L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C6 = 72624976666034176L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C6 = 0x8040000000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C6 = 16512L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C6;
    public static final long ALL_OFFICER_MOVES_FROM_C6 = 72625527495610504L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D6 = 2052L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D6 = 283691314053120L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D6 = 141012903133184L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D6 = 8256L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D6;
    public static final long ALL_OFFICER_MOVES_FROM_D6 = 424704217196612L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E6 = 1026L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E6 = 1108168671232L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E6 = 36099303470530560L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E6 = 4128L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E6;
    public static final long ALL_OFFICER_MOVES_FROM_E6 = 36100411639206946L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F6 = 513L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F6 = 0x102000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F6 = -9205322385119510528L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F6 = 2064L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F6;
    public static final long ALL_OFFICER_MOVES_FROM_F6 = -9205322380790986223L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G6 = 256L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G6 = 0x1000000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G6 = 4620710844295020544L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G6 = 1032L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G6;
    public static final long ALL_OFFICER_MOVES_FROM_G6 = 4620710844311799048L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H6 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H6 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H6 = 2310355422147510272L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H6 = 516L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H6;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H6;
    public static final long ALL_OFFICER_MOVES_FROM_H6 = 2310355422147510788L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A7 = 64L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A7 = 145249953336262656L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A7 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A7 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A7;
    public static final long ALL_OFFICER_MOVES_FROM_A7 = 145249953336262720L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B7 = 32L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B7 = 72624976668131328L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B7 = 0x800000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B7 = 128L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B7;
    public static final long ALL_OFFICER_MOVES_FROM_B7 = 72624976676520096L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C7 = 16L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C7 = 283691315101696L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C7 = 0x80400000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C7 = 64L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C7;
    public static final long ALL_OFFICER_MOVES_FROM_C7 = 283693466779728L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D7 = 8L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D7 = 1108169195520L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D7 = 550831652864L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D7 = 32L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D7;
    public static final long ALL_OFFICER_MOVES_FROM_D7 = 1659000848424L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E7 = 4L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E7 = 4328783872L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E7 = 141012904181760L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E7 = 16L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E7;
    public static final long ALL_OFFICER_MOVES_FROM_E7 = 141017232965652L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F7 = 2L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F7 = 0x1020000L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F7 = 36099303471054848L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F7 = 8L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F7;
    public static final long ALL_OFFICER_MOVES_FROM_F7 = 36099303487963146L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G7 = 1L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G7 = 65536L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G7 = -9205322385119248384L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G7 = 4L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G7;
    public static final long ALL_OFFICER_MOVES_FROM_G7 = -9205322385119182843L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H7 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H7 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H7 = 4620710844295151616L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H7 = 2L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H7;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H7;
    public static final long ALL_OFFICER_MOVES_FROM_H7 = 4620710844295151618L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_A8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_A8 = 72624976668147712L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_A8 = 0L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_A8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_A8;
    public static final long ALL_OFFICER_MOVES_FROM_A8 = 72624976668147712L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_B8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_B8 = 283691315109888L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_B8 = 32768L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_B8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_B8;
    public static final long ALL_OFFICER_MOVES_FROM_B8 = 283691315142656L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_C8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_C8 = 1108169199616L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_C8 = 0x804000L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_C8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_C8;
    public static final long ALL_OFFICER_MOVES_FROM_C8 = 1108177604608L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_D8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_D8 = 4328785920L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_D8 = 2151686144L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_D8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_D8;
    public static final long ALL_OFFICER_MOVES_FROM_D8 = 6480472064L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_E8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_E8 = 16909312L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_E8 = 550831656960L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_E8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_E8;
    public static final long ALL_OFFICER_MOVES_FROM_E8 = 550848566272L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_F8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_F8 = 66048L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_F8 = 141012904183808L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_F8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_F8;
    public static final long ALL_OFFICER_MOVES_FROM_F8 = 141012904249856L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_G8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_G8 = 256L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_G8 = 36099303471055872L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_G8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_G8;
    public static final long ALL_OFFICER_MOVES_FROM_G8 = 36099303471056128L;
    public static final long ALL_OFFICER_DIR0_MOVES_FROM_H8 = 0L;
    public static final long ALL_OFFICER_DIR1_MOVES_FROM_H8 = 0L;
    public static final long ALL_OFFICER_DIR2_MOVES_FROM_H8 = -9205322385119247872L;
    public static final long ALL_OFFICER_DIR3_MOVES_FROM_H8 = 0L;
    public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H8;
    public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_H8;
    public static final long ALL_OFFICER_MOVES_FROM_H8 = -9205322385119247872L;
    public static final long[] ALL_ORDERED_OFFICER_MOVES;
    public static final long[] ALL_ORDERED_DIR0_OFFICER_MOVES;
    public static final long[] ALL_ORDERED_DIR1_OFFICER_MOVES;
    public static final long[] ALL_ORDERED_DIR2_OFFICER_MOVES;
    public static final long[] ALL_ORDERED_DIR3_OFFICER_MOVES;
    public static final long[][][] ALL_ORDERED_OFFICER_DIRS;
    public static final int[][] ALL_ORDERED_OFFICER_VALID_DIRS;
    public static final long[] ALL_OFFICER_MOVES;
    public static final long[][] ALL_OFFICER_DIR_MOVES;
    public static final long[] ALL_OFFICER_DIR0_MOVES;
    public static final long[] ALL_OFFICER_DIR1_MOVES;
    public static final long[] ALL_OFFICER_DIR2_MOVES;
    public static final long[] ALL_OFFICER_DIR3_MOVES;
    public static final long[] ALL_OFFICER_MOVES_1P;
    public static final long[] ALL_OFFICER_MOVES_2P;
    public static final long[] ALL_OFFICER_MOVES_34P;
    public static final long[] ALL_OFFICER_MOVES_567P;
    public static final int UP_LEFT_DIR = 3;
    public static final int UP_RIGHT_DIR = 0;
    public static final int DOWN_LEFT_DIR = 2;
    public static final int DOWN_RIGHT_DIR = 1;
    public static final int[][] ALL_OFFICER_VALID_DIRS;
    public static final int[][][] ALL_OFFICER_DIRS_WITH_FIELD_IDS;
    public static final long[][][] ALL_OFFICER_DIRS_WITH_BITBOARDS;
    public static final long[][] PATHS;
    public static final long PATH_NONE = -1L;
    public static final int[] W_MAGIC;
    public static final int[] B_MAGIC;

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

    public static final int mobility(int fieldID, long availableFields) {
        long mobility_1p = ALL_OFFICER_MOVES_1P[fieldID] & availableFields;
        if (mobility_1p == 0L) {
            return 0;
        }
        int max = 16;
        int mobility = 0;
        int mobility_1p_count = Utils.countBits(mobility_1p);
        max = max * mobility_1p_count / 4;
        mobility += mobility_1p_count;
        if (max > 0) {
            long mobility_2p = ALL_OFFICER_MOVES_2P[fieldID] & availableFields;
            int mobility_2p_count = Utils.countBits(mobility_2p);
            max = max * mobility_2p_count / 4;
            mobility += mobility_2p_count;
            if (max > 0) {
                long mobility_34p = ALL_OFFICER_MOVES_34P[fieldID] & availableFields;
                int mobility_34p_count = Utils.countBits(mobility_34p);
                max = max * mobility_34p_count / 8;
                mobility += mobility_34p_count;
                if (max > 0) {
                    long mobility_567p = ALL_OFFICER_MOVES_567P[fieldID] & availableFields;
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
            long moves = ALL_OFFICER_MOVES[field_normalized_id];
            String result = "Field[" + i + ": " + Fields.ALL_ORDERED_NAMES[i] + "]= ";
            int j = Bits.nextSetBit_L2R(0, moves);
            while (j <= 63 && j != -1) {
                result = result + Fields.ALL_ORDERED_NAMES[j] + " ";
                j = Bits.nextSetBit_L2R(j + 1, moves);
            }
        }
    }

    public static void main(String[] args) {
        OfficerPlies.genMembers();
    }

    private static void genMembers() {
        int letter;
        int digit;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digits = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                String prefix1 = "public static final long[][] ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + " = new long[][] {";
                String prefix2 = "public static final int[] ALL_OFFICER_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + " = new int[] {";
                String prefix3 = "public static final long ALL_OFFICER_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                Object result1 = prefix1;
                Object result2 = prefix2;
                String result3 = prefix3;
                String prefix = "public static final long ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                int dir_letter = letter + 1;
                int dir_digit = digit + 1;
                if (letter == 7 || digit == 7) {
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
                        ++dir_letter;
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
                result3 = result3 + "ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter + 1;
                dir_digit = digit - 1;
                if (letter == 7 || digit == 0) {
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
                result3 = result3 + "ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter - 1;
                dir_digit = digit - 1;
                if (letter == 0 || digit == 0) {
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
                        --dir_letter;
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
                result3 = result3 + "ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + " | ";
                prefix = "public static final long ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + " = ";
                result = prefix;
                dir_letter = letter - 1;
                dir_digit = digit + 1;
                if (letter == 0 || digit == 7) {
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
                result3 = result3 + "ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ";";
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
        result = "public static final long[] ALL_ORDERED_OFFICER_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR0_OFFICER_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_DIR0_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR1_OFFICER_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_DIR1_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR2_OFFICER_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_DIR2_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[] ALL_ORDERED_DIR3_OFFICER_MOVES = new long[] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_DIR3_MOVES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final long[][][] ALL_ORDERED_OFFICER_DIRS = new long[][][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final int[][] ALL_ORDERED_OFFICER_VALID_DIRS = new int[][] {";
        for (digit = 0; digit < 8; ++digit) {
            for (letter = 0; letter < 8; ++letter) {
                result = (String)result + "ALL_OFFICER_VALID_DIR_INDEXES_FROM_" + letters[letter] + digits[digit] + ", ";
            }
        }
        result = (String)result + "};";
        System.out.println((String)result);
    }

    static {
        int i;
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A1 = new long[][]{{0x40000000000000L, 0x200000000000L, 0x1000000000L, 0x8000000L, 262144L, 512L, 1L}, new long[0], new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A1 = new int[]{0};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B1 = new long[][]{{0x20000000000000L, 0x100000000000L, 0x800000000L, 0x4000000L, 131072L, 256L}, new long[0], new long[0], {0x80000000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C1 = new long[][]{{0x10000000000000L, 0x80000000000L, 0x400000000L, 0x2000000L, 65536L}, new long[0], new long[0], {0x40000000000000L, 0x800000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D1 = new long[][]{{0x8000000000000L, 0x40000000000L, 0x200000000L, 0x1000000L}, new long[0], new long[0], {0x20000000000000L, 0x400000000000L, 0x8000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E1 = new long[][]{{0x4000000000000L, 0x20000000000L, 0x100000000L}, new long[0], new long[0], {0x10000000000000L, 0x200000000000L, 0x4000000000L, 0x80000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F1 = new long[][]{{0x2000000000000L, 0x10000000000L}, new long[0], new long[0], {0x8000000000000L, 0x100000000000L, 0x2000000000L, 0x40000000L, 0x800000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G1 = new long[][]{{0x1000000000000L}, new long[0], new long[0], {0x4000000000000L, 0x80000000000L, 0x1000000000L, 0x20000000L, 0x400000L, 32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G1 = new int[]{0, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H1 = new long[][]{new long[0], new long[0], new long[0], {0x2000000000000L, 0x40000000000L, 0x800000000L, 0x10000000L, 0x200000L, 16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H1 = new int[]{3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A2 = new long[][]{{0x400000000000L, 0x2000000000L, 0x10000000L, 524288L, 1024L, 2L}, {0x4000000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A2 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B2 = new long[][]{{0x200000000000L, 0x1000000000L, 0x8000000L, 262144L, 512L, 1L}, {0x2000000000000000L}, {Long.MIN_VALUE}, {0x800000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C2 = new long[][]{{0x100000000000L, 0x800000000L, 0x4000000L, 131072L, 256L}, {0x1000000000000000L}, {0x4000000000000000L}, {0x400000000000L, 0x8000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D2 = new long[][]{{0x80000000000L, 0x400000000L, 0x2000000L, 65536L}, {0x800000000000000L}, {0x2000000000000000L}, {0x200000000000L, 0x4000000000L, 0x80000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E2 = new long[][]{{0x40000000000L, 0x200000000L, 0x1000000L}, {0x400000000000000L}, {0x1000000000000000L}, {0x100000000000L, 0x2000000000L, 0x40000000L, 0x800000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F2 = new long[][]{{0x20000000000L, 0x100000000L}, {0x200000000000000L}, {0x800000000000000L}, {0x80000000000L, 0x1000000000L, 0x20000000L, 0x400000L, 32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G2 = new long[][]{{0x10000000000L}, {0x100000000000000L}, {0x400000000000000L}, {0x40000000000L, 0x800000000L, 0x10000000L, 0x200000L, 16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G2 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H2 = new long[][]{new long[0], new long[0], {0x200000000000000L}, {0x20000000000L, 0x400000000L, 0x8000000L, 0x100000L, 8192L, 64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H2 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A3 = new long[][]{{0x4000000000L, 0x20000000L, 0x100000L, 2048L, 4L}, {0x40000000000000L, 0x2000000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A3 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B3 = new long[][]{{0x2000000000L, 0x10000000L, 524288L, 1024L, 2L}, {0x20000000000000L, 0x1000000000000000L}, {0x80000000000000L}, {0x8000000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C3 = new long[][]{{0x1000000000L, 0x8000000L, 262144L, 512L, 1L}, {0x10000000000000L, 0x800000000000000L}, {0x40000000000000L, Long.MIN_VALUE}, {0x4000000000L, 0x80000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D3 = new long[][]{{0x800000000L, 0x4000000L, 131072L, 256L}, {0x8000000000000L, 0x400000000000000L}, {0x20000000000000L, 0x4000000000000000L}, {0x2000000000L, 0x40000000L, 0x800000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E3 = new long[][]{{0x400000000L, 0x2000000L, 65536L}, {0x4000000000000L, 0x200000000000000L}, {0x10000000000000L, 0x2000000000000000L}, {0x1000000000L, 0x20000000L, 0x400000L, 32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F3 = new long[][]{{0x200000000L, 0x1000000L}, {0x2000000000000L, 0x100000000000000L}, {0x8000000000000L, 0x1000000000000000L}, {0x800000000L, 0x10000000L, 0x200000L, 16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G3 = new long[][]{{0x100000000L}, {0x1000000000000L}, {0x4000000000000L, 0x800000000000000L}, {0x400000000L, 0x8000000L, 0x100000L, 8192L, 64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G3 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H3 = new long[][]{new long[0], new long[0], {0x2000000000000L, 0x400000000000000L}, {0x200000000L, 0x4000000L, 524288L, 4096L, 32L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H3 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A4 = new long[][]{{0x40000000L, 0x200000L, 4096L, 8L}, {0x400000000000L, 0x20000000000000L, 0x1000000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A4 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B4 = new long[][]{{0x20000000L, 0x100000L, 2048L, 4L}, {0x200000000000L, 0x10000000000000L, 0x800000000000000L}, {0x800000000000L}, {0x80000000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C4 = new long[][]{{0x10000000L, 524288L, 1024L, 2L}, {0x100000000000L, 0x8000000000000L, 0x400000000000000L}, {0x400000000000L, 0x80000000000000L}, {0x40000000L, 0x800000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D4 = new long[][]{{0x8000000L, 262144L, 512L, 1L}, {0x80000000000L, 0x4000000000000L, 0x200000000000000L}, {0x200000000000L, 0x40000000000000L, Long.MIN_VALUE}, {0x20000000L, 0x400000L, 32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E4 = new long[][]{{0x4000000L, 131072L, 256L}, {0x40000000000L, 0x2000000000000L, 0x100000000000000L}, {0x100000000000L, 0x20000000000000L, 0x4000000000000000L}, {0x10000000L, 0x200000L, 16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F4 = new long[][]{{0x2000000L, 65536L}, {0x20000000000L, 0x1000000000000L}, {0x80000000000L, 0x10000000000000L, 0x2000000000000000L}, {0x8000000L, 0x100000L, 8192L, 64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G4 = new long[][]{{0x1000000L}, {0x10000000000L}, {0x40000000000L, 0x8000000000000L, 0x1000000000000000L}, {0x4000000L, 524288L, 4096L, 32L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G4 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H4 = new long[][]{new long[0], new long[0], {0x20000000000L, 0x4000000000000L, 0x800000000000000L}, {0x2000000L, 262144L, 2048L, 16L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H4 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A5 = new long[][]{{0x400000L, 8192L, 16L}, {0x4000000000L, 0x200000000000L, 0x10000000000000L, 0x800000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A5 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B5 = new long[][]{{0x200000L, 4096L, 8L}, {0x2000000000L, 0x100000000000L, 0x8000000000000L, 0x400000000000000L}, {0x8000000000L}, {0x800000L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C5 = new long[][]{{0x100000L, 2048L, 4L}, {0x1000000000L, 0x80000000000L, 0x4000000000000L, 0x200000000000000L}, {0x4000000000L, 0x800000000000L}, {0x400000L, 32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D5 = new long[][]{{524288L, 1024L, 2L}, {0x800000000L, 0x40000000000L, 0x2000000000000L, 0x100000000000000L}, {0x2000000000L, 0x400000000000L, 0x80000000000000L}, {0x200000L, 16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E5 = new long[][]{{262144L, 512L, 1L}, {0x400000000L, 0x20000000000L, 0x1000000000000L}, {0x1000000000L, 0x200000000000L, 0x40000000000000L, Long.MIN_VALUE}, {0x100000L, 8192L, 64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F5 = new long[][]{{131072L, 256L}, {0x200000000L, 0x10000000000L}, {0x800000000L, 0x100000000000L, 0x20000000000000L, 0x4000000000000000L}, {524288L, 4096L, 32L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G5 = new long[][]{{65536L}, {0x100000000L}, {0x400000000L, 0x80000000000L, 0x10000000000000L, 0x2000000000000000L}, {262144L, 2048L, 16L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G5 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H5 = new long[][]{new long[0], new long[0], {0x200000000L, 0x40000000000L, 0x8000000000000L, 0x1000000000000000L}, {131072L, 1024L, 8L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H5 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A6 = new long[][]{{16384L, 32L}, {0x40000000L, 0x2000000000L, 0x100000000000L, 0x8000000000000L, 0x400000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A6 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B6 = new long[][]{{8192L, 16L}, {0x20000000L, 0x1000000000L, 0x80000000000L, 0x4000000000000L, 0x200000000000000L}, {0x80000000L}, {32768L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C6 = new long[][]{{4096L, 8L}, {0x10000000L, 0x800000000L, 0x40000000000L, 0x2000000000000L, 0x100000000000000L}, {0x40000000L, 0x8000000000L}, {16384L, 128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D6 = new long[][]{{2048L, 4L}, {0x8000000L, 0x400000000L, 0x20000000000L, 0x1000000000000L}, {0x20000000L, 0x4000000000L, 0x800000000000L}, {8192L, 64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E6 = new long[][]{{1024L, 2L}, {0x4000000L, 0x200000000L, 0x10000000000L}, {0x10000000L, 0x2000000000L, 0x400000000000L, 0x80000000000000L}, {4096L, 32L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F6 = new long[][]{{512L, 1L}, {0x2000000L, 0x100000000L}, {0x8000000L, 0x1000000000L, 0x200000000000L, 0x40000000000000L, Long.MIN_VALUE}, {2048L, 16L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G6 = new long[][]{{256L}, {0x1000000L}, {0x4000000L, 0x800000000L, 0x100000000000L, 0x20000000000000L, 0x4000000000000000L}, {1024L, 8L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G6 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H6 = new long[][]{new long[0], new long[0], {0x2000000L, 0x400000000L, 0x80000000000L, 0x10000000000000L, 0x2000000000000000L}, {512L, 4L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H6 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A7 = new long[][]{{64L}, {0x400000L, 0x20000000L, 0x1000000000L, 0x80000000000L, 0x4000000000000L, 0x200000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A7 = new int[]{0, 1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B7 = new long[][]{{32L}, {0x200000L, 0x10000000L, 0x800000000L, 0x40000000000L, 0x2000000000000L, 0x100000000000000L}, {0x800000L}, {128L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C7 = new long[][]{{16L}, {0x100000L, 0x8000000L, 0x400000000L, 0x20000000000L, 0x1000000000000L}, {0x400000L, 0x80000000L}, {64L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D7 = new long[][]{{8L}, {524288L, 0x4000000L, 0x200000000L, 0x10000000000L}, {0x200000L, 0x40000000L, 0x8000000000L}, {32L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E7 = new long[][]{{4L}, {262144L, 0x2000000L, 0x100000000L}, {0x100000L, 0x20000000L, 0x4000000000L, 0x800000000000L}, {16L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F7 = new long[][]{{2L}, {131072L, 0x1000000L}, {524288L, 0x10000000L, 0x2000000000L, 0x400000000000L, 0x80000000000000L}, {8L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G7 = new long[][]{{1L}, {65536L}, {262144L, 0x8000000L, 0x1000000000L, 0x200000000000L, 0x40000000000000L, Long.MIN_VALUE}, {4L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G7 = new int[]{0, 1, 2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H7 = new long[][]{new long[0], new long[0], {131072L, 0x4000000L, 0x800000000L, 0x100000000000L, 0x20000000000000L, 0x4000000000000000L}, {2L}};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H7 = new int[]{2, 3};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A8 = new long[][]{new long[0], {16384L, 0x200000L, 0x10000000L, 0x800000000L, 0x40000000000L, 0x2000000000000L, 0x100000000000000L}, new long[0], new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_A8 = new int[]{1};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B8 = new long[][]{new long[0], {8192L, 0x100000L, 0x8000000L, 0x400000000L, 0x20000000000L, 0x1000000000000L}, {32768L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_B8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C8 = new long[][]{new long[0], {4096L, 524288L, 0x4000000L, 0x200000000L, 0x10000000000L}, {16384L, 0x800000L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_C8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D8 = new long[][]{new long[0], {2048L, 262144L, 0x2000000L, 0x100000000L}, {8192L, 0x400000L, 0x80000000L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_D8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E8 = new long[][]{new long[0], {1024L, 131072L, 0x1000000L}, {4096L, 0x200000L, 0x40000000L, 0x8000000000L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_E8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F8 = new long[][]{new long[0], {512L, 65536L}, {2048L, 0x100000L, 0x20000000L, 0x4000000000L, 0x800000000000L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_F8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G8 = new long[][]{new long[0], {256L}, {1024L, 524288L, 0x10000000L, 0x2000000000L, 0x400000000000L, 0x80000000000000L}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_G8 = new int[]{1, 2};
        ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H8 = new long[][]{new long[0], new long[0], {512L, 262144L, 0x8000000L, 0x1000000000L, 0x200000000000L, 0x40000000000000L, Long.MIN_VALUE}, new long[0]};
        ALL_OFFICER_VALID_DIR_INDEXES_FROM_H8 = new int[]{2};
        ALL_ORDERED_OFFICER_MOVES = new long[]{18049651735527937L, 45053622886727936L, 22667548931719168L, 11334324221640704L, 5667164249915392L, 2833579985862656L, 1416240237150208L, 567382630219904L, 4611756524879479810L, -6917353036926680575L, 5764696068147249408L, 2882348036221108224L, 1441174018118909952L, 720587009051099136L, 360293502378066048L, 144117404414255168L, 2323857683139004420L, 1197958188344280066L, -8624392940535152127L, 4911175566595588352L, 2455587783297826816L, 1227793891648880768L, 577868148797087808L, 288793334762704928L, 1161999073681608712L, 581140276476643332L, 326598935265674242L, -9060072569221905919L, 4693335752243822976L, 2310639079102947392L, 1155178802063085600L, 577588851267340304L, 580999811184992272L, 290500455356698632L, 145390965166737412L, 108724279602332802L, -9205038694072573375L, 4620711952330133792L, 2310355426409252880L, 1155177711057110024L, 290499906664153120L, 145249955479592976L, 72625527495610504L, 424704217196612L, 36100411639206946L, -9205322380790986223L, 4620710844311799048L, 2310355422147510788L, 145249953336262720L, 72624976676520096L, 283693466779728L, 1659000848424L, 141017232965652L, 36099303487963146L, -9205322385119182843L, 4620710844295151618L, 72624976668147712L, 283691315142656L, 1108177604608L, 6480472064L, 550848566272L, 141012904249856L, 36099303471056128L, -9205322385119247872L};
        ALL_ORDERED_DIR0_OFFICER_MOVES = new long[]{18049651735527937L, 9024825867763968L, 4512412933881856L, 2256206466908160L, 1128103225065472L, 0x2010000000000L, 0x1000000000000L, 0L, 70506452091906L, 35253226045953L, 17626613022976L, 8813306511360L, 4406653222912L, 0x20100000000L, 0x10000000000L, 0L, 275415828484L, 137707914242L, 68853957121L, 34426978560L, 17213489152L, 0x201000000L, 0x100000000L, 0L, 1075843080L, 537921540L, 268960770L, 134480385L, 67240192L, 0x2010000L, 0x1000000L, 0L, 4202512L, 2101256L, 1050628L, 525314L, 262657L, 131328L, 65536L, 0L, 16416L, 8208L, 4104L, 2052L, 1026L, 513L, 256L, 0L, 64L, 32L, 16L, 8L, 4L, 2L, 1L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_DIR1_OFFICER_MOVES = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, 0L, 0x2040000000000000L, 0x1020000000000000L, 0x810000000000000L, 0x408000000000000L, 0x204000000000000L, 0x102000000000000L, 0x1000000000000L, 0L, 1161999072605765632L, 580999536302882816L, 290499768151441408L, 145249884075720704L, 72624942037860352L, 0x1020000000000L, 0x10000000000L, 0L, 580999811180789760L, 290499905590394880L, 145249952795197440L, 72624976397598720L, 283691179835392L, 0x10200000000L, 0x100000000L, 0L, 290499906664136704L, 145249953332068352L, 72624976666034176L, 283691314053120L, 1108168671232L, 0x102000000L, 0x1000000L, 0L, 145249953336262656L, 72624976668131328L, 283691315101696L, 1108169195520L, 4328783872L, 0x1020000L, 65536L, 0L, 72624976668147712L, 283691315109888L, 1108169199616L, 4328785920L, 16909312L, 66048L, 256L, 0L};
        ALL_ORDERED_DIR2_OFFICER_MOVES = new long[]{0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0L, 0x80000000000000L, -9205357638345293824L, 0x4020000000000000L, 0x2010000000000000L, 0x1008000000000000L, 0x804000000000000L, 0x402000000000000L, 0L, 0x800000000000L, 0x80400000000000L, -9205322453973204992L, 4620710809868173312L, 2310355404934086656L, 1155177702467043328L, 577588851233521664L, 0L, 0x8000000000L, 0x804000000000L, 36099303202095104L, -9205322385253728256L, 4620710844227911680L, 2310355422113955840L, 1155177711056977920L, 0L, 0x80000000L, 0x8040000000L, 141012903133184L, 36099303470530560L, -9205322385119510528L, 4620710844295020544L, 2310355422147510272L, 0L, 0x800000L, 0x80400000L, 550831652864L, 141012904181760L, 36099303471054848L, -9205322385119248384L, 4620710844295151616L, 0L, 32768L, 0x804000L, 2151686144L, 550831656960L, 141012904183808L, 36099303471055872L, -9205322385119247872L};
        ALL_ORDERED_DIR3_OFFICER_MOVES = new long[]{0L, 0x80000000000000L, 0x40800000000000L, 9078117754732544L, 4539061024849920L, 2269530520813568L, 1134765260439552L, 567382630219904L, 0L, 0x800000000000L, 0x408000000000L, 35461397479424L, 17730707128320L, 8865353596928L, 4432676798592L, 2216338399296L, 0L, 0x8000000000L, 0x4080000000L, 138521083904L, 69260574720L, 34630287488L, 17315143744L, 8657571872L, 0L, 0x80000000L, 0x40800000L, 541097984L, 270549120L, 135274560L, 67637280L, 33818640L, 0L, 0x800000L, 0x408000L, 2113664L, 1056832L, 528416L, 264208L, 132104L, 0L, 32768L, 16512L, 8256L, 4128L, 2064L, 1032L, 516L, 0L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L};
        ALL_ORDERED_OFFICER_DIRS = new long[][][]{ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H1, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H2, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H3, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H4, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H5, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H6, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H7, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_A8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_B8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_C8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_D8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_E8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_F8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_G8, ALL_OFFICER_MOVES_BY_DIR_AND_SEQ_FROM_H8};
        ALL_ORDERED_OFFICER_VALID_DIRS = new int[][]{ALL_OFFICER_VALID_DIR_INDEXES_FROM_A1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H1, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H2, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H3, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H4, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H5, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H6, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H7, ALL_OFFICER_VALID_DIR_INDEXES_FROM_A8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_B8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_C8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_D8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_E8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_F8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_G8, ALL_OFFICER_VALID_DIR_INDEXES_FROM_H8};
        ALL_OFFICER_MOVES = new long[64];
        ALL_OFFICER_DIR_MOVES = new long[4][64];
        ALL_OFFICER_DIR0_MOVES = new long[64];
        ALL_OFFICER_DIR1_MOVES = new long[64];
        ALL_OFFICER_DIR2_MOVES = new long[64];
        ALL_OFFICER_DIR3_MOVES = new long[64];
        ALL_OFFICER_MOVES_1P = new long[64];
        ALL_OFFICER_MOVES_2P = new long[64];
        ALL_OFFICER_MOVES_34P = new long[64];
        ALL_OFFICER_MOVES_567P = new long[64];
        ALL_OFFICER_VALID_DIRS = new int[64][];
        ALL_OFFICER_DIRS_WITH_FIELD_IDS = new int[64][][];
        ALL_OFFICER_DIRS_WITH_BITBOARDS = new long[64][][];
        PATHS = new long[64][64];
        W_MAGIC = Utils.reverseSpecial(new int[]{-20, -15, -15, -13, -13, -15, -15, -20, -5, 0, -5, 0, 0, -5, 0, -5, -6, -2, 4, 2, 2, 4, -2, -6, -4, 0, 2, 10, 10, 2, 0, -4, -4, 0, 2, 10, 10, 2, 0, -4, -6, -2, 4, 2, 2, 4, -2, -6, -5, 0, -2, 0, 0, -2, 0, -5, -8, -8, -6, -4, -4, -6, -8, -8});
        B_MAGIC = Utils.reverseSpecial(new int[]{-8, -8, -6, -4, -4, -6, -8, -8, -5, 0, -5, 0, 0, -5, 0, -5, -6, -2, 4, 2, 2, 4, -2, -6, -4, 0, 2, 10, 10, 2, 0, -4, -4, 0, 2, 10, 10, 2, 0, -4, -6, -2, 4, 2, 2, 4, -2, -6, -5, 0, -5, 0, 0, -5, 0, -5, -20, -15, -15, -13, -13, -15, -15, -20});
        for (i = 0; i < ALL_ORDERED_OFFICER_MOVES.length; ++i) {
            int idx = Fields.IDX_ORDERED_2_A1H1[i];
            long fieldMoves = ALL_ORDERED_OFFICER_MOVES[i];
            long[][] dirs = ALL_ORDERED_OFFICER_DIRS[i];
            OfficerPlies.ALL_OFFICER_MOVES[idx] = fieldMoves;
            OfficerPlies.ALL_OFFICER_VALID_DIRS[idx] = ALL_ORDERED_OFFICER_VALID_DIRS[i];
            OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[idx] = dirs;
            OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[idx] = OfficerPlies.bitboards2fieldIDs(dirs);
            OfficerPlies.ALL_OFFICER_DIR0_MOVES[idx] = ALL_ORDERED_DIR0_OFFICER_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR1_MOVES[idx] = ALL_ORDERED_DIR1_OFFICER_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR2_MOVES[idx] = ALL_ORDERED_DIR2_OFFICER_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR3_MOVES[idx] = ALL_ORDERED_DIR3_OFFICER_MOVES[i];
            for (int dirID : ALL_OFFICER_VALID_DIRS[idx]) {
                long[] dirBitboards = dirs[dirID];
                for (int seq = 0; seq < dirBitboards.length; ++seq) {
                    long toBitboard = dirs[dirID][seq];
                    if (seq == 0) {
                        int n = idx;
                        ALL_OFFICER_MOVES_1P[n] = ALL_OFFICER_MOVES_1P[n] | toBitboard;
                        continue;
                    }
                    if (seq == 1) {
                        int n = idx;
                        ALL_OFFICER_MOVES_2P[n] = ALL_OFFICER_MOVES_2P[n] | toBitboard;
                        continue;
                    }
                    if (seq == 2 || seq == 3) {
                        int n = idx;
                        ALL_OFFICER_MOVES_34P[n] = ALL_OFFICER_MOVES_34P[n] | toBitboard;
                        continue;
                    }
                    int n = idx;
                    ALL_OFFICER_MOVES_567P[n] = ALL_OFFICER_MOVES_567P[n] | toBitboard;
                }
            }
        }
        for (int from = 0; from < ALL_ORDERED_OFFICER_MOVES.length; ++from) {
            for (int to = 0; to < ALL_ORDERED_OFFICER_MOVES.length; ++to) {
                int fromID = Fields.IDX_ORDERED_2_A1H1[from];
                long fromAttacks = ALL_OFFICER_MOVES[fromID];
                int toID = Fields.IDX_ORDERED_2_A1H1[to];
                long toBitboard = ALL_A1H1[toID];
                if ((fromAttacks & toBitboard) != 0L) {
                    long[] fieldBiboards;
                    int[] fieldIDs;
                    if ((ALL_OFFICER_DIR0_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][0];
                        fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][0];
                    } else if ((ALL_OFFICER_DIR1_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][1];
                        fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][1];
                    } else if ((ALL_OFFICER_DIR2_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][2];
                        fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][2];
                    } else if ((ALL_OFFICER_DIR3_MOVES[fromID] & toBitboard) != 0L) {
                        fieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromID][3];
                        fieldBiboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromID][3];
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
                OfficerPlies.PATHS[fromID][toID] = -1L;
            }
        }
        for (i = 0; i < 64; ++i) {
            OfficerPlies.ALL_OFFICER_DIR_MOVES[0][i] = ALL_OFFICER_DIR0_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR_MOVES[1][i] = ALL_OFFICER_DIR1_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR_MOVES[2][i] = ALL_OFFICER_DIR2_MOVES[i];
            OfficerPlies.ALL_OFFICER_DIR_MOVES[3][i] = ALL_OFFICER_DIR3_MOVES[i];
        }
        OfficerPlies.verify();
    }
}

