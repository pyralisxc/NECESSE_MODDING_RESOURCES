/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.api;

public interface IBoardConfig {
    public boolean getFieldsStatesSupport();

    public double getMaterial_PAWN_O();

    public double getMaterial_PAWN_E();

    public double getMaterial_KING_O();

    public double getMaterial_KING_E();

    public double getMaterial_KNIGHT_O();

    public double getMaterial_KNIGHT_E();

    public double getMaterial_BISHOP_O();

    public double getMaterial_BISHOP_E();

    public double getMaterial_ROOK_O();

    public double getMaterial_ROOK_E();

    public double getMaterial_QUEEN_O();

    public double getMaterial_QUEEN_E();

    public double getMaterial_BARIER_NOPAWNS_O();

    public double getMaterial_BARIER_NOPAWNS_E();

    public double[] getPST_PAWN_O();

    public double[] getPST_PAWN_E();

    public double[] getPST_KING_O();

    public double[] getPST_KING_E();

    public double[] getPST_KNIGHT_O();

    public double[] getPST_KNIGHT_E();

    public double[] getPST_BISHOP_O();

    public double[] getPST_BISHOP_E();

    public double[] getPST_ROOK_O();

    public double[] getPST_ROOK_E();

    public double[] getPST_QUEEN_O();

    public double[] getPST_QUEEN_E();

    public double getWeight_PST_PAWN_O();

    public double getWeight_PST_PAWN_E();

    public double getWeight_PST_KING_O();

    public double getWeight_PST_KING_E();

    public double getWeight_PST_KNIGHT_O();

    public double getWeight_PST_KNIGHT_E();

    public double getWeight_PST_BISHOP_O();

    public double getWeight_PST_BISHOP_E();

    public double getWeight_PST_ROOK_O();

    public double getWeight_PST_ROOK_E();

    public double getWeight_PST_QUEEN_O();

    public double getWeight_PST_QUEEN_E();
}

