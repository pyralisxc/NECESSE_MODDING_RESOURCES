/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.evaladapters.carballo.eval;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.evaladapters.carballo.eval.BagaturEvaluator_Phases;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;

public class BagaturEvaluatorFactory
implements IEvaluatorFactory {
    @Override
    public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
        return new BagaturEvaluator_Phases(bitboard, evalCache, null);
    }

    @Override
    public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
        return new BagaturEvaluator_Phases(bitboard, evalCache, evalConfig);
    }
}

