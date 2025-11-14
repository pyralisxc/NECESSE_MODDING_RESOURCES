/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.bitboard.common;

import java.util.ArrayList;
import java.util.List;

public class BoardStat {
    public Measure forwardMove = new Measure("forwardMove");
    public Measure backwardMove = new Measure("backwardMove");
    public Measure forwardNullMove = new Measure("forwardNullMove");
    public Measure backwardNullMove = new Measure("backwardNullMove");
    public Measure allMoves = new Measure("allMoves");
    public Measure capturePromotionMoves = new Measure("capturePromotionMoves");
    public Measure nonCaptureNonPromotionMoves = new Measure("nonCaptureNonPromotionMoves");
    public Measure promotions = new Measure("promotions");
    public Measure promotions2 = new Measure("promotions2");
    public Measure directCheckMoves = new Measure("directCheckMoves");
    public Measure hiddenCheckMoves = new Measure("hiddenCheckMoves");
    public Measure allCheckMoves = new Measure("allCheckMoves");
    public Measure capturePromotionCheckMoves = new Measure("capturePromotionCheckMoves");
    public Measure kingEscapes = new Measure("kingEscapes");
    public Measure hasMove = new Measure("hasMove");
    public Measure hasMoveInNonCheck = new Measure("hasMoveInNonCheck");
    public Measure hasMoveInCheck = new Measure("hasMoveInCheck");
    public Measure countMoves = new Measure("countMoves");
    public Measure countMovesInCheck = new Measure("countMovesInCheck");
    public Measure countMovesInNonCheck = new Measure("countMovesInNonCheck");
    public Measure countCheckMoves = new Measure("countCheckMoves");
    public Measure countCapturePromotionMovesInNonCheck = new Measure("countCapturePromotionMovesInNonCheck");
    public Measure countCapturePromotionCheckMovesInNonCheck = new Measure("countCapturePromotionCheckMovesInNonCheck");
    public Measure isInCheck = new Measure("isInCheck");
    public Measure isCheckMove = new Measure("isCheckMove");
    public Measure isDirectCheckMove = new Measure("isDirectCheckMove");
    public Measure checksCount = new Measure("checksCount");
    public Measure isPossible = new Measure("isPossible");
    public Measure stateRepetition = new Measure("stateRepetition");
    public Measure fillCheckKeepers = new Measure("fillCheckKeepers");
    public Measure testing = new Measure("testing");
    private List<Measure> measures = new ArrayList<Measure>();

    public BoardStat() {
        this.measures.add(this.forwardMove);
        this.measures.add(this.backwardMove);
        this.measures.add(this.forwardNullMove);
        this.measures.add(this.backwardNullMove);
        this.measures.add(this.allMoves);
        this.measures.add(this.capturePromotionMoves);
        this.measures.add(this.nonCaptureNonPromotionMoves);
        this.measures.add(this.promotions);
        this.measures.add(this.promotions2);
        this.measures.add(this.directCheckMoves);
        this.measures.add(this.hiddenCheckMoves);
        this.measures.add(this.allCheckMoves);
        this.measures.add(this.capturePromotionCheckMoves);
        this.measures.add(this.kingEscapes);
        this.measures.add(this.hasMove);
        this.measures.add(this.hasMoveInNonCheck);
        this.measures.add(this.hasMoveInCheck);
        this.measures.add(this.countMoves);
        this.measures.add(this.countMovesInCheck);
        this.measures.add(this.countMovesInNonCheck);
        this.measures.add(this.countCheckMoves);
        this.measures.add(this.countCapturePromotionMovesInNonCheck);
        this.measures.add(this.countCapturePromotionCheckMovesInNonCheck);
        this.measures.add(this.isInCheck);
        this.measures.add(this.isCheckMove);
        this.measures.add(this.isDirectCheckMove);
        this.measures.add(this.checksCount);
        this.measures.add(this.isPossible);
        this.measures.add(this.stateRepetition);
        this.measures.add(this.fillCheckKeepers);
        this.measures.add(this.testing);
    }

    public void clear() {
        this.forwardMove.clear();
        this.backwardMove.clear();
        this.forwardNullMove.clear();
        this.backwardNullMove.clear();
        this.allMoves.clear();
        this.capturePromotionMoves.clear();
        this.nonCaptureNonPromotionMoves.clear();
        this.promotions.clear();
        this.promotions2.clear();
        this.directCheckMoves.clear();
        this.hiddenCheckMoves.clear();
        this.allCheckMoves.clear();
        this.capturePromotionCheckMoves.clear();
        this.kingEscapes.clear();
        this.hasMove.clear();
        this.hasMoveInNonCheck.clear();
        this.hasMoveInCheck.clear();
        this.countMoves.clear();
        this.countMovesInCheck.clear();
        this.countMovesInNonCheck.clear();
        this.countCheckMoves.clear();
        this.countCapturePromotionMovesInNonCheck.clear();
        this.countCapturePromotionCheckMovesInNonCheck.clear();
        this.isInCheck.clear();
        this.isCheckMove.clear();
        this.isDirectCheckMove.clear();
        this.checksCount.clear();
        this.isPossible.clear();
        this.stateRepetition.clear();
        this.fillCheckKeepers.clear();
        this.testing.clear();
    }

    public String toString() {
        Object result = "";
        for (Measure tmp : this.measures) {
            result = (String)result + String.valueOf(tmp) + "\r\n";
        }
        return result;
    }

    public static class Measure {
        public String name;
        public long calls;
        public long callOutput;
        public double callAVGTimeInNanos;
        private long startTime = 0L;

        public Measure(String _name) {
            this.name = _name;
        }

        public void start() {
            this.startTime = System.nanoTime();
        }

        public void stop(long _callOutput) {
            this.callOutput += _callOutput;
            long endTime = System.nanoTime();
            long timeInNanos = endTime - this.startTime;
            ++this.calls;
            this.callAVGTimeInNanos = this.callAVGTimeInNanos * ((double)(this.calls - 1L) / (double)this.calls) + (double)timeInNanos / (double)this.calls;
        }

        public void clear() {
            this.calls = 0L;
            this.callAVGTimeInNanos = 0.0;
            this.callOutput = 0L;
        }

        public String toString() {
            Object result = "";
            result = (String)result + this.name + ", " + this.calls + ", " + this.callAVGTimeInNanos + ", " + this.callOutput + ", " + (this.calls != 0L ? this.callOutput / this.calls : 0L);
            return result;
        }
    }
}

