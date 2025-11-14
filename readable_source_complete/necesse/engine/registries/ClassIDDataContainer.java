/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.registries;

import java.lang.reflect.InvocationTargetException;
import necesse.engine.registries.ClassIDData;
import necesse.engine.registries.IDDataContainer;

public class ClassIDDataContainer<C>
implements IDDataContainer {
    protected final ClassIDData<? extends C> data;

    public ClassIDDataContainer(Class<? extends C> aClass, Class<?> ... constructorParameters) throws NoSuchMethodException {
        this.data = new ClassIDData<C>(aClass, constructorParameters);
    }

    @Override
    public ClassIDData<? extends C> getIDData() {
        return this.data;
    }

    public C newInstance(Object ... args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.data.newInstance(args);
    }
}

