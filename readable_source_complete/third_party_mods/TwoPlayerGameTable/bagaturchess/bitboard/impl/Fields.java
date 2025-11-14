/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl;

import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Bits;
import java.util.HashMap;
import java.util.Map;

public class Fields
extends Bits {
    public static final int ID_MAX = 64;
    public static final int DUMMY_FIELD_ID = 69;
    public static final long A1 = Long.MIN_VALUE;
    public static final long B1 = 0x4000000000000000L;
    public static final long C1 = 0x2000000000000000L;
    public static final long D1 = 0x1000000000000000L;
    public static final long E1 = 0x800000000000000L;
    public static final long F1 = 0x400000000000000L;
    public static final long G1 = 0x200000000000000L;
    public static final long H1 = 0x100000000000000L;
    public static final long A2 = 0x80000000000000L;
    public static final long B2 = 0x40000000000000L;
    public static final long C2 = 0x20000000000000L;
    public static final long D2 = 0x10000000000000L;
    public static final long E2 = 0x8000000000000L;
    public static final long F2 = 0x4000000000000L;
    public static final long G2 = 0x2000000000000L;
    public static final long H2 = 0x1000000000000L;
    public static final long A3 = 0x800000000000L;
    public static final long B3 = 0x400000000000L;
    public static final long C3 = 0x200000000000L;
    public static final long D3 = 0x100000000000L;
    public static final long E3 = 0x80000000000L;
    public static final long F3 = 0x40000000000L;
    public static final long G3 = 0x20000000000L;
    public static final long H3 = 0x10000000000L;
    public static final long A4 = 0x8000000000L;
    public static final long B4 = 0x4000000000L;
    public static final long C4 = 0x2000000000L;
    public static final long D4 = 0x1000000000L;
    public static final long E4 = 0x800000000L;
    public static final long F4 = 0x400000000L;
    public static final long G4 = 0x200000000L;
    public static final long H4 = 0x100000000L;
    public static final long A5 = 0x80000000L;
    public static final long B5 = 0x40000000L;
    public static final long C5 = 0x20000000L;
    public static final long D5 = 0x10000000L;
    public static final long E5 = 0x8000000L;
    public static final long F5 = 0x4000000L;
    public static final long G5 = 0x2000000L;
    public static final long H5 = 0x1000000L;
    public static final long A6 = 0x800000L;
    public static final long B6 = 0x400000L;
    public static final long C6 = 0x200000L;
    public static final long D6 = 0x100000L;
    public static final long E6 = 524288L;
    public static final long F6 = 262144L;
    public static final long G6 = 131072L;
    public static final long H6 = 65536L;
    public static final long A7 = 32768L;
    public static final long B7 = 16384L;
    public static final long C7 = 8192L;
    public static final long D7 = 4096L;
    public static final long E7 = 2048L;
    public static final long F7 = 1024L;
    public static final long G7 = 512L;
    public static final long H7 = 256L;
    public static final long A8 = 128L;
    public static final long B8 = 64L;
    public static final long C8 = 32L;
    public static final long D8 = 16L;
    public static final long E8 = 8L;
    public static final long F8 = 4L;
    public static final long G8 = 2L;
    public static final long H8 = 1L;
    public static final long WHITE_PROMOTIONS = 255L;
    public static final long BLACK_PROMOTIONS = -72057594037927936L;
    public static final int A1_ID = 0;
    public static final int B1_ID = 1;
    public static final int C1_ID = 2;
    public static final int D1_ID = 3;
    public static final int E1_ID = 4;
    public static final int F1_ID = 5;
    public static final int G1_ID = 6;
    public static final int H1_ID = 7;
    public static final int A2_ID = 8;
    public static final int B2_ID = 9;
    public static final int C2_ID = 10;
    public static final int D2_ID = 11;
    public static final int E2_ID = 12;
    public static final int F2_ID = 13;
    public static final int G2_ID = 14;
    public static final int H2_ID = 15;
    public static final int A3_ID = 16;
    public static final int B3_ID = 17;
    public static final int C3_ID = 18;
    public static final int D3_ID = 19;
    public static final int E3_ID = 20;
    public static final int F3_ID = 21;
    public static final int G3_ID = 22;
    public static final int H3_ID = 23;
    public static final int A4_ID = 24;
    public static final int B4_ID = 25;
    public static final int C4_ID = 26;
    public static final int D4_ID = 27;
    public static final int E4_ID = 28;
    public static final int F4_ID = 29;
    public static final int G4_ID = 30;
    public static final int H4_ID = 31;
    public static final int A5_ID = 32;
    public static final int B5_ID = 33;
    public static final int C5_ID = 34;
    public static final int D5_ID = 35;
    public static final int E5_ID = 36;
    public static final int F5_ID = 37;
    public static final int G5_ID = 38;
    public static final int H5_ID = 39;
    public static final int A6_ID = 40;
    public static final int B6_ID = 41;
    public static final int C6_ID = 42;
    public static final int D6_ID = 43;
    public static final int E6_ID = 44;
    public static final int F6_ID = 45;
    public static final int G6_ID = 46;
    public static final int H6_ID = 47;
    public static final int A7_ID = 48;
    public static final int B7_ID = 49;
    public static final int C7_ID = 50;
    public static final int D7_ID = 51;
    public static final int E7_ID = 52;
    public static final int F7_ID = 53;
    public static final int G7_ID = 54;
    public static final int H7_ID = 55;
    public static final int A8_ID = 56;
    public static final int B8_ID = 57;
    public static final int C8_ID = 58;
    public static final int D8_ID = 59;
    public static final int E8_ID = 60;
    public static final int F8_ID = 61;
    public static final int G8_ID = 62;
    public static final int H8_ID = 63;
    public static final long[] ALL_ORDERED_A1H1;
    public static final int[] IDX_2_ORDERED_A1H1;
    public static final String[] ALL_ORDERED_NAMES;
    public static final int[] IDX_ORDERED_2_A1H1;
    public static final long[] ALL_A1H1;
    public static final long[] ALL_A1A8;
    public static final int[] LETTERS;
    public static final int[] DIGITS;
    public static final int LETTER_A_ID = 0;
    public static final int LETTER_B_ID = 1;
    public static final int LETTER_C_ID = 2;
    public static final int LETTER_D_ID = 3;
    public static final int LETTER_E_ID = 4;
    public static final int LETTER_F_ID = 5;
    public static final int LETTER_G_ID = 6;
    public static final int LETTER_H_ID = 7;
    public static final int LETTER_NONE_ID = 8;
    public static final int DIGIT_1_ID = 0;
    public static final int DIGIT_2_ID = 1;
    public static final int DIGIT_3_ID = 2;
    public static final int DIGIT_4_ID = 3;
    public static final int DIGIT_5_ID = 4;
    public static final int DIGIT_6_ID = 5;
    public static final int DIGIT_7_ID = 6;
    public static final int DIGIT_8_ID = 7;
    public static final int DIGIT_NONE_ID = 8;
    public static final long LETTER_A = -9187201950435737472L;
    public static final long LETTER_B = 0x4040404040404040L;
    public static final long LETTER_C = 0x2020202020202020L;
    public static final long LETTER_D = 0x1010101010101010L;
    public static final long LETTER_E = 0x808080808080808L;
    public static final long LETTER_F = 0x404040404040404L;
    public static final long LETTER_G = 0x202020202020202L;
    public static final long LETTER_H = 0x101010101010101L;
    public static final long DIGIT_1 = -72057594037927936L;
    public static final long DIGIT_2 = 0xFF000000000000L;
    public static final long DIGIT_3 = 0xFF0000000000L;
    public static final long DIGIT_4 = 0xFF00000000L;
    public static final long DIGIT_5 = 0xFF000000L;
    public static final long DIGIT_6 = 0xFF0000L;
    public static final long DIGIT_7 = 65280L;
    public static final long DIGIT_8 = 255L;
    public static final long INITIAL_BOARD = -281474976645121L;
    public static final long ALL_FIELDS = -1L;
    public static final long CORNERS = -17802464409370369L;
    public static final long CENTER_1 = 0x1818000000L;
    public static final long CENTER_2 = 0x3C3C3C3C0000L;
    public static final long ED2 = 0x18000000000000L;
    public static final long ED7 = 6144L;
    public static final long CENTER_3 = 0x3F030303033F00L;
    public static final int[] CENTRALIZATION;
    public static final int[] FILE_SYMMETRY;
    public static final int[] HORIZONTAL_SYMMETRY;
    public static final int[] VERTICAL_SYMMETRY;
    public static int[] CENTER_MANHATTAN_DISTANCE;
    public static final long ALL_WHITE_FIELDS = 0x55AA55AA55AA55AAL;
    public static final long ALL_BLACK_FIELDS = -6172840429334713771L;
    public static final long SPACE_WHITE = 0x3C3C3C00000000L;
    public static final long SPACE_BLACK = 0x3C3C3C00L;
    public static final long[] ALL_OFFICERS_FIELDS;
    public static final long[] LETTERS_BY_FIELD_ID;
    public static final long[] LETTERS_NEIGHBOURS_BY_FIELD_ID;
    public static final long[] LETTERS_LEFT_BY_FIELD_ID;
    public static final long[] LETTERS_RIGHT_BY_FIELD_ID;
    private static Map<String, Integer> fieldSignToFieldID;
    private static Map<Integer, String> fieldIDToFieldSign;
    private static final long BINARY_SEARCH_1_1 = -4294967296L;
    private static final long BINARY_SEARCH_1_2 = 0xFFFFFFFFL;
    private static final long BINARY_SEARCH_1_1_1 = -281474976710656L;
    private static final long BINARY_SEARCH_1_1_2 = 0xFFFF00000000L;
    private static final long BINARY_SEARCH_1_1_1_1 = -72057594037927936L;
    private static final long BINARY_SEARCH_1_1_1_2 = 0xFF000000000000L;
    private static final long BINARY_SEARCH_1_1_2_1 = 0xFF0000000000L;
    private static final long BINARY_SEARCH_1_1_2_2 = 0xFF00000000L;
    private static final long BINARY_SEARCH_1_1_1_1_1 = -1152921504606846976L;
    private static final long BINARY_SEARCH_1_1_1_1_2 = 0xF00000000000000L;
    private static final long BINARY_SEARCH_1_1_1_2_1 = 0xF0000000000000L;
    private static final long BINARY_SEARCH_1_1_1_2_2 = 0xF000000000000L;
    private static final long BINARY_SEARCH_1_1_2_1_1 = 0xF00000000000L;
    private static final long BINARY_SEARCH_1_1_2_1_2 = 0xF0000000000L;
    private static final long BINARY_SEARCH_1_1_2_2_1 = 0xF000000000L;
    private static final long BINARY_SEARCH_1_1_2_2_2 = 0xF00000000L;
    private static final long BINARY_SEARCH_1_2_1 = 0xFFFF0000L;
    private static final long BINARY_SEARCH_1_2_2 = 65535L;
    private static final long BINARY_SEARCH_1_2_1_1 = 0xFF000000L;
    private static final long BINARY_SEARCH_1_2_1_2 = 0xFF0000L;
    private static final long BINARY_SEARCH_1_2_2_1 = 65280L;
    private static final long BINARY_SEARCH_1_2_2_2 = 255L;
    private static final long BINARY_SEARCH_1_2_1_1_1 = 0xF0000000L;
    private static final long BINARY_SEARCH_1_2_1_1_2 = 0xF000000L;
    private static final long BINARY_SEARCH_1_2_1_2_1 = 0xF00000L;
    private static final long BINARY_SEARCH_1_2_1_2_2 = 983040L;
    private static final long BINARY_SEARCH_1_2_2_1_1 = 61440L;
    private static final long BINARY_SEARCH_1_2_2_1_2 = 3840L;
    private static final long BINARY_SEARCH_1_2_2_2_1 = 240L;
    private static final long BINARY_SEARCH_1_2_2_2_2 = 15L;

    public static int getRank_W(int fieldID) {
        return DIGITS[fieldID];
    }

    public static int getRank_B(int fieldID) {
        return 7 - DIGITS[fieldID];
    }

    public static int getFieldIDByFileAndRank(int digit, int letter) {
        return digit * 8 + letter;
    }

    public static final int get67IDByBitboard(long bitBoard) {
        return Long.numberOfLeadingZeros(bitBoard);
    }

    public int getDigitsDiff(int fieldID1, int fieldID2) {
        int d1 = DIGITS[fieldID1];
        int d2 = DIGITS[fieldID2];
        return Math.abs(d1 - d2);
    }

    public int getDistance(int fieldID1, int fieldID2) {
        int l1 = LETTERS[fieldID1];
        int l2 = LETTERS[fieldID2];
        int d1 = DIGITS[fieldID1];
        int d2 = DIGITS[fieldID2];
        int dl = Math.abs(l1 - l2);
        int dd = Math.abs(d1 - d2);
        return Math.max(dl, dd);
    }

    private static final int get67IDByBitboard(long bitBoard, int from, int to) {
        int id = -1;
        for (int i = from; i < to; ++i) {
            if (ALL_BITS[i] != bitBoard) continue;
            id = i;
            break;
        }
        return id;
    }

    public static final int[][] rotateBoard(int[][] _matrix) {
        int[][] matrix = new int[8][8];
        for (int i = 0; i < 8; ++i) {
            for (int j = 0; j < 8; ++j) {
                matrix[j][7 - i] = _matrix[i][j];
            }
        }
        return matrix;
    }

    public static int getFieldID(String fieldSign) {
        return fieldSignToFieldID.get(fieldSign);
    }

    public static String getFieldSign(int fieldID) {
        return fieldIDToFieldSign.get(fieldID);
    }

    public static String getFieldSign_UC(int fieldID) {
        return Fields.getFieldSign(fieldID).toUpperCase();
    }

    public static int getDistancePoints(int fieldID1, int fieldID2) {
        int l1 = LETTERS[fieldID1];
        int d1 = DIGITS[fieldID1];
        int l2 = LETTERS[fieldID2];
        int d2 = DIGITS[fieldID2];
        int delta_l = Math.abs(l1 - l2);
        int delta_d = Math.abs(d1 - d2);
        int max_delta = Math.max(delta_l, delta_d);
        return max_delta;
    }

    public static boolean areOnTheSameLine(int fieldID1, int fieldID2) {
        int l1 = LETTERS[fieldID1];
        int d1 = DIGITS[fieldID1];
        int l2 = LETTERS[fieldID2];
        int d2 = DIGITS[fieldID2];
        return l1 == l2 || d1 == d2;
    }

    public static int getDistancePoints_reversed(int fieldID1, int fieldID2) {
        return 7 - Fields.getDistancePoints(fieldID1, fieldID2);
    }

    public static int getTropismPoint(int fieldID1, int fieldID2) {
        int delta_d;
        int l1 = LETTERS[fieldID1];
        int d1 = DIGITS[fieldID1];
        int l2 = LETTERS[fieldID2];
        int d2 = DIGITS[fieldID2];
        int delta_l = Math.abs(l1 - l2);
        int sum = delta_l + (delta_d = Math.abs(d1 - d2));
        if (14 - sum < 0) {
            throw new IllegalStateException("sum=" + sum);
        }
        return 14 - sum;
    }

    public static int getCenteredPoint(int fieldID) {
        return CENTRALIZATION[fieldID];
    }

    protected static final int[][] bitboards2fieldIDs(long[][] dirsBitboards) {
        int[][] result = new int[dirsBitboards.length][];
        for (int i = 0; i < dirsBitboards.length; ++i) {
            long[] dirBitboards = dirsBitboards[i];
            result[i] = new int[dirBitboards.length];
            for (int j = 0; j < dirBitboards.length; ++j) {
                long bitboard = dirBitboards[j];
                result[i][j] = Fields.get67IDByBitboard(bitboard);
                if (result[i][j] >= 0 && result[i][j] <= 63) continue;
                throw new IllegalStateException();
            }
        }
        return result;
    }

    private static void genMembers_A1H1() {
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digit = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "";
        for (i = 0; i < 64; ++i) {
            result = "public static final long " + letters[i % 8] + digit[i / 8] + " = BIT_" + i + ";";
            System.out.println((String)result);
        }
        result = "public static final long ALL_A1H1[] = new long[] {";
        for (i = 0; i < 64; ++i) {
            result = (String)result + letters[i % 8] + digit[i / 8] + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_A1A8() {
        Object result;
        int i;
        String[] letters = new String[]{"A", "B", "C", "D", "E", "F", "G", "H"};
        String[] digit = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        for (i = 0; i < 64; ++i) {
            result = "public static final long A1A8_" + letters[i / 8] + digit[i % 8] + " = BIT_" + i + ";";
        }
        result = "public static final long ALL_A1A8[] = new long[] {";
        for (i = 0; i < 64; ++i) {
            result = (String)result + "A1A8_" + letters[i / 8] + digit[i % 8] + ", ";
            if (i != 63) continue;
            result = (String)result + "};";
        }
        System.out.println((String)result);
    }

    private static void genMembers_FieldNames() {
        int mod;
        int div;
        int i;
        String[] letters = new String[]{"a", "b", "c", "d", "e", "f", "g", "h"};
        String[] digit = new String[]{"1", "2", "3", "4", "5", "6", "7", "8"};
        Object result = "public static final String ALL_ORDERED_NAMES[] = new String[] {";
        for (i = 0; i < 64; ++i) {
            div = i / 8;
            mod = i % 8;
            result = (String)result + "\"" + letters[mod] + digit[div] + "\", ";
        }
        result = (String)result + "};";
        System.out.println((String)result);
        result = "public static final String ALL_ORDERED_A1A8_NAMES[] = new String[] {";
        for (i = 0; i < 64; ++i) {
            div = i / 8;
            mod = i % 8;
            result = (String)result + "\"" + letters[div] + digit[mod] + "\", ";
        }
        result = (String)result + "};";
        System.out.println((String)result);
    }

    private static final void verify() {
    }

    public static void main(String[] args) {
        Fields.genMembers_FieldNames();
    }

    static {
        int id;
        int i;
        ALL_ORDERED_A1H1 = new long[]{Long.MIN_VALUE, 0x4000000000000000L, 0x2000000000000000L, 0x1000000000000000L, 0x800000000000000L, 0x400000000000000L, 0x200000000000000L, 0x100000000000000L, 0x80000000000000L, 0x40000000000000L, 0x20000000000000L, 0x10000000000000L, 0x8000000000000L, 0x4000000000000L, 0x2000000000000L, 0x1000000000000L, 0x800000000000L, 0x400000000000L, 0x200000000000L, 0x100000000000L, 0x80000000000L, 0x40000000000L, 0x20000000000L, 0x10000000000L, 0x8000000000L, 0x4000000000L, 0x2000000000L, 0x1000000000L, 0x800000000L, 0x400000000L, 0x200000000L, 0x100000000L, 0x80000000L, 0x40000000L, 0x20000000L, 0x10000000L, 0x8000000L, 0x4000000L, 0x2000000L, 0x1000000L, 0x800000L, 0x400000L, 0x200000L, 0x100000L, 524288L, 262144L, 131072L, 65536L, 32768L, 16384L, 8192L, 4096L, 2048L, 1024L, 512L, 256L, 128L, 64L, 32L, 16L, 8L, 4L, 2L, 1L};
        IDX_2_ORDERED_A1H1 = new int[64];
        ALL_ORDERED_NAMES = new String[]{"a1", "b1", "c1", "d1", "e1", "f1", "g1", "h1", "a2", "b2", "c2", "d2", "e2", "f2", "g2", "h2", "a3", "b3", "c3", "d3", "e3", "f3", "g3", "h3", "a4", "b4", "c4", "d4", "e4", "f4", "g4", "h4", "a5", "b5", "c5", "d5", "e5", "f5", "g5", "h5", "a6", "b6", "c6", "d6", "e6", "f6", "g6", "h6", "a7", "b7", "c7", "d7", "e7", "f7", "g7", "h7", "a8", "b8", "c8", "d8", "e8", "f8", "g8", "h8"};
        IDX_ORDERED_2_A1H1 = new int[64];
        ALL_A1H1 = new long[64];
        ALL_A1A8 = new long[64];
        LETTERS = new int[64];
        DIGITS = new int[64];
        CENTRALIZATION = Utils.reverseSpecial(new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 1, 2, 2, 2, 2, 1, 0, 0, 1, 2, 4, 4, 2, 1, 0, 0, 1, 2, 4, 4, 2, 1, 0, 0, 1, 2, 2, 2, 2, 1, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0});
        FILE_SYMMETRY = new int[]{0, 1, 2, 3, 3, 2, 1, 0};
        HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
        VERTICAL_SYMMETRY = Utils.reverseSpecial(new int[]{63, 62, 61, 60, 59, 58, 57, 56, 55, 54, 53, 52, 51, 50, 49, 48, 47, 46, 45, 44, 43, 42, 41, 40, 39, 38, 37, 36, 35, 34, 33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1, 0});
        CENTER_MANHATTAN_DISTANCE = Utils.reverseSpecial(new int[]{6, 5, 4, 3, 3, 4, 5, 6, 5, 4, 3, 2, 2, 3, 4, 5, 4, 3, 2, 1, 1, 2, 3, 4, 3, 2, 1, 0, 0, 1, 2, 3, 3, 2, 1, 0, 0, 1, 2, 3, 4, 3, 2, 1, 1, 2, 3, 4, 5, 4, 3, 2, 2, 3, 4, 5, 6, 5, 4, 3, 3, 4, 5, 6});
        ALL_OFFICERS_FIELDS = new long[64];
        LETTERS_BY_FIELD_ID = new long[64];
        LETTERS_NEIGHBOURS_BY_FIELD_ID = new long[64];
        LETTERS_LEFT_BY_FIELD_ID = new long[64];
        LETTERS_RIGHT_BY_FIELD_ID = new long[64];
        fieldSignToFieldID = new HashMap<String, Integer>();
        fieldIDToFieldSign = new HashMap<Integer, String>();
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            long a1h1 = ALL_ORDERED_A1H1[i];
            id = Fields.get67IDByBitboard(a1h1);
            Fields.ALL_A1H1[id] = a1h1;
        }
        for (i = 0; i < ALL_ORDERED_NAMES.length; ++i) {
            String a1h1 = ALL_ORDERED_NAMES[i];
            int id2 = Fields.get67IDByBitboard(ALL_ORDERED_A1H1[i]);
            fieldSignToFieldID.put(a1h1, id2);
            fieldIDToFieldSign.put(id2, a1h1);
        }
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            long a1h1 = ALL_ORDERED_A1H1[i];
            id = Fields.get67IDByBitboard(a1h1);
            if ((a1h1 & 0x55AA55AA55AA55AAL) != 0L) {
                Fields.ALL_OFFICERS_FIELDS[id] = 0x55AA55AA55AA55AAL;
                continue;
            }
            if ((a1h1 & 0xAA55AA55AA55AA55L) != 0L) {
                Fields.ALL_OFFICERS_FIELDS[id] = -6172840429334713771L;
                continue;
            }
            throw new IllegalStateException();
        }
        for (i = 0; i < ALL_ORDERED_A1H1.length; ++i) {
            long a1h1 = ALL_ORDERED_A1H1[i];
            id = Fields.get67IDByBitboard(a1h1);
            if ((0x8080808080808080L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = -9187201950435737472L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x4040404040404040L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x4040404040404040L;
                continue;
            }
            if ((0x4040404040404040L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x4040404040404040L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = -6872316419617283936L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = -9187201950435737472L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x2020202020202020L;
                continue;
            }
            if ((0x2020202020202020L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x2020202020202020L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x5050505050505050L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x4040404040404040L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x1010101010101010L;
                continue;
            }
            if ((0x1010101010101010L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x1010101010101010L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x2828282828282828L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x2020202020202020L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x808080808080808L;
                continue;
            }
            if ((0x808080808080808L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x808080808080808L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x1414141414141414L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x1010101010101010L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x404040404040404L;
                continue;
            }
            if ((0x404040404040404L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x404040404040404L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0xA0A0A0A0A0A0A0AL;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x808080808080808L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x202020202020202L;
                continue;
            }
            if ((0x202020202020202L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x202020202020202L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x505050505050505L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x404040404040404L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0x101010101010101L;
                continue;
            }
            if ((0x101010101010101L & a1h1) != 0L) {
                Fields.LETTERS_BY_FIELD_ID[id] = 0x101010101010101L;
                Fields.LETTERS_NEIGHBOURS_BY_FIELD_ID[id] = 0x202020202020202L;
                Fields.LETTERS_LEFT_BY_FIELD_ID[id] = 0x202020202020202L;
                Fields.LETTERS_RIGHT_BY_FIELD_ID[id] = 0L;
                continue;
            }
            throw new IllegalStateException();
        }
        block4: for (int id3 = 0; id3 < ALL_A1H1.length; ++id3) {
            long a1h1 = ALL_A1H1[id3];
            for (int j = 0; j < ALL_ORDERED_A1H1.length; ++j) {
                long tmp = ALL_ORDERED_A1H1[j];
                if (tmp == a1h1) {
                    Fields.IDX_ORDERED_2_A1H1[j] = id3;
                    Fields.IDX_2_ORDERED_A1H1[id3] = j;
                    continue block4;
                }
                if (j != ALL_ORDERED_A1H1.length - 1) continue;
                throw new IllegalStateException();
            }
        }
        for (int digit = 0; digit < 8; ++digit) {
            int letter = 0;
            while (letter < 8) {
                int ordered_id = Fields.getFieldIDByFileAndRank(digit, letter);
                Fields.LETTERS[Fields.IDX_ORDERED_2_A1H1[ordered_id]] = letter++;
                Fields.DIGITS[Fields.IDX_ORDERED_2_A1H1[ordered_id]] = digit;
            }
        }
        Fields.verify();
    }
}

