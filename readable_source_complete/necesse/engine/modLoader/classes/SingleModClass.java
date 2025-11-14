/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader.classes;

import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.classes.ModClass;

public abstract class SingleModClass
extends ModClass {
    private final boolean requiredClass;
    private Class modClass;

    public SingleModClass(boolean requiredClass) {
        this.requiredClass = requiredClass;
    }

    public Class getModClass() {
        return this.modClass;
    }

    @Override
    public final void registerModClass(LoadedMod mod, Class<?> modClass) throws ModLoadException {
        if (this.modClass != null) {
            throw new ModLoadException(mod, this.getErrorName() + " cannot be used multiple times.");
        }
        this.modClass = modClass;
    }

    @Override
    public final void finalizeLoading(LoadedMod mod) throws ModLoadException {
        if (this.requiredClass && this.modClass == null) {
            throw new ModLoadException(mod, "Did not contain a " + this.getErrorName());
        }
        this.finalizeSingleLoading(mod);
    }

    public abstract String getErrorName();

    public abstract void finalizeSingleLoading(LoadedMod var1) throws ModLoadException;
}

