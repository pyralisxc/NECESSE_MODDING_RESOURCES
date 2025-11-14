/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.parameterHandlers;

import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.CmdArgument;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.parameterHandlers.ParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class RelativeIntParameterHandler
extends ParameterHandler<RelativeInt> {
    private Integer defaultValue;

    public static int handleRelativeInt(Object object, int relative) {
        if (object instanceof RelativeInt) {
            RelativeInt ri = (RelativeInt)object;
            if (ri.relativeFunction != null) {
                return ri.relativeFunction.apply(relative, ri.value);
            }
            return ri.value;
        }
        throw new IllegalArgumentException("Object is not a relative int");
    }

    public RelativeIntParameterHandler() {
        this.defaultValue = 0;
    }

    public RelativeIntParameterHandler(Integer defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public List<AutoComplete> autocomplete(Client client, Server server, ServerClient serverClient, CmdArgument argument) {
        return Collections.emptyList();
    }

    @Override
    public RelativeInt parse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) throws IllegalArgumentException {
        try {
            if (arg.startsWith("%")) {
                arg = arg.substring(1);
                BiFunction<Integer, Integer, Integer> relativeFunction = Integer::sum;
                for (Operator operator : Operator.values()) {
                    if (!arg.startsWith(operator.prefix)) continue;
                    relativeFunction = operator.calculate;
                    arg = arg.substring(1);
                    break;
                }
                if (arg.isEmpty()) {
                    return new RelativeInt(relativeFunction, 0);
                }
                return new RelativeInt(relativeFunction, Integer.parseInt(arg));
            }
            return new RelativeInt(null, Integer.parseInt(arg));
        }
        catch (NumberFormatException e) {
            throw new IllegalArgumentException((arg.isEmpty() ? "Argument" : arg) + " for <" + parameter.name + "> is not a number");
        }
    }

    @Override
    public boolean tryParse(Client client, Server server, ServerClient serverClient, String arg, CmdParameter parameter) {
        if (arg.isEmpty()) {
            return true;
        }
        try {
            if (arg.startsWith("%")) {
                arg = arg.substring(1);
                for (Operator operator : Operator.values()) {
                    if (!arg.startsWith(operator.prefix)) continue;
                    arg = arg.substring(1);
                    break;
                }
            }
            if (arg.isEmpty()) {
                return true;
            }
            Integer.parseInt(arg);
            return true;
        }
        catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public RelativeInt getDefault(Client client, Server server, ServerClient serverClient, CmdParameter parameter) {
        return new RelativeInt(null, this.defaultValue);
    }

    public static class RelativeInt {
        public final BiFunction<Integer, Integer, Integer> relativeFunction;
        public final int value;

        public RelativeInt(BiFunction<Integer, Integer, Integer> relativeFunction, int value) {
            this.relativeFunction = relativeFunction;
            this.value = value;
        }
    }

    private static enum Operator {
        PLUS("+", Integer::sum),
        MINUS("-", (i1, i2) -> i1 - i2),
        MULTIPLY("*", (i1, i2) -> i1 * i2),
        DIVIDE("/", (i1, i2) -> i1 / i2),
        MODULO("%", (i1, i2) -> i1 % i2),
        POWER("^", (i1, i2) -> (int)Math.pow(i1.intValue(), i2.intValue()));

        public String prefix;
        public BiFunction<Integer, Integer, Integer> calculate;

        private Operator(String prefix, BiFunction<Integer, Integer, Integer> calculate) {
            this.prefix = prefix;
            this.calculate = calculate;
        }
    }
}

