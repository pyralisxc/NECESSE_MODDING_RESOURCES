/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

public class BaseEvalWeights {
    private static final int FIGURE_COST_PAWN_OPENING = 100;
    private static final int FIGURE_COST_KNIGHT_OPENING = 325;
    private static final int FIGURE_COST_OFFICER_OPENING = 325;
    private static final int FIGURE_COST_CASTLE_OPENING = 500;
    private static final int FIGURE_COST_QUEEN_OPENING = 1000;
    private static final int FIGURE_FACTOR_PAWN = 0;
    private static final int FIGURE_FACTOR_KNIGHT = 3;
    private static final int FIGURE_FACTOR_OFFICER = 3;
    private static final int FIGURE_FACTOR_CASTLE = 5;
    private static final int FIGURE_FACTOR_QUEEN = 9;
    private static final int FIGURE_FACTOR_KING = 0;
    private static final int FIGURE_FACTOR_MAX = 62;
    private static final int FIGURE_COST_PAWN_SEE = 100;
    private static final int FIGURE_COST_KNIGHT_SEE = 300;
    private static final int FIGURE_COST_OFFICER_SEE = 300;
    private static final int FIGURE_COST_CASTLE_SEE = 500;
    private static final int FIGURE_COST_QUEEN_SEE = 900;
    private static final int FIGURE_COST_KING_SEE = 3600;
    private static final int FIGURE_COST_KING = 11300;

    public static int getFigureCost(int type) {
        switch (type) {
            case 1: {
                return 100;
            }
            case 2: {
                return 325;
            }
            case 3: {
                return 325;
            }
            case 4: {
                return 500;
            }
            case 5: {
                return 1000;
            }
            case 6: {
                return 11300;
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }

    public static final int interpolateByFactor(int val_o, int val_e, int factor) {
        if (factor > BaseEvalWeights.getMaxMaterialFactor()) {
            factor = BaseEvalWeights.getMaxMaterialFactor();
        }
        int o_part = factor;
        int e_part = BaseEvalWeights.getMaxMaterialFactor() - factor;
        int result = (val_o * o_part + val_e * e_part) / BaseEvalWeights.getMaxMaterialFactor();
        return result;
    }

    public static final int interpolateByFactorAndColour(int val_o, int val_e, int factor) {
        if (factor > BaseEvalWeights.getMaxMaterialFactor() / 2) {
            factor = BaseEvalWeights.getMaxMaterialFactor() / 2;
        }
        int o_part = factor;
        int e_part = BaseEvalWeights.getMaxMaterialFactor() / 2 - factor;
        int result = (val_o * o_part + val_e * e_part) / (BaseEvalWeights.getMaxMaterialFactor() / 2);
        return result;
    }

    public static int getFigureMaterialSEE(int type) {
        switch (type) {
            case 1: {
                return 100;
            }
            case 2: {
                return 300;
            }
            case 3: {
                return 300;
            }
            case 4: {
                return 500;
            }
            case 5: {
                return 900;
            }
            case 6: {
                return 3600;
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }

    public static int getMaxMaterialFactor() {
        return 62;
    }

    public static int getFigureMaterialFactor(int type) {
        switch (type) {
            case 1: {
                return 0;
            }
            case 2: {
                return 3;
            }
            case 3: {
                return 3;
            }
            case 4: {
                return 5;
            }
            case 5: {
                return 9;
            }
            case 6: {
                return 0;
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }
}

