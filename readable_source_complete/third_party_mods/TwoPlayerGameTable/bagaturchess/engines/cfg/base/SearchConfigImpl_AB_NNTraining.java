/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.engines.cfg.base.SearchConfigImpl_AB;

public class SearchConfigImpl_AB_NNTraining
extends SearchConfigImpl_AB {
    @Override
    public boolean isOther_UseTPTScores() {
        return false;
    }

    @Override
    public boolean isOther_UseAlphaOptimizationInQSearch() {
        return false;
    }
}

