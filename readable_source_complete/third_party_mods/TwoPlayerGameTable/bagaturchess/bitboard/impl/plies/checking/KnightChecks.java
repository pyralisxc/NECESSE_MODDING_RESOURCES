/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.plies.KnightPlies;

public class KnightChecks
extends KnightPlies {
    public static int[][][] CHECK_MIDDLE_FIELDS_IDS = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_DIR_ID = new int[64][64][];
    public static long[][][] CHECK_MIDDLE_FIELDS_BITBOARDS = new long[64][64][];
    public static long[] FIELDS_ATTACK_2 = new long[64];

    public static void initPair_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = ALL_KNIGHT_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = ALL_KNIGHT_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int middleFieldID = middleFieldIDs[dirID1][0];
            long middleFieldBitboard = middleFieldBitboards[dirID1][0];
            int[] middleFieldValidDirIDs = ALL_KNIGHT_VALID_DIRS[middleFieldID];
            int[][] toFieldIDs = ALL_KNIGHT_DIRS_WITH_FIELD_IDS[middleFieldID];
            long[][] toFieldBitboards = ALL_KNIGHT_DIRS_WITH_BITBOARDS[middleFieldID];
            for (int dirID2 : middleFieldValidDirIDs) {
                int cur_toFieldID = toFieldIDs[dirID2][0];
                long endFieldBitboard = toFieldBitboards[dirID2][0];
                int n = fromFieldID;
                FIELDS_ATTACK_2[n] = FIELDS_ATTACK_2[n] | endFieldBitboard;
                if (toFieldID != cur_toFieldID) continue;
                int[] list1 = CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
                int[] list2 = CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID];
                long[] list3 = CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
                if (list1 == null) {
                    KnightChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = new int[1];
                    KnightChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][0] = middleFieldID;
                    KnightChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = new int[1];
                    KnightChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] = dirID1;
                    KnightChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = new long[1];
                    KnightChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][0] = middleFieldBitboard;
                    continue;
                }
                if (list1.length == 1) {
                    KnightChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = new int[2];
                    KnightChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][0] = list1[0];
                    KnightChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID][1] = middleFieldID;
                    KnightChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = new int[2];
                    KnightChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] = list2[0];
                    KnightChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][1] = dirID1;
                    KnightChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = new long[2];
                    KnightChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][0] = list3[0];
                    KnightChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID][1] = middleFieldBitboard;
                    continue;
                }
                throw new IllegalStateException();
            }
        }
    }

    public static void genAll_Dynamic() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = KnightChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = KnightChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID) continue;
                KnightChecks.initPair_Dynamic(fromID, toID);
            }
        }
    }

    public static String testChecks(int fromFieldID, int toFieldID) {
        String result = "Knight checks from " + KnightChecks.getFieldSign_UC(fromFieldID) + " to " + KnightChecks.getFieldSign_UC(toFieldID) + " -> ";
        int[] fields = CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
        long[] fieldBoards = CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
        if (fields == null && fieldBoards != null) {
            throw new IllegalStateException();
        }
        if (fields != null && fieldBoards == null) {
            throw new IllegalStateException();
        }
        if (fields != null) {
            if (fields.length != fieldBoards.length) {
                throw new IllegalStateException();
            }
            for (int i = 0; i < fields.length; ++i) {
                if (ALL_A1H1[fields[i]] == fieldBoards[i]) continue;
                throw new IllegalStateException(Bits.toBinaryStringMatrix(fieldBoards[i]));
            }
        }
        if (fields == null) {
            result = result + "NO";
        } else if (fields.length == 1) {
            result = result + KnightChecks.getFieldSign_UC(fields[0]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] + ")";
        } else if (fields.length == 2) {
            result = result + KnightChecks.getFieldSign_UC(fields[0]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][0] + ") and " + KnightChecks.getFieldSign_UC(fields[1]) + "(dir" + CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID][1] + ")";
        } else {
            throw new IllegalStateException();
        }
        return result;
    }

    public static void testAll() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = KnightChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = KnightChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                System.out.println(KnightChecks.testChecks(fromID, toID));
            }
        }
    }

    public static void main(String[] args) {
        int from = KnightChecks.get67IDByBitboard(0x800000000000000L);
        int to = KnightChecks.get67IDByBitboard(0x8000000L);
        System.out.println(KnightChecks.testChecks(from, to));
        System.out.println(Bits.toBinaryStringMatrix(FIELDS_ATTACK_2[from]));
    }

    static {
        KnightChecks.genAll_Dynamic();
    }
}

