/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import necesse.engine.modLoader.LoadedMod;

public class ModNextListData {
    public final LoadedMod mod;
    public final boolean enabled;

    public ModNextListData(LoadedMod mod, boolean enabled) {
        this.mod = mod;
        this.enabled = enabled;
    }
}

