/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader.classes;

import java.util.LinkedList;
import java.util.List;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModLoadException;
import necesse.engine.modLoader.classes.ModClass;

public abstract class MultiModClass
extends ModClass {
    private final boolean requiredClass;
    private List<Class<?>> modClasses = new LinkedList();

    public MultiModClass(boolean requiredClass) {
        this.requiredClass = requiredClass;
    }

    public Iterable<Class<?>> getModClasses() {
        return this.modClasses;
    }

    @Override
    public final void registerModClass(LoadedMod mod, Class<?> modClass) {
        this.modClasses.add(modClass);
    }

    @Override
    public final void finalizeLoading(LoadedMod mod) throws ModLoadException {
        if (this.requiredClass && this.modClasses.size() == 0) {
            throw new ModLoadException(mod, mod.id + " did not contain a " + this.getErrorName());
        }
        this.finalizeMultiLoading(mod);
    }

    public abstract String getErrorName();

    public abstract void finalizeMultiLoading(LoadedMod var1) throws ModLoadException;
}

