/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.plies;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class Enpassanting
extends Fields {
    public static final int ENPASSANT_DIR_ID = 1;
    public static final int[][][] ADJOINING_FILES_FIELD_IDS = new int[Figures.COLOUR_MAX][64][];
    public static final int[][][] ADJOINING_FILE_FIELD_ID_AT_CAPTURE;

    public static int getEnemyFieldID(String enpassantTargetSquar) {
        int enpassTargetFieldID = Enpassanting.getFieldID(enpassantTargetSquar);
        if (enpassTargetFieldID >= 16 && enpassTargetFieldID <= 23) {
            enpassTargetFieldID -= 8;
        } else if (enpassTargetFieldID >= 40 && enpassTargetFieldID <= 47) {
            enpassTargetFieldID += 8;
        } else {
            throw new IllegalStateException("enpassTargetFieldID=" + enpassTargetFieldID);
        }
        return enpassTargetFieldID;
    }

    public static int converteEnpassantTargetToFENFormat(long enpassantTargetSquare) {
        int enpassTargetFieldID = Enpassanting.get67IDByBitboard(enpassantTargetSquare);
        enpassTargetFieldID = enpassTargetFieldID <= 31 ? (enpassTargetFieldID -= 8) : (enpassTargetFieldID += 8);
        return enpassTargetFieldID;
    }

    static {
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][24] = new int[]{25};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][25] = new int[]{24, 26};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][26] = new int[]{25, 27};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][27] = new int[]{26, 28};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][28] = new int[]{27, 29};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][29] = new int[]{28, 30};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][30] = new int[]{29, 31};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[0][31] = new int[]{30};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][32] = new int[]{33};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][33] = new int[]{32, 34};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][34] = new int[]{33, 35};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][35] = new int[]{34, 36};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][36] = new int[]{35, 37};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][37] = new int[]{36, 38};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][38] = new int[]{37, 39};
        Enpassanting.ADJOINING_FILES_FIELD_IDS[1][39] = new int[]{38};
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

