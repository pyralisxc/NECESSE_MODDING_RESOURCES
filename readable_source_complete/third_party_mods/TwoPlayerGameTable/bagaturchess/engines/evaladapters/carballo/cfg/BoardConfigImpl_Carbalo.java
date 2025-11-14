/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo.cfg;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;

public class BoardConfigImpl_Carbalo
implements IBoardConfig {
    private static double[] zeros = new double[64];
    private double MATERIAL_PAWN_O = 92.0;
    private double MATERIAL_PAWN_E = 92.0;
    private double MATERIAL_KNIGHT_O = 385.0;
    private double MATERIAL_KNIGHT_E = 385.0;
    private double MATERIAL_BISHOP_O = 385.0;
    private double MATERIAL_BISHOP_E = 385.0;
    private double MATERIAL_ROOK_O = 593.0;
    private double MATERIAL_ROOK_E = 593.0;
    private double MATERIAL_QUEEN_O = 1244.0;
    private double MATERIAL_QUEEN_E = 1244.0;
    private double MATERIAL_KING_O = 9900.0;
    private double MATERIAL_KING_E = 9900.0;
    private double MATERIAL_BARIER_NOPAWNS_O = Math.max(this.MATERIAL_KNIGHT_O, this.MATERIAL_BISHOP_O) + this.MATERIAL_PAWN_O;
    private double MATERIAL_BARIER_NOPAWNS_E = Math.max(this.MATERIAL_KNIGHT_E, this.MATERIAL_BISHOP_E) + this.MATERIAL_PAWN_E;
    private static final double[] KING_O = Utils.reverseSpecial(new double[]{-22.0, -35.0, -40.0, -40.0, -40.0, -40.0, -35.0, -22.0, -22.0, -35.0, -40.0, -40.0, -40.0, -40.0, -35.0, -22.0, -25.0, -35.0, -40.0, -45.0, -45.0, -40.0, -35.0, -25.0, -15.0, -30.0, -35.0, -40.0, -40.0, -35.0, -30.0, -15.0, -10.0, -15.0, -20.0, -25.0, -25.0, -20.0, -15.0, -10.0, 4.0, -2.0, -5.0, -15.0, -15.0, -5.0, -2.0, 4.0, 16.0, 14.0, 7.0, -3.0, -3.0, 7.0, 14.0, 16.0, 24.0, 24.0, 9.0, 0.0, 0.0, 9.0, 24.0, 24.0});
    private static final double[] KING_E = Utils.reverseSpecial(new double[]{0.0, 8.0, 16.0, 24.0, 24.0, 16.0, 8.0, 0.0, 8.0, 16.0, 24.0, 32.0, 32.0, 24.0, 16.0, 8.0, 16.0, 24.0, 32.0, 40.0, 40.0, 32.0, 24.0, 16.0, 24.0, 32.0, 40.0, 48.0, 48.0, 40.0, 32.0, 24.0, 24.0, 32.0, 40.0, 48.0, 48.0, 40.0, 32.0, 24.0, 16.0, 24.0, 32.0, 40.0, 40.0, 32.0, 24.0, 16.0, 8.0, 16.0, 24.0, 32.0, 32.0, 24.0, 16.0, 8.0, 0.0, 8.0, 16.0, 24.0, 24.0, 16.0, 8.0, 0.0});
    private static final double[] PAWN_O = Utils.reverseSpecial(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 8.0, 16.0, 24.0, 32.0, 32.0, 24.0, 16.0, 8.0, 3.0, 12.0, 20.0, 28.0, 28.0, 20.0, 12.0, 3.0, -5.0, 4.0, 10.0, 20.0, 20.0, 10.0, 4.0, -5.0, -6.0, 4.0, 5.0, 16.0, 16.0, 5.0, 4.0, -6.0, -6.0, 4.0, 2.0, 5.0, 5.0, 2.0, 4.0, -6.0, -6.0, 4.0, 4.0, -15.0, -15.0, 4.0, 4.0, -6.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
    private static final double[] PAWN_E = Utils.reverseSpecial(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 25.0, 40.0, 45.0, 45.0, 45.0, 45.0, 40.0, 25.0, 17.0, 32.0, 35.0, 35.0, 35.0, 35.0, 32.0, 17.0, 5.0, 24.0, 24.0, 24.0, 24.0, 24.0, 24.0, 5.0, -9.0, 11.0, 11.0, 11.0, 11.0, 11.0, 11.0, -9.0, -17.0, 3.0, 3.0, 3.0, 3.0, 3.0, 3.0, -17.0, -20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -20.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
    public static final double[] KNIGHT_O = Utils.reverseSpecial(new double[]{-53.0, -42.0, -32.0, -21.0, -21.0, -32.0, -42.0, -53.0, -42.0, -32.0, -10.0, 0.0, 0.0, -10.0, -32.0, -42.0, -21.0, 5.0, 10.0, 16.0, 16.0, 10.0, 5.0, -21.0, -18.0, 0.0, 10.0, 21.0, 21.0, 10.0, 0.0, -18.0, -18.0, 0.0, 3.0, 21.0, 21.0, 3.0, 0.0, -18.0, -21.0, -10.0, 0.0, 0.0, 0.0, 0.0, -10.0, -21.0, -42.0, -32.0, -10.0, 0.0, 0.0, -10.0, -32.0, -42.0, -53.0, -42.0, -32.0, -21.0, -21.0, -32.0, -42.0, -53.0});
    private static final double[] KNIGHT_E = Utils.reverseSpecial(new double[]{-56.0, -44.0, -34.0, -22.0, -22.0, -34.0, -44.0, -56.0, -44.0, -34.0, -10.0, 0.0, 0.0, -10.0, -34.0, -44.0, -22.0, 5.0, 10.0, 17.0, 17.0, 10.0, 5.0, -22.0, -19.0, 0.0, 10.0, 22.0, 22.0, 10.0, 0.0, -19.0, -19.0, 0.0, 3.0, 22.0, 22.0, 3.0, 0.0, -19.0, -22.0, -10.0, 0.0, 0.0, 0.0, 0.0, -10.0, -22.0, -44.0, -34.0, -10.0, 0.0, 0.0, -10.0, -34.0, -44.0, -56.0, -44.0, -34.0, -22.0, -22.0, -34.0, -44.0, -56.0});
    public static final double[] BISHOP_O = Utils.reverseSpecial(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 3.0, 4.0, 4.0, 4.0, 4.0, 3.0, 0.0, 0.0, 4.0, 2.0, 2.0, 2.0, 2.0, 4.0, 0.0, -5.0, -5.0, -7.0, -5.0, -5.0, -7.0, -5.0, -5.0});
    private static final double[] BISHOP_E = Utils.reverseSpecial(new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 4.0, 4.0, 4.0, 4.0, 2.0, 0.0, 0.0, 2.0, 2.0, 2.0, 2.0, 2.0, 2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0});
    private static final double[] ROOK_O = Utils.reverseSpecial(new double[]{0.0, 3.0, 5.0, 5.0, 5.0, 5.0, 3.0, 0.0, 15.0, 20.0, 20.0, 20.0, 20.0, 20.0, 20.0, 15.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, -2.0, -2.0, 0.0, 0.0, 2.0, 2.0, 0.0, 0.0, -2.0, -3.0, 2.0, 5.0, 5.0, 5.0, 5.0, 2.0, -3.0, 0.0, 3.0, 5.0, 5.0, 5.0, 5.0, 3.0, 0.0});
    private static final double[] ROOK_E = zeros;
    private static final double[] QUEEN_O = Utils.reverseSpecial(new double[]{-10.0, -5.0, 0.0, 0.0, 0.0, 0.0, -5.0, -10.0, -5.0, 0.0, 5.0, 5.0, 5.0, 5.0, 0.0, -5.0, 0.0, 5.0, 5.0, 6.0, 6.0, 5.0, 5.0, 0.0, 0.0, 5.0, 6.0, 6.0, 6.0, 6.0, 5.0, 0.0, 0.0, 5.0, 6.0, 6.0, 6.0, 6.0, 5.0, 0.0, 0.0, 5.0, 5.0, 6.0, 6.0, 5.0, 5.0, 0.0, -5.0, 0.0, 5.0, 5.0, 5.0, 5.0, 0.0, -5.0, -10.0, -5.0, 0.0, 0.0, 0.0, 0.0, -5.0, -10.0});
    private static final double[] QUEEN_E = zeros;

    @Override
    public boolean getFieldsStatesSupport() {
        return false;
    }

    @Override
    public double[] getPST_PAWN_O() {
        return PAWN_O;
    }

    @Override
    public double[] getPST_PAWN_E() {
        return PAWN_E;
    }

    @Override
    public double[] getPST_KING_O() {
        return KING_O;
    }

    @Override
    public double[] getPST_KING_E() {
        return KING_E;
    }

    @Override
    public double[] getPST_KNIGHT_O() {
        return KNIGHT_O;
    }

    @Override
    public double[] getPST_KNIGHT_E() {
        return KNIGHT_E;
    }

    @Override
    public double[] getPST_BISHOP_O() {
        return BISHOP_O;
    }

    @Override
    public double[] getPST_BISHOP_E() {
        return BISHOP_E;
    }

    @Override
    public double[] getPST_ROOK_O() {
        return ROOK_O;
    }

    @Override
    public double[] getPST_ROOK_E() {
        return ROOK_E;
    }

    @Override
    public double[] getPST_QUEEN_O() {
        return QUEEN_O;
    }

    @Override
    public double[] getPST_QUEEN_E() {
        return QUEEN_E;
    }

    @Override
    public double getMaterial_PAWN_O() {
        return this.MATERIAL_PAWN_O;
    }

    @Override
    public double getMaterial_PAWN_E() {
        return this.MATERIAL_PAWN_E;
    }

    @Override
    public double getMaterial_KING_O() {
        return this.MATERIAL_KING_O;
    }

    @Override
    public double getMaterial_KING_E() {
        return this.MATERIAL_KING_E;
    }

    @Override
    public double getMaterial_KNIGHT_O() {
        return this.MATERIAL_KNIGHT_O;
    }

    @Override
    public double getMaterial_KNIGHT_E() {
        return this.MATERIAL_KNIGHT_E;
    }

    @Override
    public double getMaterial_BISHOP_O() {
        return this.MATERIAL_BISHOP_O;
    }

    @Override
    public double getMaterial_BISHOP_E() {
        return this.MATERIAL_BISHOP_E;
    }

    @Override
    public double getMaterial_ROOK_O() {
        return this.MATERIAL_ROOK_O;
    }

    @Override
    public double getMaterial_ROOK_E() {
        return this.MATERIAL_ROOK_E;
    }

    @Override
    public double getMaterial_QUEEN_O() {
        return this.MATERIAL_QUEEN_O;
    }

    @Override
    public double getMaterial_QUEEN_E() {
        return this.MATERIAL_QUEEN_E;
    }

    @Override
    public double getMaterial_BARIER_NOPAWNS_O() {
        return this.MATERIAL_BARIER_NOPAWNS_O;
    }

    @Override
    public double getMaterial_BARIER_NOPAWNS_E() {
        return this.MATERIAL_BARIER_NOPAWNS_E;
    }

    @Override
    public double getWeight_PST_PAWN_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_PAWN_E() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_KING_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_KING_E() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_KNIGHT_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_KNIGHT_E() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_BISHOP_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_BISHOP_E() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_ROOK_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_ROOK_E() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_QUEEN_O() {
        return 1.0;
    }

    @Override
    public double getWeight_PST_QUEEN_E() {
        return 1.0;
    }
}

