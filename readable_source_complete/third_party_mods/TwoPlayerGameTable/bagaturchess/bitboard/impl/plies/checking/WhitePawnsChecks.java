/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.checking;

import bagaturchess.bitboard.impl.Bits;
import bagaturchess.bitboard.impl.plies.WhitePawnPlies;

public class WhitePawnsChecks
extends WhitePawnPlies {
    public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_IDS = new int[64][64][];
    public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID = new int[64][64][];
    public static int[][][] CHECK_ATTACK_MIDDLE_FIELDS_SEQS = new int[64][64][];
    public static long[][][] CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS = new long[64][64][];
    public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_IDS = new int[64][64][];
    public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID = new int[64][64][];
    public static int[][][] CHECK_NONATTACK_MIDDLE_FIELDS_SEQS = new int[64][64][];
    public static long[][][] CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS = new long[64][64][];
    public static long[] FIELDS_ATTACK_2_ALL = new long[64];
    public static long[] FIELDS_ATTACK_2_CAPTURES = new long[64];
    public static long[] FIELDS_ATTACK_2_NONCAPTURES = new long[64];
    public static long[] FIELDS_ATTACKERS_2 = new long[64];

    public static void initPair_Attacks_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
            long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
            long path1 = 0L;
            for (int seq1 = 0; seq1 < middleFieldIDsByDir.length; ++seq1) {
                int middleFieldID = middleFieldIDsByDir[seq1];
                long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
                int[] middleFieldValidDirIDs = ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[middleFieldID];
                int[][] toFieldIDs = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[middleFieldID];
                long[][] toFieldBitboards = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[middleFieldID];
                block2: for (int dirID2 : middleFieldValidDirIDs) {
                    int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
                    long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
                    long path2 = 0L;
                    for (int seq2 = 0; seq2 < cur_toFieldIDsByDir.length; ++seq2) {
                        int cur_toFieldID = cur_toFieldIDsByDir[seq2];
                        long endFieldBitboard = endFieldBitboardsByDir[seq2];
                        int n = fromFieldID;
                        FIELDS_ATTACK_2_ALL[n] = FIELDS_ATTACK_2_ALL[n] | endFieldBitboard;
                        int n2 = fromFieldID;
                        FIELDS_ATTACK_2_CAPTURES[n2] = FIELDS_ATTACK_2_CAPTURES[n2] | endFieldBitboard;
                        if (toFieldID == cur_toFieldID) {
                            int n3 = toFieldID;
                            FIELDS_ATTACKERS_2[n3] = FIELDS_ATTACKERS_2[n3] | ALL_A1H1[fromFieldID];
                            if (CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null || CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 1) {
                                WhitePawnsChecks.CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
                                WhitePawnsChecks.CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_ATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
                                WhitePawnsChecks.CHECK_ATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_ATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
                                WhitePawnsChecks.CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
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

    public static void genAll_Attacks_Dynamic() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID) continue;
                WhitePawnsChecks.initPair_Attacks_Dynamic(fromID, toID);
            }
        }
    }

    public static void initPair_NonAttacks_Dynamic(int fromFieldID, int toFieldID) {
        int[] fromFieldValidDirIDs = ALL_WHITE_PAWN_NONATTACKS_VALID_DIRS[fromFieldID];
        int[][] middleFieldIDs = ALL_WHITE_PAWN_NONATTACKS_DIRS_WITH_FIELD_IDS[fromFieldID];
        long[][] middleFieldBitboards = ALL_WHITE_PAWN_NONATTACKS_DIRS_WITH_BITBOARDS[fromFieldID];
        for (int dirID1 : fromFieldValidDirIDs) {
            int[] middleFieldIDsByDir = middleFieldIDs[dirID1];
            long[] middleFieldBitboardsByDir = middleFieldBitboards[dirID1];
            long path1 = 0L;
            for (int seq1 = 0; seq1 < middleFieldIDsByDir.length; ++seq1) {
                int middleFieldID = middleFieldIDsByDir[seq1];
                long middleFieldBitboard = middleFieldBitboardsByDir[seq1];
                int[] middleFieldValidDirIDs = ALL_WHITE_PAWN_ATTACKS_VALID_DIRS[middleFieldID];
                int[][] toFieldIDs = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_FIELD_IDS[middleFieldID];
                long[][] toFieldBitboards = ALL_WHITE_PAWN_ATTACKS_DIRS_WITH_BITBOARDS[middleFieldID];
                block2: for (int dirID2 : middleFieldValidDirIDs) {
                    int[] cur_toFieldIDsByDir = toFieldIDs[dirID2];
                    long[] endFieldBitboardsByDir = toFieldBitboards[dirID2];
                    long path2 = 0L;
                    for (int seq2 = 0; seq2 < cur_toFieldIDsByDir.length; ++seq2) {
                        int cur_toFieldID = cur_toFieldIDsByDir[seq2];
                        long endFieldBitboard = endFieldBitboardsByDir[seq2];
                        int n = fromFieldID;
                        FIELDS_ATTACK_2_ALL[n] = FIELDS_ATTACK_2_ALL[n] | endFieldBitboard;
                        int n2 = fromFieldID;
                        FIELDS_ATTACK_2_NONCAPTURES[n2] = FIELDS_ATTACK_2_NONCAPTURES[n2] | endFieldBitboard;
                        if (toFieldID == cur_toFieldID) {
                            int n3 = toFieldID;
                            FIELDS_ATTACKERS_2[n3] = FIELDS_ATTACKERS_2[n3] | ALL_A1H1[fromFieldID];
                            if (CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] == null || CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID].length <= 1) {
                                WhitePawnsChecks.CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID], middleFieldID);
                                WhitePawnsChecks.CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_DIR_ID[fromFieldID][toFieldID], dirID1);
                                WhitePawnsChecks.CHECK_NONATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_SEQS[fromFieldID][toFieldID], seq1);
                                WhitePawnsChecks.CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID] = WhitePawnsChecks.extendArray(CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID], middleFieldBitboard);
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

    public static void genAll_NonAttacks_Dynamic() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                if (fromID == toID) continue;
                WhitePawnsChecks.initPair_NonAttacks_Dynamic(fromID, toID);
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

    public static String testAttackChecks(int fromFieldID, int toFieldID) {
        int i;
        String result = "White pawn atack checks from " + WhitePawnsChecks.getFieldSign_UC(fromFieldID) + " to " + WhitePawnsChecks.getFieldSign_UC(toFieldID) + " -> ";
        int[] fields = CHECK_ATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
        long[] fieldBoards = CHECK_ATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
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
            if (fields.length > 2) {
                throw new IllegalStateException();
            }
            for (i = 0; i < fields.length; ++i) {
                result = result + WhitePawnsChecks.getFieldSign_UC(fields[i]) + ", ";
            }
            if (fields != null) {
                result = result + "\r\n";
                for (i = 0; i < fields.length; ++i) {
                    result = result + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2_CAPTURES[fromFieldID]) + "\r\n";
                }
            }
            System.out.println(result);
        }
        return result;
    }

    public static String testNonAttackChecks(int fromFieldID, int toFieldID) {
        int i;
        String result = "White pawn nonattack checks from " + WhitePawnsChecks.getFieldSign_UC(fromFieldID) + " to " + WhitePawnsChecks.getFieldSign_UC(toFieldID) + " -> ";
        int[] fields = CHECK_NONATTACK_MIDDLE_FIELDS_IDS[fromFieldID][toFieldID];
        long[] fieldBoards = CHECK_NONATTACK_MIDDLE_FIELDS_BITBOARDS[fromFieldID][toFieldID];
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
            if (fields.length > 1) {
                throw new IllegalStateException();
            }
            for (i = 0; i < fields.length; ++i) {
                result = result + WhitePawnsChecks.getFieldSign_UC(fields[i]) + ", ";
            }
            if (fields != null) {
                result = result + "\r\n";
                for (i = 0; i < fields.length; ++i) {
                    result = result + Bits.toBinaryStringMatrix(FIELDS_ATTACK_2_NONCAPTURES[fromFieldID]) + "\r\n";
                }
            }
            System.out.println(result);
        }
        return result;
    }

    public static void testAll() {
        for (int i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            int fromID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                int toID = WhitePawnsChecks.get67IDByBitboard(ALL_ORDERED_A1H1[j]);
                WhitePawnsChecks.testAttackChecks(fromID, toID);
                WhitePawnsChecks.testNonAttackChecks(fromID, toID);
            }
        }
    }

    public static void main(String[] args) {
        int from = WhitePawnsChecks.get67IDByBitboard(0x8000000000000L);
        int to = WhitePawnsChecks.get67IDByBitboard(0x8000000L);
        WhitePawnsChecks.testAll();
    }

    static {
        WhitePawnsChecks.genAll_Attacks_Dynamic();
        WhitePawnsChecks.genAll_NonAttacks_Dynamic();
    }
}

