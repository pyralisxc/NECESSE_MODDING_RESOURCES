/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.plies.specials;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class Castling
extends Fields {
    public static final long MASK_WHITE_KING_SIDE = 0x600000000000000L;
    public static final long MASK_WHITE_QUEEN_SIDE = 0x7000000000000000L;
    public static final long MASK_BLACK_KING_SIDE = 6L;
    public static final long MASK_BLACK_QUEEN_SIDE = 112L;
    public static final int[] KINGS_PIDS_BY_COLOUR = new int[Figures.COLOUR_MAX];
    public static final long[] MASK_KING_CASTLE_SIDE_BY_COLOUR;
    public static final long[] MASK_QUEEN_CASTLE_SIDE_BY_COLOUR;
    public static final int[] KING_FROM_FIELD_ID_BY_COLOUR;
    public static final int[] KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR;
    public static final int[] CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR;
    public static final int[] CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR;
    public static final int[] KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR;
    public static final int[] CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR;
    public static final int[] CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR;
    public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR;
    public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR;
    public static final long[][] CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR;
    public static final long[][] CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR;

    public static final int getRookFromFieldID_queen(int colour) {
        return CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[colour];
    }

    public static final int getRookToFieldID_queen(int colour) {
        return CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[colour];
    }

    public static final int getRookFromFieldID_king(int colour) {
        return CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[colour];
    }

    public static final int getRookToFieldID_king(int colour) {
        return CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[colour];
    }

    static {
        Castling.KINGS_PIDS_BY_COLOUR[0] = 6;
        Castling.KINGS_PIDS_BY_COLOUR[1] = 12;
        MASK_KING_CASTLE_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
        Castling.MASK_KING_CASTLE_SIDE_BY_COLOUR[0] = 0x600000000000000L;
        Castling.MASK_KING_CASTLE_SIDE_BY_COLOUR[1] = 6L;
        MASK_QUEEN_CASTLE_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX];
        Castling.MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[0] = 0x7000000000000000L;
        Castling.MASK_QUEEN_CASTLE_SIDE_BY_COLOUR[1] = 112L;
        KING_FROM_FIELD_ID_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_FROM_FIELD_ID_BY_COLOUR[0] = Castling.get67IDByBitboard(0x800000000000000L);
        Castling.KING_FROM_FIELD_ID_BY_COLOUR[1] = Castling.get67IDByBitboard(8L);
        KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x200000000000000L);
        Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(2L);
        CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x100000000000000L);
        Castling.CASTLE_FROM_FIELD_ID_ON_KING_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(1L);
        CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x400000000000000L);
        Castling.CASTLE_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(4L);
        KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x2000000000000000L);
        Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(32L);
        CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(Long.MIN_VALUE);
        Castling.CASTLE_FROM_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(128L);
        CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x1000000000000000L);
        Castling.CASTLE_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(16L);
        CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][3];
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[0][0] = Castling.get67IDByBitboard(0x800000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[0][1] = Castling.get67IDByBitboard(0x400000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[0][2] = Castling.get67IDByBitboard(0x200000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[1][0] = Castling.get67IDByBitboard(8L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[1][1] = Castling.get67IDByBitboard(4L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[1][2] = Castling.get67IDByBitboard(2L);
        CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][3];
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[0][0] = Castling.get67IDByBitboard(0x2000000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[0][1] = Castling.get67IDByBitboard(0x1000000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[0][2] = Castling.get67IDByBitboard(0x800000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[1][0] = Castling.get67IDByBitboard(32L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[1][1] = Castling.get67IDByBitboard(16L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[1][2] = Castling.get67IDByBitboard(8L);
        CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX][3];
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[0][0] = 0x800000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[0][1] = 0x400000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[0][2] = 0x200000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[1][0] = 8L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[1][1] = 4L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_KING_SIDE_BY_COLOUR[1][2] = 2L;
        CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR = new long[Figures.COLOUR_MAX][3];
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[0][0] = 0x2000000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[0][1] = 0x1000000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[0][2] = 0x800000000000000L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[1][0] = 32L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[1][1] = 16L;
        Castling.CHECKING_CHECK_FIELD_BITBOARDS_ON_QUEEN_SIDE_BY_COLOUR[1][2] = 8L;
    }
}

