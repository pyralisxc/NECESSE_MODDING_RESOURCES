/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.modLoader.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value=RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface ModCustomPatch {
    public Class<?> target();

    public int priority() default 0;
}

