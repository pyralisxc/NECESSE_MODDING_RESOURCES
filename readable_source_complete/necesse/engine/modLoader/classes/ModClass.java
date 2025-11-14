/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader.classes;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;

public abstract class ModClass {
    public abstract boolean shouldRegisterModClass(Class<?> var1);

    public abstract void registerModClass(LoadedMod var1, Class<?> var2) throws ModLoadException;

    public abstract void finalizeLoading(LoadedMod var1) throws ModLoadException;
}

