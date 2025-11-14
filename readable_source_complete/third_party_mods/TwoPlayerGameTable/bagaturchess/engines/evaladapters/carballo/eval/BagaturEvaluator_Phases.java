/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo.eval;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.evaladapters.carballo.AttacksInfo;
import bagaturchess.engines.evaladapters.carballo.CompleteEvaluator;
import bagaturchess.engines.evaladapters.carballo.IBoard;
import bagaturchess.engines.evaladapters.carballo.eval.BoardImpl;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;

public class BagaturEvaluator_Phases
extends BaseEvaluator {
    private IBoard board;
    private CompleteEvaluator evaluator;
    private AttacksInfo ai;

    public BagaturEvaluator_Phases(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
        super(_bitboard, _evalCache, _evalConfig);
        this.bitboard = _bitboard;
        this.board = new BoardImpl(this.bitboard);
        this.evaluator = new CompleteEvaluator();
        this.ai = new AttacksInfo();
    }

    public int getMaterialQueen() {
        return 1244;
    }

    @Override
    protected int phase1() {
        int eval = this.evaluator.evaluate1(this.board, this.ai);
        return eval;
    }

    @Override
    protected int phase2() {
        int eval = this.evaluator.evaluate2(this.board, this.ai);
        return eval;
    }

    @Override
    protected int phase3() {
        int eval = 0;
        return eval;
    }

    @Override
    protected int phase4() {
        return 0;
    }

    @Override
    protected int phase5() {
        return 0;
    }
}

