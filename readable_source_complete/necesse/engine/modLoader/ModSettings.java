/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;

public abstract class ModSettings {
    public abstract void addSaveData(SaveData var1);

    public abstract void applyLoadData(LoadData var1);
}

