/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl1;

import bagaturchess.bitboard.api.IBaseEval;
import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.bitboard.api.IBoardConfig;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.eval.PSTs;

public class BaseEvaluation
implements MoveListener,
IBaseEval {
    private IBitBoard board;
    private IBoardConfig boardConfig;
    private PSTs psts;
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

    public BaseEvaluation(IBoardConfig _boardConfig, IBitBoard _board) {
        this.boardConfig = _boardConfig;
        this.board = _board;
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

    public void move(int move, int color) {
    }

    public void unmove(int move, int color) {
    }

    protected void inc(int color, int type) {
        switch (color) {
            case 0: {
                if (type == 1) {
                    this.w_material_pawns_o += this.getFigureMaterial_O(type);
                    this.w_material_pawns_e += this.getFigureMaterial_E(type);
                    break;
                }
                this.w_material_nopawns_o += this.getFigureMaterial_O(type);
                this.w_material_nopawns_e += this.getFigureMaterial_E(type);
                break;
            }
            case 1: {
                if (type == 1) {
                    this.b_material_pawns_o += this.getFigureMaterial_O(type);
                    this.b_material_pawns_e += this.getFigureMaterial_E(type);
                    break;
                }
                this.b_material_nopawns_o += this.getFigureMaterial_O(type);
                this.b_material_nopawns_e += this.getFigureMaterial_E(type);
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure colour " + type + " is undefined!");
            }
        }
    }

    protected void dec(int color, int type) {
        switch (color) {
            case 0: {
                if (type == 1) {
                    this.w_material_pawns_o -= this.getFigureMaterial_O(type);
                    this.w_material_pawns_e -= this.getFigureMaterial_E(type);
                    break;
                }
                this.w_material_nopawns_o -= this.getFigureMaterial_O(type);
                this.w_material_nopawns_e -= this.getFigureMaterial_E(type);
                break;
            }
            case 1: {
                if (type == 1) {
                    this.b_material_pawns_o -= this.getFigureMaterial_O(type);
                    this.b_material_pawns_e -= this.getFigureMaterial_E(type);
                    break;
                }
                this.b_material_nopawns_o -= this.getFigureMaterial_O(type);
                this.b_material_nopawns_e -= this.getFigureMaterial_E(type);
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure colour " + type + " is undefined!");
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
    public void addPiece_Special(int color, int type) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void initially_addPiece(int color, int type, long bb_pieces) {
        this.inc(color, type);
    }

    @Override
    public void preForwardMove(int color, int move) {
        if (this.board.getMoveOps().isCapture(move)) {
            int cap_type = this.board.getMoveOps().getCapturedFigureType(move);
            this.dec(1 - color, cap_type);
        }
        if (this.board.getMoveOps().isPromotion(move)) {
            int prom_type = this.board.getMoveOps().getPromotionFigureType(move);
            this.inc(color, prom_type);
            int type = this.board.getMoveOps().getFigureType(move);
            if (type != 1) {
                throw new IllegalStateException();
            }
            this.dec(color, type);
        }
    }

    @Override
    public void postForwardMove(int color, int move) {
    }

    @Override
    public void preBackwardMove(int color, int move) {
        if (this.board.getMoveOps().isCapture(move)) {
            int cap_type = this.board.getMoveOps().getCapturedFigureType(move);
            this.inc(1 - color, cap_type);
        }
        if (this.board.getMoveOps().isPromotion(move)) {
            int prom_type = this.board.getMoveOps().getPromotionFigureType(move);
            this.dec(color, prom_type);
            int type = this.board.getMoveOps().getFigureType(move);
            if (type != 1) {
                throw new IllegalStateException();
            }
            this.inc(color, type);
        }
    }

    @Override
    public void postBackwardMove(int color, int move) {
    }

    @Override
    public int getMaterial(int pieceType) {
        switch (pieceType) {
            case 1: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_PAWN_O(), this.boardConfig.getMaterial_PAWN_E());
            }
            case 2: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_KNIGHT_O(), this.boardConfig.getMaterial_KNIGHT_E());
            }
            case 3: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_BISHOP_O(), this.boardConfig.getMaterial_BISHOP_E());
            }
            case 4: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_ROOK_O(), this.boardConfig.getMaterial_ROOK_E());
            }
            case 5: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_QUEEN_O(), this.boardConfig.getMaterial_QUEEN_E());
            }
            case 6: {
                return this.board.getMaterialFactor().interpolateByFactor(this.boardConfig.getMaterial_KING_O(), this.boardConfig.getMaterial_KING_E());
            }
        }
        throw new IllegalArgumentException("Figure type " + pieceType + " is undefined!");
    }

    @Override
    public int getMaterialGain(int move) {
        if (!this.board.getMoveOps().isCapture(move) && !this.board.getMoveOps().isPromotion(move)) {
            return 0;
        }
        int val = 0;
        if (this.board.getMoveOps().isCapture(move)) {
            int captured_type = this.board.getMoveOps().getCapturedFigureType(move);
            val += this.getMaterial(captured_type);
        }
        if (this.board.getMoveOps().isPromotion(move)) {
            int prom_type = this.board.getMoveOps().getPromotionFigureType(move);
            val += this.getMaterial(prom_type);
            val -= this.getMaterial(1);
        }
        return val;
    }
}

