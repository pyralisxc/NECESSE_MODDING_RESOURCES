/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class OfficerChecks
extends OfficerPlies {
    public static int[][][] CHECK_MIDDLE_FIELDS_IDS = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_DIR_ID = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_SEQS = new int[64][64][];
    public static long[][][] CHECK_MIDDLE_FIELDS_BITBOARDS = new long[64][64][];
    public static long[][][] FIELDS_PATH1 = new long[64][64][];
    public static long[][][] FIELDS_PATH2 = new long[64][64][];
    public static long[][][] FIELDS_WHOLE_PATH = new long[64][64][];

    public static void initPair_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = ALL_OFFICER_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
            long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
            long path1 = 0L;
            for (int seq1 = 0; seq1 < middleFieldIDsByDir.length; ++seq1) {
                int middleFieldID = middleFieldIDsByDir[seq1];
                long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
                int[] middleFieldValidDirIDs = ALL_OFFICER_VALID_DIRS[middleFieldID];
                int[][] toFieldIDs = ALL_OFFICER_DIRS_WITH_FIELD_IDS[middleFieldID];
                long[][] toFieldBitboards = ALL_OFFICER_DIRS_WITH_BITBOARDS[middleFieldID];
                block2: for (int dirID2 : middleFieldValidDirIDs) {
                    int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
                    long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
                    long path2 = 0L;
                    for (int seq2 = 0; seq2 < cur_toFieldIDsByDir.length; ++seq2) {
                        int cur_toFieldID = cur_toFieldIDsByDir[seq2];
                        long endFieldBitboard = endFieldBitboardsByDir[seq2];
                        if ((path1 & endFieldBitboard) != 0L || cur_toFieldID == fromFieldID) continue block2;
                        if (toFieldID == cur_toFieldID) {
                            if (CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null || CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 5) {
                                OfficerChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = OfficerChecks.extendArray(CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
                                OfficerChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = OfficerChecks.extendArray(CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
                                OfficerChecks.CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID] = OfficerChecks.extendArray(CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
                                OfficerChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = OfficerChecks.extendArray(CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
                                if (PATHS[fromFieldID][middleFieldID] != path1) {
                                    throw new IllegalStateException();
                                }
                                if (PATHS[middleFieldID][toFieldID] != path2) {
                                    throw new IllegalStateException();
                                }
                                OfficerChecks.FIELDS_PATH1[fromFieldID][toFieldID] = OfficerChecks.extendArray(FIELDS_PATH1[fromFieldID][toFieldID], path1);
                                OfficerChecks.FIELDS_PATH2[fromFieldID][toFieldID] = OfficerChecks.extendArray(FIELDS_PATH2[fromFieldID][toFieldID], path2);
                                OfficerChecks.FIELDS_WHOLE_PATH[fromFieldID][toFieldID] = OfficerChecks.extendArray(FIELDS_WHOLE_PATH[fromFieldID][toFieldID], path1 | path2);
                                continue block2;
                            }
                            throw new IllegalStateException();
                        }
                        path2 |= endFieldBitboard;
                    }
                }
                path1 |= middleFieldBitboard;
            }
        }
    }

    private static long[] extendArray(long[] source, long el) {
        long[] result = null;
        if (source != null) {
            result = new long[source.length + 1];
            System.arraycopy(source, 0, result, 0, source.length);
            result[source.length] = el;
        } else {
            result = new long[]{el};
        }
        return result;
    }

    private static int[] extendArray(int[] source, int el) {
        int[] result = null;
        if (source != null) {
            result = new int[source.length + 1];
            System.arraycopy(source, 0, result, 0, source.length);
            result[source.length] = el;
        } else {
            result = new int[]{el};
        }
        return result;
    }

    public static void genAll_Dynamic() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = OfficerChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = OfficerChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID) continue;
                OfficerChecks.initPair_Dynamic(fromID, toID);
            }
        }
    }

    public static String testChecks(int fromFieldID, int toFieldID) {
        int i;
        String result = "Officer checks from " + OfficerChecks.getFieldSign_UC(fromFieldID) + " to " + OfficerChecks.getFieldSign_UC(toFieldID) + " -> ";
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
            for (i = 0; i < fields.length; ++i) {
                if (ALL_A1H1[fields[i]] == fieldBoards[i]) continue;
                throw new IllegalStateException("\r\n" + Bits.toBinaryStringMatrix(fieldBoards[i]));
            }
        }
        if (fields == null) {
            result = result + "NO";
        } else {
            if (fields.length > 6) {
                throw new IllegalStateException();
            }
            for (i = 0; i < fields.length; ++i) {
                result = result + OfficerChecks.getFieldSign_UC(fields[i]) + ", ";
            }
        }
        if (fields != null) {
            result = result + "\r\n";
            for (i = 0; i < fields.length; ++i) {
                result = result + Bits.toBinaryStringMatrix(FIELDS_WHOLE_PATH[fromFieldID][toFieldID][i]) + "\r\n";
            }
        }
        return result;
    }

    public static void testAll() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = OfficerChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = OfficerChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                System.out.println(OfficerChecks.testChecks(fromID, toID));
            }
        }
    }

    public static void main(String[] args) {
        int from = OfficerChecks.get67IDByBitboard(0x4000000L);
        int to = OfficerChecks.get67IDByBitboard(8L);
        System.out.println(OfficerChecks.testChecks(from, to));
    }

    static {
        OfficerChecks.genAll_Dynamic();
    }
}

