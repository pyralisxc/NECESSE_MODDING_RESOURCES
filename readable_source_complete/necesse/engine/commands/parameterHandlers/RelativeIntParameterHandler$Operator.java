/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.function.BiFunction;

private static enum RelativeIntParameterHandler.Operator {
    PLUS("+", Integer::sum),
    MINUS("-", (i1, i2) -> i1 - i2),
    MULTIPLY("*", (i1, i2) -> i1 * i2),
    DIVIDE("/", (i1, i2) -> i1 / i2),
    MODULO("%", (i1, i2) -> i1 % i2),
    POWER("^", (i1, i2) -> (int)Math.pow(i1.intValue(), i2.intValue()));

    public String prefix;
    public BiFunction<Integer, Integer, Integer> calculate;

    private RelativeIntParameterHandler.Operator(String prefix, BiFunction<Integer, Integer, Integer> calculate) {
        this.prefix = prefix;
        this.calculate = calculate;
    }
}
