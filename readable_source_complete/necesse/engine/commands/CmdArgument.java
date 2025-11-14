/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import necesse.engine.commands.CmdParameter;

public class CmdArgument {
    public final CmdParameter param;
    public final String arg;
    public final int argCount;

    public CmdArgument(CmdParameter param, String arg, int argCount) {
        this.param = param;
        this.arg = arg;
        this.argCount = argCount;
    }
}

