/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.common.Utils;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.PSTs;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class BaseEvaluation
implements MoveListener,
IBaseEval {
    private static final int[] HORIZONTAL_SYMMETRY = Utils.reverseSpecial(new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, 62, 63});
    private double w_material_nopawns_o;
    private double b_material_nopawns_o;
    private double w_material_nopawns_e;
    private double b_material_nopawns_e;
    private double w_material_pawns_o;
    private double b_material_pawns_o;
    private double w_material_pawns_e;
    private double b_material_pawns_e;
    private double whitePST_o;
    private double blackPST_o;
    private double whitePST_e;
    private double blackPST_e;
    private IBoardConfig boardConfig;
    private PSTs pst;
    private IMaterialFactor interpolator;

    public BaseEvaluation(IBoardConfig _boardConfig, IMaterialFactor _interpolator) {
        this.boardConfig = _boardConfig;
        this.interpolator = _interpolator;
        this.pst = new PSTs(this.boardConfig);
        this.w_material_nopawns_o = 0.0;
        this.b_material_nopawns_o = 0.0;
        this.w_material_nopawns_e = 0.0;
        this.b_material_nopawns_e = 0.0;
        this.w_material_pawns_o = 0.0;
        this.b_material_pawns_o = 0.0;
        this.w_material_pawns_e = 0.0;
        this.b_material_pawns_e = 0.0;
        this.whitePST_o = 0.0;
        this.blackPST_o = 0.0;
        this.whitePST_e = 0.0;
        this.blackPST_e = 0.0;
    }

    @Override
    public int getPST_e() {
        return (int)(this.whitePST_e - this.blackPST_e);
    }

    @Override
    public int getPST_o() {
        return (int)(this.whitePST_o - this.blackPST_o);
    }

    @Override
    public int getMaterial_o() {
        return this.getWhiteMaterialPawns_o() + this.getWhiteMaterialNonPawns_o() - this.getBlackMaterialPawns_o() - this.getBlackMaterialNonPawns_o();
    }

    @Override
    public int getMaterial_e() {
        return this.getWhiteMaterialPawns_e() + this.getWhiteMaterialNonPawns_e() - this.getBlackMaterialPawns_e() - this.getBlackMaterialNonPawns_e();
    }

    @Override
    public int getWhiteMaterialPawns_o() {
        return (int)this.w_material_pawns_o;
    }

    @Override
    public int getWhiteMaterialPawns_e() {
        return (int)this.w_material_pawns_e;
    }

    @Override
    public int getBlackMaterialPawns_o() {
        return (int)this.b_material_pawns_o;
    }

    @Override
    public int getBlackMaterialPawns_e() {
        return (int)this.b_material_pawns_e;
    }

    @Override
    public int getWhiteMaterialNonPawns_o() {
        return (int)this.w_material_nopawns_o;
    }

    @Override
    public int getWhiteMaterialNonPawns_e() {
        return (int)this.w_material_nopawns_e;
    }

    @Override
    public int getBlackMaterialNonPawns_o() {
        return (int)this.b_material_nopawns_o;
    }

    @Override
    public int getBlackMaterialNonPawns_e() {
        return (int)this.b_material_nopawns_e;
    }

    @Override
    public int getMaterial_BARIER_NOPAWNS_O() {
        return (int)this.boardConfig.getMaterial_BARIER_NOPAWNS_O();
    }

    @Override
    public int getMaterial_BARIER_NOPAWNS_E() {
        return (int)this.boardConfig.getMaterial_BARIER_NOPAWNS_E();
    }

    public void move(int move) {
        int col = MoveInt.getColour(move);
        int toFieldID = MoveInt.getToFieldID(move);
        if (col == 0) {
            this.whitePST_o += this.pst.getMoveScores_o(move);
            this.whitePST_e += this.pst.getMoveScores_e(move);
            if (MoveInt.isEnpassant(move)) {
                int enCapField = MoveInt.getEnpassantCapturedFieldID(move);
                this.blackPST_o -= this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[enCapField], 1);
                this.blackPST_e -= this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[enCapField], 1);
            } else {
                if (MoveInt.isCapture(move)) {
                    int capType = MoveInt.getCapturedFigureType(move);
                    this.blackPST_o -= this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], capType);
                    this.blackPST_e -= this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], capType);
                }
                if (MoveInt.isPromotion(move)) {
                    int promType = MoveInt.getPromotionFigureType(move);
                    this.whitePST_o += this.pst.getPieceScores_o(toFieldID, promType);
                    this.whitePST_e += this.pst.getPieceScores_e(toFieldID, promType);
                }
                if (MoveInt.isCastling(move)) {
                    int castFromID = MoveInt.getCastlingRookFromID(move);
                    int castToID = MoveInt.getCastlingRookToID(move);
                    this.whitePST_o -= this.pst.getPieceScores_o(castFromID, 4);
                    this.whitePST_o += this.pst.getPieceScores_o(castToID, 4);
                    this.whitePST_e -= this.pst.getPieceScores_e(castFromID, 4);
                    this.whitePST_e += this.pst.getPieceScores_e(castToID, 4);
                }
            }
        } else {
            this.blackPST_o += this.pst.getMoveScores_o(move);
            this.blackPST_e += this.pst.getMoveScores_e(move);
            if (MoveInt.isEnpassant(move)) {
                int enCapField = MoveInt.getEnpassantCapturedFieldID(move);
                this.whitePST_o -= this.pst.getPieceScores_o(enCapField, 1);
                this.whitePST_e -= this.pst.getPieceScores_e(enCapField, 1);
            } else {
                if (MoveInt.isCapture(move)) {
                    int capType = MoveInt.getCapturedFigureType(move);
                    this.whitePST_o -= this.pst.getPieceScores_o(toFieldID, capType);
                    this.whitePST_e -= this.pst.getPieceScores_e(toFieldID, capType);
                }
                if (MoveInt.isPromotion(move)) {
                    int promType = MoveInt.getPromotionFigureType(move);
                    this.blackPST_o += this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], promType);
                    this.blackPST_e += this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], promType);
                }
                if (MoveInt.isCastling(move)) {
                    int castFromID = MoveInt.getCastlingRookFromID(move);
                    int castToID = MoveInt.getCastlingRookToID(move);
                    this.blackPST_o -= this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castFromID], 4);
                    this.blackPST_o += this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castToID], 4);
                    this.blackPST_e -= this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castFromID], 4);
                    this.blackPST_e += this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castToID], 4);
                }
            }
        }
    }

    public void unmove(int move) {
        int col = MoveInt.getColour(move);
        int toFieldID = MoveInt.getToFieldID(move);
        if (col == 0) {
            this.whitePST_o -= this.pst.getMoveScores_o(move);
            this.whitePST_e -= this.pst.getMoveScores_e(move);
            if (MoveInt.isEnpassant(move)) {
                int enCapField = MoveInt.getEnpassantCapturedFieldID(move);
                this.blackPST_o += this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[enCapField], 1);
                this.blackPST_e += this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[enCapField], 1);
            } else {
                if (MoveInt.isCapture(move)) {
                    int capType = MoveInt.getCapturedFigureType(move);
                    this.blackPST_o += this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], capType);
                    this.blackPST_e += this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], capType);
                }
                if (MoveInt.isPromotion(move)) {
                    int promType = MoveInt.getPromotionFigureType(move);
                    this.whitePST_o -= this.pst.getPieceScores_o(toFieldID, promType);
                    this.whitePST_e -= this.pst.getPieceScores_e(toFieldID, promType);
                }
                if (MoveInt.isCastling(move)) {
                    int castFromID = MoveInt.getCastlingRookFromID(move);
                    int castToID = MoveInt.getCastlingRookToID(move);
                    this.whitePST_o += this.pst.getPieceScores_o(castFromID, 4);
                    this.whitePST_o -= this.pst.getPieceScores_o(castToID, 4);
                    this.whitePST_e += this.pst.getPieceScores_e(castFromID, 4);
                    this.whitePST_e -= this.pst.getPieceScores_e(castToID, 4);
                }
            }
        } else {
            this.blackPST_o -= this.pst.getMoveScores_o(move);
            this.blackPST_e -= this.pst.getMoveScores_e(move);
            if (MoveInt.isEnpassant(move)) {
                int enCapField = MoveInt.getEnpassantCapturedFieldID(move);
                this.whitePST_o += this.pst.getPieceScores_o(enCapField, 1);
                this.whitePST_e += this.pst.getPieceScores_e(enCapField, 1);
            } else {
                if (MoveInt.isCapture(move)) {
                    int capType = MoveInt.getCapturedFigureType(move);
                    this.whitePST_o += this.pst.getPieceScores_o(toFieldID, capType);
                    this.whitePST_e += this.pst.getPieceScores_e(toFieldID, capType);
                }
                if (MoveInt.isPromotion(move)) {
                    int promType = MoveInt.getPromotionFigureType(move);
                    this.blackPST_o -= this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[toFieldID], promType);
                    this.blackPST_e -= this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[toFieldID], promType);
                }
                if (MoveInt.isCastling(move)) {
                    int castFromID = MoveInt.getCastlingRookFromID(move);
                    int castToID = MoveInt.getCastlingRookToID(move);
                    this.blackPST_o += this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castFromID], 4);
                    this.blackPST_o -= this.pst.getPieceScores_o(HORIZONTAL_SYMMETRY[castToID], 4);
                    this.blackPST_e += this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castFromID], 4);
                    this.blackPST_e -= this.pst.getPieceScores_e(HORIZONTAL_SYMMETRY[castToID], 4);
                }
            }
        }
    }

    protected void inc(int figurePID) {
        int figureColour = Figures.getFigureColour(figurePID);
        int figureType = Figures.getFigureType(figurePID);
        switch (figureColour) {
            case 0: {
                if (figureType == 1) {
                    this.w_material_pawns_o += this.getFigureMaterial_O(figureType);
                    this.w_material_pawns_e += this.getFigureMaterial_E(figureType);
                    break;
                }
                this.w_material_nopawns_o += this.getFigureMaterial_O(figureType);
                this.w_material_nopawns_e += this.getFigureMaterial_E(figureType);
                break;
            }
            case 1: {
                if (figureType == 1) {
                    this.b_material_pawns_o += this.getFigureMaterial_O(figureType);
                    this.b_material_pawns_e += this.getFigureMaterial_E(figureType);
                    break;
                }
                this.b_material_nopawns_o += this.getFigureMaterial_O(figureType);
                this.b_material_nopawns_e += this.getFigureMaterial_E(figureType);
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure colour " + figureColour + " is undefined!");
            }
        }
    }

    protected void dec(int figurePID) {
        int figureColour = Figures.getFigureColour(figurePID);
        int figureType = Figures.getFigureType(figurePID);
        switch (figureColour) {
            case 0: {
                if (figureType == 1) {
                    this.w_material_pawns_o -= this.getFigureMaterial_O(figureType);
                    this.w_material_pawns_e -= this.getFigureMaterial_E(figureType);
                    break;
                }
                this.w_material_nopawns_o -= this.getFigureMaterial_O(figureType);
                this.w_material_nopawns_e -= this.getFigureMaterial_E(figureType);
                break;
            }
            case 1: {
                if (figureType == 1) {
                    this.b_material_pawns_o -= this.getFigureMaterial_O(figureType);
                    this.b_material_pawns_e -= this.getFigureMaterial_E(figureType);
                    break;
                }
                this.b_material_nopawns_o -= this.getFigureMaterial_O(figureType);
                this.b_material_nopawns_e -= this.getFigureMaterial_E(figureType);
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure colour " + figureColour + " is undefined!");
            }
        }
    }

    public double getFigureMaterial_O(int type) {
        switch (type) {
            case 1: {
                return this.boardConfig.getMaterial_PAWN_O();
            }
            case 2: {
                return this.boardConfig.getMaterial_KNIGHT_O();
            }
            case 3: {
                return this.boardConfig.getMaterial_BISHOP_O();
            }
            case 4: {
                return this.boardConfig.getMaterial_ROOK_O();
            }
            case 5: {
                return this.boardConfig.getMaterial_QUEEN_O();
            }
            case 6: {
                return this.boardConfig.getMaterial_KING_O();
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }

    public double getFigureMaterial_E(int type) {
        switch (type) {
            case 1: {
                return this.boardConfig.getMaterial_PAWN_E();
            }
            case 2: {
                return this.boardConfig.getMaterial_KNIGHT_E();
            }
            case 3: {
                return this.boardConfig.getMaterial_BISHOP_E();
            }
            case 4: {
                return this.boardConfig.getMaterial_ROOK_E();
            }
            case 5: {
                return this.boardConfig.getMaterial_QUEEN_E();
            }
            case 6: {
                return this.boardConfig.getMaterial_KING_E();
            }
        }
        throw new IllegalArgumentException("Figure type " + type + " is undefined!");
    }

    @Override
    public void addPiece_Special(int pid, int fieldID) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initially_addPiece(int pid, int fieldID, long bb_pieces) {
        this.added(pid);
    }

    @Override
    public void preForwardMove(int color, int move) {
    }

    @Override
    public void postForwardMove(int color, int move) {
        if (MoveInt.isCapture(move)) {
            int cap_pid = MoveInt.getCapturedFigurePID(move);
            this.removed(cap_pid);
        }
        if (MoveInt.isPromotion(move)) {
            int prom_pid = MoveInt.getPromotionFigurePID(move);
            this.added(prom_pid);
            int pid = MoveInt.getFigurePID(move);
            this.removed(pid);
        }
    }

    @Override
    public void preBackwardMove(int color, int move) {
    }

    @Override
    public void postBackwardMove(int color, int move) {
        if (MoveInt.isCapture(move)) {
            int cap_pid = MoveInt.getCapturedFigurePID(move);
            this.added(cap_pid);
        }
        if (MoveInt.isPromotion(move)) {
            int prom_pid = MoveInt.getPromotionFigurePID(move);
            this.removed(prom_pid);
            int pid = MoveInt.getFigurePID(move);
            this.added(pid);
        }
    }

    protected void added(int figurePID) {
        this.inc(figurePID);
    }

    protected void removed(int figurePID) {
        this.dec(figurePID);
    }

    @Override
    public int getMaterial(int pieceType) {
        switch (pieceType) {
            case 1: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_PAWN_O(), this.boardConfig.getMaterial_PAWN_E());
            }
            case 2: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_KNIGHT_O(), this.boardConfig.getMaterial_KNIGHT_E());
            }
            case 3: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_BISHOP_O(), this.boardConfig.getMaterial_BISHOP_E());
            }
            case 4: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_ROOK_O(), this.boardConfig.getMaterial_ROOK_E());
            }
            case 5: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_QUEEN_O(), this.boardConfig.getMaterial_QUEEN_E());
            }
            case 6: {
                return this.interpolator.interpolateByFactor(this.boardConfig.getMaterial_KING_O(), this.boardConfig.getMaterial_KING_E());
            }
        }
        throw new IllegalArgumentException("Figure type " + pieceType + " is undefined!");
    }

    @Override
    public int getMaterialGain(int move) {
        if (!MoveInt.isCapture(move) && !MoveInt.isPromotion(move)) {
            return 0;
        }
        int val = 0;
        if (MoveInt.isCapture(move)) {
            int capturedPID = MoveInt.getCapturedFigurePID(move);
            int figureType = Figures.getFigureType(capturedPID);
            val += this.getMaterial(figureType);
        }
        if (MoveInt.isPromotion(move)) {
            int promID = MoveInt.getPromotionFigurePID(move);
            int promType = Figures.getFigureType(promID);
            val += this.getMaterial(promType);
            val -= this.getMaterial(1);
        }
        return val;
    }

    public double getPSTMoveGoodPercent(int move) {
        int min = this.interpolator.interpolateByFactor(this.pst.getMoveMinScores_o(move), this.pst.getMoveMinScores_e(move));
        int max = this.interpolator.interpolateByFactor(this.pst.getMoveMaxScores_o(move), this.pst.getMoveMaxScores_e(move));
        int score = this.interpolator.interpolateByFactor(this.pst.getMoveScores_o(move), this.pst.getMoveScores_e(move));
        double b = max - min;
        if (b == 0.0) {
            return 0.0;
        }
        double result = (double)Math.abs(score) / b;
        if (result > 1.0) {
            result = 1.0;
        }
        if (result < 0.0) {
            result = 0.0;
        }
        return result;
    }
}

