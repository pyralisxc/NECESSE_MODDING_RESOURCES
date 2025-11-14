/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import necesse.engine.modLoader.LoadedMod;
import necesse.engine.modLoader.ModRuntimeException;

public class ModMethod {
    private final LoadedMod mod;
    private final Class<? extends Annotation> annotationClass;
    private final Object caller;
    private final Method method;

    public ModMethod(LoadedMod mod, Class<? extends Annotation> annotationClass, Object caller, Method method) {
        this.mod = mod;
        this.annotationClass = annotationClass;
        this.caller = caller;
        this.method = method;
    }

    public ModMethod(LoadedMod mod, Class<? extends Annotation> annotationClass, Object caller, Class<?> methodClass, String methodName, Class<?> ... methodParameters) {
        Method method;
        this.mod = mod;
        this.annotationClass = annotationClass;
        this.caller = caller;
        try {
            method = methodClass.getMethod(methodName, methodParameters);
        }
        catch (NoSuchMethodException e) {
            method = null;
        }
        this.method = method;
    }

    public boolean foundMethod() {
        return this.method != null;
    }

    public Class<?> getReturnType() {
        return this.method.getReturnType();
    }

    public Object invoke(Object ... args) {
        LoadedMod.runningMod = this.mod;
        try {
            if (this.method == null) {
                Object var2_2 = null;
                return var2_2;
            }
            Object object = this.method.invoke(this.caller, args);
            return object;
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            if (e.getCause() != null) {
                throw new ModRuntimeException(this.mod, "Error in running mod " + this.mod.id + " @" + this.getAnnotationClassName() + " " + this.method.getName() + ":", e.getCause());
            }
            throw new ModRuntimeException(this.mod, "Unknown error in running mod " + this.mod.id + " @" + this.getAnnotationClassName() + " " + this.method.getName(), e);
        }
        finally {
            LoadedMod.runningMod = null;
        }
    }

    private String getAnnotationClassName() {
        if (this.annotationClass == null) {
            return "NULL";
        }
        return this.annotationClass.getSimpleName();
    }
}

