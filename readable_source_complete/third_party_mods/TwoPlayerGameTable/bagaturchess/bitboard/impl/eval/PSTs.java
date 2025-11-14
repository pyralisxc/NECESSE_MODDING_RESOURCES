/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class PSTs {
    private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
    private IBoardConfig boardCfg;
    private double MIN_SCORES_PAWN_O;
    private double MIN_SCORES_PAWN_E;
    private double MIN_SCORES_KING_O;
    private double MIN_SCORES_KING_E;
    private double MIN_SCORES_KNIGHT_O;
    private double MIN_SCORES_KNIGHT_E;
    private double MIN_SCORES_BISHOP_O;
    private double MIN_SCORES_BISHOP_E;
    private double MIN_SCORES_ROOK_O;
    private double MIN_SCORES_ROOK_E;
    private double MIN_SCORES_QUEEN_O;
    private double MIN_SCORES_QUEEN_E;
    private double MAX_SCORES_PAWN_O;
    private double MAX_SCORES_PAWN_E;
    private double MAX_SCORES_KING_O;
    private double MAX_SCORES_KING_E;
    private double MAX_SCORES_KNIGHT_O;
    private double MAX_SCORES_KNIGHT_E;
    private double MAX_SCORES_BISHOP_O;
    private double MAX_SCORES_BISHOP_E;
    private double MAX_SCORES_ROOK_O;
    private double MAX_SCORES_ROOK_E;
    private double MAX_SCORES_QUEEN_O;
    private double MAX_SCORES_QUEEN_E;

    public PSTs(IBoardConfig _boardCfg) {
        this.boardCfg = _boardCfg;
        this.MAX_SCORES_PAWN_O = this.getMax(this.boardCfg.getPST_PAWN_O()) * this.boardCfg.getWeight_PST_PAWN_O();
        this.MAX_SCORES_PAWN_E = this.getMax(this.boardCfg.getPST_PAWN_E()) * this.boardCfg.getWeight_PST_PAWN_E();
        this.MAX_SCORES_KING_O = this.getMax(this.boardCfg.getPST_KING_O()) * this.boardCfg.getWeight_PST_KING_O();
        this.MAX_SCORES_KING_E = this.getMax(this.boardCfg.getPST_KING_E()) * this.boardCfg.getWeight_PST_KING_E();
        this.MAX_SCORES_KNIGHT_O = this.getMax(this.boardCfg.getPST_KNIGHT_O()) * this.boardCfg.getWeight_PST_KNIGHT_O();
        this.MAX_SCORES_KNIGHT_E = this.getMax(this.boardCfg.getPST_KNIGHT_E()) * this.boardCfg.getWeight_PST_KNIGHT_E();
        this.MAX_SCORES_BISHOP_O = this.getMax(this.boardCfg.getPST_BISHOP_O()) * this.boardCfg.getWeight_PST_BISHOP_O();
        this.MAX_SCORES_BISHOP_E = this.getMax(this.boardCfg.getPST_BISHOP_E()) * this.boardCfg.getWeight_PST_BISHOP_E();
        this.MAX_SCORES_ROOK_O = this.getMax(this.boardCfg.getPST_ROOK_O()) * this.boardCfg.getWeight_PST_ROOK_O();
        this.MAX_SCORES_ROOK_E = this.getMax(this.boardCfg.getPST_ROOK_E()) * this.boardCfg.getWeight_PST_ROOK_E();
        this.MAX_SCORES_QUEEN_O = this.getMax(this.boardCfg.getPST_QUEEN_O()) * this.boardCfg.getWeight_PST_QUEEN_O();
        this.MAX_SCORES_QUEEN_E = this.getMax(this.boardCfg.getPST_QUEEN_E()) * this.boardCfg.getWeight_PST_QUEEN_E();
        this.MIN_SCORES_PAWN_O = this.getMin(this.boardCfg.getPST_PAWN_O()) * this.boardCfg.getWeight_PST_PAWN_O();
        this.MIN_SCORES_PAWN_E = this.getMin(this.boardCfg.getPST_PAWN_E()) * this.boardCfg.getWeight_PST_PAWN_E();
        this.MIN_SCORES_KING_O = this.getMin(this.boardCfg.getPST_KING_O()) * this.boardCfg.getWeight_PST_KING_O();
        this.MIN_SCORES_KING_E = this.getMin(this.boardCfg.getPST_KING_E()) * this.boardCfg.getWeight_PST_KING_E();
        this.MIN_SCORES_KNIGHT_O = this.getMin(this.boardCfg.getPST_KNIGHT_O()) * this.boardCfg.getWeight_PST_KNIGHT_O();
        this.MIN_SCORES_KNIGHT_E = this.getMin(this.boardCfg.getPST_KNIGHT_E()) * this.boardCfg.getWeight_PST_KNIGHT_E();
        this.MIN_SCORES_BISHOP_O = this.getMin(this.boardCfg.getPST_BISHOP_O()) * this.boardCfg.getWeight_PST_BISHOP_O();
        this.MIN_SCORES_BISHOP_E = this.getMin(this.boardCfg.getPST_BISHOP_E()) * this.boardCfg.getWeight_PST_BISHOP_E();
        this.MIN_SCORES_ROOK_O = this.getMin(this.boardCfg.getPST_ROOK_O()) * this.boardCfg.getWeight_PST_ROOK_O();
        this.MIN_SCORES_ROOK_E = this.getMin(this.boardCfg.getPST_ROOK_E()) * this.boardCfg.getWeight_PST_ROOK_E();
        this.MIN_SCORES_QUEEN_O = this.getMin(this.boardCfg.getPST_QUEEN_O()) * this.boardCfg.getWeight_PST_QUEEN_O();
        this.MIN_SCORES_QUEEN_E = this.getMin(this.boardCfg.getPST_QUEEN_E()) * this.boardCfg.getWeight_PST_QUEEN_E();
    }

    private double getMax(double[] arr) {
        double max = -1000000.0;
        for (int i = 0; i < arr.length; ++i) {
            if (!(arr[i] > max)) continue;
            max = arr[i];
        }
        return max;
    }

    private double getMin(double[] arr) {
        double min = 1000000.0;
        for (int i = 0; i < arr.length; ++i) {
            if (!(arr[i] < min)) continue;
            min = arr[i];
        }
        return min;
    }

    public final double getMoveScores_o(int move) {
        int type = MoveInt.getFigureType(move);
        int from = MoveInt.getFromFieldID(move);
        int to = MoveInt.getToFieldID(move);
        if (!MoveInt.isWhite(move)) {
            from = HORIZONTAL_SYMMETRY[from];
            to = HORIZONTAL_SYMMETRY[to];
        }
        double[] pst = this.getArray_o(type);
        return pst[to] - pst[from];
    }

    public final double getMoveScores_e(int move) {
        int type = MoveInt.getFigureType(move);
        int from = MoveInt.getFromFieldID(move);
        int to = MoveInt.getToFieldID(move);
        if (!MoveInt.isWhite(move)) {
            from = HORIZONTAL_SYMMETRY[from];
            to = HORIZONTAL_SYMMETRY[to];
        }
        double[] pst = this.getArray_e(type);
        return pst[to] - pst[from];
    }

    public final double getMoveMaxScores_o(int move) {
        int type = MoveInt.getFigureType(move);
        switch (type) {
            case 1: {
                return this.MAX_SCORES_PAWN_O;
            }
            case 6: {
                return this.MAX_SCORES_KING_O;
            }
            case 2: {
                return this.MAX_SCORES_KNIGHT_O;
            }
            case 3: {
                return this.MAX_SCORES_BISHOP_O;
            }
            case 4: {
                return this.MAX_SCORES_ROOK_O;
            }
            case 5: {
                return this.MAX_SCORES_QUEEN_O;
            }
        }
        throw new IllegalStateException();
    }

    public final double getMoveMaxScores_e(int move) {
        int type = MoveInt.getFigureType(move);
        switch (type) {
            case 1: {
                return this.MAX_SCORES_PAWN_E;
            }
            case 6: {
                return this.MAX_SCORES_KING_E;
            }
            case 2: {
                return this.MAX_SCORES_KNIGHT_E;
            }
            case 3: {
                return this.MAX_SCORES_BISHOP_E;
            }
            case 4: {
                return this.MAX_SCORES_ROOK_E;
            }
            case 5: {
                return this.MAX_SCORES_QUEEN_E;
            }
        }
        throw new IllegalStateException();
    }

    public final double getMoveMinScores_o(int move) {
        int type = MoveInt.getFigureType(move);
        switch (type) {
            case 1: {
                return this.MIN_SCORES_PAWN_O;
            }
            case 6: {
                return this.MIN_SCORES_KING_O;
            }
            case 2: {
                return this.MIN_SCORES_KNIGHT_O;
            }
            case 3: {
                return this.MIN_SCORES_BISHOP_O;
            }
            case 4: {
                return this.MIN_SCORES_ROOK_O;
            }
            case 5: {
                return this.MIN_SCORES_QUEEN_O;
            }
        }
        throw new IllegalStateException();
    }

    public final double getMoveMinScores_e(int move) {
        int type = MoveInt.getFigureType(move);
        switch (type) {
            case 1: {
                return this.MIN_SCORES_PAWN_E;
            }
            case 6: {
                return this.MIN_SCORES_KING_E;
            }
            case 2: {
                return this.MIN_SCORES_KNIGHT_E;
            }
            case 3: {
                return this.MIN_SCORES_BISHOP_E;
            }
            case 4: {
                return this.MIN_SCORES_ROOK_E;
            }
            case 5: {
                return this.MIN_SCORES_QUEEN_E;
            }
        }
        throw new IllegalStateException();
    }

    public final double getPieceScores_o(int field, int type) {
        double[] pst = this.getArray_o(type);
        return pst[field];
    }

    public final double getPieceScores_e(int field, int type) {
        double[] pst = this.getArray_e(type);
        return pst[field];
    }

    public final double[] getArray_o(int type) {
        switch (type) {
            case 1: {
                return this.boardCfg.getPST_PAWN_O();
            }
            case 6: {
                return this.boardCfg.getPST_KING_O();
            }
            case 2: {
                return this.boardCfg.getPST_KNIGHT_O();
            }
            case 3: {
                return this.boardCfg.getPST_BISHOP_O();
            }
            case 4: {
                return this.boardCfg.getPST_ROOK_O();
            }
            case 5: {
                return this.boardCfg.getPST_QUEEN_O();
            }
        }
        throw new IllegalStateException();
    }

    public final double[] getArray_e(int type) {
        switch (type) {
            case 1: {
                return this.boardCfg.getPST_PAWN_E();
            }
            case 6: {
                return this.boardCfg.getPST_KING_E();
            }
            case 2: {
                return this.boardCfg.getPST_KNIGHT_E();
            }
            case 3: {
                return this.boardCfg.getPST_BISHOP_E();
            }
            case 4: {
                return this.boardCfg.getPST_ROOK_E();
            }
            case 5: {
                return this.boardCfg.getPST_QUEEN_E();
            }
        }
        throw new IllegalStateException();
    }

    private final double getWeight_o(int type) {
        switch (type) {
            case 1: {
                return this.boardCfg.getWeight_PST_PAWN_O();
            }
            case 6: {
                return this.boardCfg.getWeight_PST_KING_O();
            }
            case 2: {
                return this.boardCfg.getWeight_PST_KNIGHT_O();
            }
            case 3: {
                return this.boardCfg.getWeight_PST_BISHOP_O();
            }
            case 4: {
                return this.boardCfg.getWeight_PST_ROOK_O();
            }
            case 5: {
                return this.boardCfg.getWeight_PST_QUEEN_O();
            }
        }
        throw new IllegalStateException();
    }

    private final double getWeight_e(int type) {
        switch (type) {
            case 1: {
                return this.boardCfg.getWeight_PST_PAWN_E();
            }
            case 6: {
                return this.boardCfg.getWeight_PST_KING_E();
            }
            case 2: {
                return this.boardCfg.getWeight_PST_KNIGHT_E();
            }
            case 3: {
                return this.boardCfg.getWeight_PST_BISHOP_E();
            }
            case 4: {
                return this.boardCfg.getWeight_PST_ROOK_E();
            }
            case 5: {
                return this.boardCfg.getWeight_PST_QUEEN_E();
            }
        }
        throw new IllegalStateException();
    }
}

