/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.materialonly;

import bagaturchess.bitboard.api.IBitBoard;
import bagaturchess.search.api.IEvalConfig;
import bagaturchess.search.impl.eval.BaseEvaluator;
import bagaturchess.search.impl.eval.cache.IEvalCache;

public class MaterialEvaluator
extends BaseEvaluator {
    public MaterialEvaluator(IBitBoard _bitboard, IEvalCache _evalCache, IEvalConfig _evalConfig) {
        super(_bitboard, _evalCache, _evalConfig);
    }

    @Override
    protected int phase1() {
        return this.eval_material_nopawnsdrawrule() + this.interpolator.interpolateByFactor(this.baseEval.getPST_o(), this.baseEval.getPST_e());
    }

    @Override
    protected int phase2() {
        return 0;
    }

    @Override
    protected int phase3() {
        return 0;
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

