/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.plies.CastlePlies;
import bagaturchess.bitboard.impl.plies.OfficerPlies;

public class QueenUniqueChecks
extends Fields {
    public static int[][][] CHECK_MIDDLE_FIELDS_IDS = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_DIR_ID = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_DIR_TYPES = new int[64][64][];
    public static int[][][] CHECK_MIDDLE_FIELDS_SEQS = new int[64][64][];
    public static long[][][] CHECK_MIDDLE_FIELDS_BITBOARDS = new long[64][64][];
    public static long[][][] FIELDS_PATH1 = new long[64][64][];
    public static long[][][] FIELDS_PATH2 = new long[64][64][];
    public static long[][][] FIELDS_WHOLE_PATH = new long[64][64][];

    public static void initPair_CO_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
            long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
            long path1 = 0L;
            for (int seq1 = 0; seq1 < middleFieldIDsByDir.length; ++seq1) {
                int middleFieldID = middleFieldIDsByDir[seq1];
                long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
                int[] middleFieldValidDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[middleFieldID];
                int[][] toFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[middleFieldID];
                long[][] toFieldBitboards = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[middleFieldID];
                block2: for (int dirID2 : middleFieldValidDirIDs) {
                    int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
                    long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
                    long path2 = 0L;
                    for (int seq2 = 0; seq2 < cur_toFieldIDsByDir.length; ++seq2) {
                        int cur_toFieldID = cur_toFieldIDsByDir[seq2];
                        long endFieldBitboard = endFieldBitboardsByDir[seq2];
                        if ((path1 & endFieldBitboard) != 0L || cur_toFieldID == fromFieldID) continue block2;
                        if (toFieldID == cur_toFieldID) {
                            if (CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null || CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 3) {
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_DIR_TYPES[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_DIR_TYPES[fromFieldID][toFieldID], 4);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
                                QueenUniqueChecks.FIELDS_PATH1[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_PATH1[fromFieldID][toFieldID], path1);
                                QueenUniqueChecks.FIELDS_PATH2[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_PATH2[fromFieldID][toFieldID], path2);
                                QueenUniqueChecks.FIELDS_WHOLE_PATH[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_WHOLE_PATH[fromFieldID][toFieldID], path1 | path2);
                                continue block2;
                            }
                            throw new IllegalStateException("" + CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length);
                        }
                        path2 |= endFieldBitboard;
                    }
                }
                path1 |= middleFieldBitboard;
            }
        }
    }

    public static void initPair_OC_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = OfficerPlies.ALL_OFFICER_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = OfficerPlies.ALL_OFFICER_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = OfficerPlies.ALL_OFFICER_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
            long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
            long path1 = 0L;
            for (int seq1 = 0; seq1 < middleFieldIDsByDir.length; ++seq1) {
                int middleFieldID = middleFieldIDsByDir[seq1];
                long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
                int[] middleFieldValidDirIDs = CastlePlies.ALL_CASTLE_VALID_DIRS[middleFieldID];
                int[][] toFieldIDs = CastlePlies.ALL_CASTLE_DIRS_WITH_FIELD_IDS[middleFieldID];
                long[][] toFieldBitboards = CastlePlies.ALL_CASTLE_DIRS_WITH_BITBOARDS[middleFieldID];
                block2: for (int dirID2 : middleFieldValidDirIDs) {
                    int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
                    long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
                    long path2 = 0L;
                    for (int seq2 = 0; seq2 < cur_toFieldIDsByDir.length; ++seq2) {
                        int cur_toFieldID = cur_toFieldIDsByDir[seq2];
                        long endFieldBitboard = endFieldBitboardsByDir[seq2];
                        if ((path1 & endFieldBitboard) != 0L || cur_toFieldID == fromFieldID) continue block2;
                        if (toFieldID == cur_toFieldID) {
                            if (CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null || CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 7) {
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_DIR_TYPES[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_DIR_TYPES[fromFieldID][toFieldID], 3);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
                                QueenUniqueChecks.CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(CHECK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
                                QueenUniqueChecks.FIELDS_PATH1[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_PATH1[fromFieldID][toFieldID], path1);
                                QueenUniqueChecks.FIELDS_PATH2[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_PATH2[fromFieldID][toFieldID], path2);
                                QueenUniqueChecks.FIELDS_WHOLE_PATH[fromFieldID][toFieldID] = QueenUniqueChecks.extendArray(FIELDS_WHOLE_PATH[fromFieldID][toFieldID], path1 | path2);
                                continue block2;
                            }
                            throw new IllegalStateException("" + CHECK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length);
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
            int fromID = QueenUniqueChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = QueenUniqueChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID) continue;
                QueenUniqueChecks.initPair_CO_Dynamic(fromID, toID);
                QueenUniqueChecks.initPair_OC_Dynamic(fromID, toID);
            }
        }
    }

    public static String testChecks(int fromFieldID, int toFieldID) {
        int i;
        String result = "Queen checks from " + QueenUniqueChecks.getFieldSign_UC(fromFieldID) + " to " + QueenUniqueChecks.getFieldSign_UC(toFieldID) + " -> ";
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
            if (fields.length > 8) {
                throw new IllegalStateException("" + fields.length);
            }
            for (i = 0; i < fields.length; ++i) {
                result = result + QueenUniqueChecks.getFieldSign_UC(fields[i]) + ", ";
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
            int fromID = QueenUniqueChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = QueenUniqueChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                System.out.println(QueenUniqueChecks.testChecks(fromID, toID));
            }
        }
    }

    public static void main(String[] args) {
        int from = QueenUniqueChecks.get67IDByBitboard(2L);
        int to = QueenUniqueChecks.get67IDByBitboard(16384L);
        System.out.println(QueenUniqueChecks.testChecks(from, to));
    }

    static {
        QueenUniqueChecks.genAll_Dynamic();
    }
}

