/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl;
import bagaturchess.engines.cfg.base.SearchConfigImpl_AB;
import bagaturchess.engines.cfg.base.SearchConfigImpl_AB_NNTraining;
import bagaturchess.learning.goldmiddle.impl4.cfg.BoardConfigImpl_V20;
import bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20;
import bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20_GOLDENMIDDLE_Train;
import bagaturchess.learning.goldmiddle.impl4.cfg.EvaluationConfig_V20_NNTraining;
import bagaturchess.search.api.IRootSearchConfig;
import bagaturchess.search.api.IRootSearchConfig_Single;
import bagaturchess.search.impl.alg.impl1.Search_PVS_NWS;
import bagaturchess.uci.api.IUCIOptionsProvider;

public class RootSearchConfig_BaseImpl_1Core
extends RootSearchConfig_BaseImpl
implements IRootSearchConfig_Single,
IUCIOptionsProvider {
    public static final IRootSearchConfig EVALIMPL4 = new RootSearchConfig_BaseImpl_1Core(new String[]{Search_PVS_NWS.class.getName(), SearchConfigImpl_AB.class.getName(), BoardConfigImpl_V20.class.getName(), EvaluationConfig_V20.class.getName()});
    public static final IRootSearchConfig EVALIMPL4_NNUE = new RootSearchConfig_BaseImpl_1Core(new String[]{Search_PVS_NWS.class.getName(), SearchConfigImpl_AB_NNTraining.class.getName(), BoardConfigImpl_V20.class.getName(), EvaluationConfig_V20_NNTraining.class.getName()});
    public static final IRootSearchConfig EVALIMPL4_TUNING_GOLDENMIDDEL = new RootSearchConfig_BaseImpl_1Core(new String[]{Search_PVS_NWS.class.getName(), SearchConfigImpl_AB_NNTraining.class.getName(), BoardConfigImpl_V20.class.getName(), EvaluationConfig_V20_GOLDENMIDDLE_Train.class.getName()});

    public RootSearchConfig_BaseImpl_1Core(String[] args) {
        super(args);
    }

    @Override
    public int getTPTsCount() {
        return 1;
    }
}

