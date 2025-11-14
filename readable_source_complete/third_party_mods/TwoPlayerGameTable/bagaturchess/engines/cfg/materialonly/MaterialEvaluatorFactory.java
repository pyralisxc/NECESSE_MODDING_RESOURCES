/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.materialonly;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.engines.cfg.materialonly.MaterialEvaluator;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.api.IEvaluator;
import bagaturchess.search.api.IEvaluatorFactory;
import bagaturchess.search.impl.eval.cache.IEvalCache;

public class MaterialEvaluatorFactory
implements IEvaluatorFactory {
    @Override
    public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache) {
        return new MaterialEvaluator(bitboard, evalCache, null);
    }

    @Override
    public IEvaluator create(IBitBoard bitboard, IEvalCache evalCache, IEvalConfig evalConfig) {
        return new MaterialEvaluator(bitboard, evalCache, evalConfig);
    }
}

