/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.materialonly;

import bagaturchess.engines.cfg.materialonly.MaterialEvaluatorFactory;
import bagaturchess.learning.goldmiddle.impl.cfg.bagatur.eval.BagaturPawnsEvalFactory;
import bagaturchess.search.api.IEvalConfig;

public class MaterialEvalConfigImpl
implements IEvalConfig {
    @Override
    public boolean useEvalCache() {
        return true;
    }

    @Override
    public boolean useLazyEval() {
        return true;
    }

    @Override
    public boolean isTrainingMode() {
        return false;
    }

    @Override
    public String getEvaluatorFactoryClassName() {
        return MaterialEvaluatorFactory.class.getName();
    }

    @Override
    public String getPawnsCacheFactoryClassName() {
        return BagaturPawnsEvalFactory.class.getName();
    }
}

