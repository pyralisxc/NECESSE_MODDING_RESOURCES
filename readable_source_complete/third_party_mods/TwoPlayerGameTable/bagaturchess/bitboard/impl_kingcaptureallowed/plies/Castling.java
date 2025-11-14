/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl_kingcaptureallowed.plies;

import bagaturchess.bitboard.impl.Fields;
import bagaturchess.bitboard.impl.Figures;

public class Castling
extends Fields {
    public static final int[] KINGS_PIDS_BY_COLOUR = new int[Figures.COLOUR_MAX];
    public static final int[] KING_FROM_FIELD_ID_BY_COLOUR;
    public static final int[] KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR;
    public static final int[] KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR;
    public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR;
    public static final int[][] CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR;

    static {
        Castling.KINGS_PIDS_BY_COLOUR[0] = 6;
        Castling.KINGS_PIDS_BY_COLOUR[1] = 12;
        KING_FROM_FIELD_ID_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_FROM_FIELD_ID_BY_COLOUR[0] = Castling.get67IDByBitboard(0x800000000000000L);
        Castling.KING_FROM_FIELD_ID_BY_COLOUR[1] = Castling.get67IDByBitboard(8L);
        KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x200000000000000L);
        Castling.KING_TO_FIELD_ID_ON_KING_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(2L);
        KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX];
        Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[0] = Castling.get67IDByBitboard(0x2000000000000000L);
        Castling.KING_TO_FIELD_ID_ON_QUEEN_SIDE_BY_COLOUR[1] = Castling.get67IDByBitboard(32L);
        CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][2];
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[0][0] = Castling.get67IDByBitboard(0x400000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[0][1] = Castling.get67IDByBitboard(0x200000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[1][0] = Castling.get67IDByBitboard(4L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_KING_SIDE_BY_COLOUR[1][1] = Castling.get67IDByBitboard(2L);
        CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR = new int[Figures.COLOUR_MAX][2];
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[0][0] = Castling.get67IDByBitboard(0x2000000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[0][1] = Castling.get67IDByBitboard(0x1000000000000000L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[1][0] = Castling.get67IDByBitboard(32L);
        Castling.CHECKING_CHECK_FIELD_IDS_ON_QUEEN_SIDE_BY_COLOUR[1][1] = Castling.get67IDByBitboard(16L);
    }
}

