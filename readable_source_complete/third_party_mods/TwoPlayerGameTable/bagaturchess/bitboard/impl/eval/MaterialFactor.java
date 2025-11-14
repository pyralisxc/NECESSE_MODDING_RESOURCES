/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.impl.eval;

import bagaturchess.bitboard.api.IMaterialFactor;
import bagaturchess.bitboard.common.MoveListener;
import bagaturchess.bitboard.impl.Figures;
import bagaturchess.bitboard.impl.eval.BaseEvalWeights;
import bagaturchess.bitboard.impl.movegen.MoveInt;

public class MaterialFactor
implements MoveListener,
IMaterialFactor,
Cloneable {
    protected int whiteMaterialFactor;
    protected int blackMaterialFactor;

    public MaterialFactor() {
        this.init();
    }

    private void init() {
        this.whiteMaterialFactor = 0;
        this.blackMaterialFactor = 0;
    }

    @Override
    public int getBlackFactor() {
        return this.blackMaterialFactor;
    }

    @Override
    public int getWhiteFactor() {
        return this.whiteMaterialFactor;
    }

    @Override
    public int getTotalFactor() {
        return this.blackMaterialFactor + this.whiteMaterialFactor;
    }

    @Override
    public double getOpenningPart() {
        double openningPart = (double)this.getTotalFactor() / (double)BaseEvalWeights.getMaxMaterialFactor();
        if (openningPart > 1.0) {
            openningPart = 1.0;
        }
        return openningPart;
    }

    @Override
    public int interpolateByFactor(int val_o, int val_e) {
        double openningPart = this.getOpenningPart();
        return (int)((double)val_o * openningPart + (double)val_e * (1.0 - openningPart));
    }

    @Override
    public int interpolateByFactor(double val_o, double val_e) {
        return this.interpolateByFactor((int)val_o, (int)val_e);
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
    public void preForwardMove(int color, int move) {
    }

    protected void added(int figurePID) {
        this.inc(figurePID);
    }

    protected void removed(int figurePID) {
        this.dec(figurePID);
    }

    private void inc(int figurePID) {
        int figureColour = Figures.getFigureColour(figurePID);
        int figureType = Figures.getFigureType(figurePID);
        int factor = BaseEvalWeights.getFigureMaterialFactor(figureType);
        switch (figureColour) {
            case 0: {
                this.whiteMaterialFactor += factor;
                break;
            }
            case 1: {
                this.blackMaterialFactor += factor;
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
        int factor = BaseEvalWeights.getFigureMaterialFactor(figureType);
        switch (figureColour) {
            case 0: {
                this.whiteMaterialFactor -= factor;
                break;
            }
            case 1: {
                this.blackMaterialFactor -= factor;
                break;
            }
            default: {
                throw new IllegalArgumentException("Figure colour " + figureColour + " is undefined!");
            }
        }
    }

    public MaterialFactor clone() {
        MaterialFactor clone = null;
        try {
            clone = (MaterialFactor)super.clone();
        }
        catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        clone.whiteMaterialFactor = this.whiteMaterialFactor;
        clone.blackMaterialFactor = this.blackMaterialFactor;
        return clone;
    }

    public boolean equals(Object obj) {
        boolean result = false;
        if (obj instanceof MaterialFactor) {
            MaterialFactor other = (MaterialFactor)obj;
            result = other.whiteMaterialFactor == this.whiteMaterialFactor && other.blackMaterialFactor == this.blackMaterialFactor;
        }
        return result;
    }
}

