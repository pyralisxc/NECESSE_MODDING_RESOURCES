/*
 * Decompiled with CFR 0.152.
 */
package bagaturchess.engines.cfg.base;

import bagaturchess.engines.cfg.base.RootSearchConfig_BaseImpl_SMP;

public class RootSearchConfig_BaseImpl_SMP_Threads
extends RootSearchConfig_BaseImpl_SMP {
    public RootSearchConfig_BaseImpl_SMP_Threads(String[] args) {
        super(args);
    }

    @Override
    public boolean initCaches() {
        return true;
    }
}

