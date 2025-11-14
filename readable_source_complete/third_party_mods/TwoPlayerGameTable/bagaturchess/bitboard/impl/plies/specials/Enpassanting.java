/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.specials;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class Enpassanting
extends Fields {
    public static final int ENPASSANT_DIR_ID = 1;
    public static final long[][] ADJOINING_FILES = new long[Figures.COLOUR_MAX][64];
    public static final long[][][] ADJOINING_FILE_BITBOARD_AT_CAPTURE;
    public static final int[][][] ADJOINING_FILE_FIELD_ID_AT_CAPTURE;

    public static long getEnemyBitboard(String enpassantTargetSquar) {
        int enpassTargetFieldID = Enpassanting.getFieldID(enpassantTargetSquar);
        long enpassTargetBoard = Fields.ALL_ORDERED_A1H1[enpassTargetFieldID];
        if (enpassTargetFieldID >= 16 && enpassTargetFieldID <= 23) {
            enpassTargetBoard >>= 8;
        } else if (enpassTargetFieldID >= 40 && enpassTargetFieldID <= 47) {
            enpassTargetBoard <<= 8;
        } else {
            throw new IllegalStateException("enpassTargetFieldID=" + enpassTargetFieldID);
        }
        return enpassTargetBoard;
    }

    public static int converteEnpassantTargetToFENFormat(long enpassantTargetSquare) {
        int enpassTargetFieldID = Enpassanting.get67IDByBitboard(enpassantTargetSquare);
        enpassTargetFieldID = enpassTargetFieldID <= 31 ? (enpassTargetFieldID -= 8) : (enpassTargetFieldID += 8);
        return enpassTargetFieldID;
    }

    static {
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x8000000000L)] = 0x4000000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x4000000000L)] = 0xA000000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x2000000000L)] = 0x5000000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x1000000000L)] = 0x2800000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x800000000L)] = 0x1400000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x400000000L)] = 0xA00000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x200000000L)] = 0x500000000L;
        Enpassanting.ADJOINING_FILES[0][Enpassanting.get67IDByBitboard((long)0x100000000L)] = 0x200000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x80000000L)] = 0x40000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x40000000L)] = 0xA0000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x20000000L)] = 0x50000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x10000000L)] = 0x28000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x8000000L)] = 0x14000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x4000000L)] = 0xA000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x2000000L)] = 0x5000000L;
        Enpassanting.ADJOINING_FILES[1][Enpassanting.get67IDByBitboard((long)0x1000000L)] = 0x2000000L;
        ADJOINING_FILE_BITBOARD_AT_CAPTURE = new long[Figures.COLOUR_MAX][64][2];
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x80000000L)][1] = 0x40000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x40000000L)][0] = 0x80000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x40000000L)][1] = 0x20000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x20000000L)][0] = 0x40000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x20000000L)][1] = 0x10000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x10000000L)][0] = 0x20000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x10000000L)][1] = 0x8000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x8000000L)][0] = 0x10000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x8000000L)][1] = 0x4000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x4000000L)][0] = 0x8000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x4000000L)][1] = 0x2000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x2000000L)][0] = 0x4000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x2000000L)][1] = 0x1000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x1000000L)][0] = 0x2000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x8000000000L)][1] = 0x4000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x4000000000L)][0] = 0x8000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x4000000000L)][1] = 0x2000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x2000000000L)][0] = 0x4000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x2000000000L)][1] = 0x1000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x1000000000L)][0] = 0x2000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x1000000000L)][1] = 0x800000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x800000000L)][0] = 0x1000000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x800000000L)][1] = 0x400000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x400000000L)][0] = 0x800000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x400000000L)][1] = 0x200000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x200000000L)][0] = 0x400000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x200000000L)][1] = 0x100000000L;
        Enpassanting.ADJOINING_FILE_BITBOARD_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x100000000L)][0] = 0x200000000L;
        ADJOINING_FILE_FIELD_ID_AT_CAPTURE = new int[Figures.COLOUR_MAX][64][2];
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x80000000L)][1] = Enpassanting.get67IDByBitboard(0x40000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x40000000L)][0] = Enpassanting.get67IDByBitboard(0x80000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x40000000L)][1] = Enpassanting.get67IDByBitboard(0x20000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x20000000L)][0] = Enpassanting.get67IDByBitboard(0x40000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x20000000L)][1] = Enpassanting.get67IDByBitboard(0x10000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x10000000L)][0] = Enpassanting.get67IDByBitboard(0x20000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x10000000L)][1] = Enpassanting.get67IDByBitboard(0x8000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x8000000L)][0] = Enpassanting.get67IDByBitboard(0x10000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x8000000L)][1] = Enpassanting.get67IDByBitboard(0x4000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x4000000L)][0] = Enpassanting.get67IDByBitboard(0x8000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x4000000L)][1] = Enpassanting.get67IDByBitboard(0x2000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x2000000L)][0] = Enpassanting.get67IDByBitboard(0x4000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x2000000L)][1] = Enpassanting.get67IDByBitboard(0x1000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[0][Enpassanting.get67IDByBitboard((long)0x1000000L)][0] = Enpassanting.get67IDByBitboard(0x2000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x8000000000L)][1] = Enpassanting.get67IDByBitboard(0x4000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x4000000000L)][0] = Enpassanting.get67IDByBitboard(0x8000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x4000000000L)][1] = Enpassanting.get67IDByBitboard(0x2000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x2000000000L)][0] = Enpassanting.get67IDByBitboard(0x4000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x2000000000L)][1] = Enpassanting.get67IDByBitboard(0x1000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x1000000000L)][0] = Enpassanting.get67IDByBitboard(0x2000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x1000000000L)][1] = Enpassanting.get67IDByBitboard(0x800000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x800000000L)][0] = Enpassanting.get67IDByBitboard(0x1000000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x800000000L)][1] = Enpassanting.get67IDByBitboard(0x400000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x400000000L)][0] = Enpassanting.get67IDByBitboard(0x800000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x400000000L)][1] = Enpassanting.get67IDByBitboard(0x200000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x200000000L)][0] = Enpassanting.get67IDByBitboard(0x400000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x200000000L)][1] = Enpassanting.get67IDByBitboard(0x100000000L);
        Enpassanting.ADJOINING_FILE_FIELD_ID_AT_CAPTURE[1][Enpassanting.get67IDByBitboard((long)0x100000000L)][0] = Enpassanting.get67IDByBitboard(0x200000000L);
    }
}

