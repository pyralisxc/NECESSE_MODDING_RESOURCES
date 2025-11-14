/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.function.BiFunction;

public static class RelativeIntParameterHandler.RelativeInt {
    public final BiFunction<Integer, Integer, Integer> relativeFunction;
    public final int value;

    public RelativeIntParameterHandler.RelativeInt(BiFunction<Integer, Integer, Integer> relativeFunction, int value) {
        this.relativeFunction = relativeFunction;
        this.value = value;
    }
}
