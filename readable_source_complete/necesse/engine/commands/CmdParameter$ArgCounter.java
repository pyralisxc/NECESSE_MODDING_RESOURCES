/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

public static class CmdParameter.ArgCounter {
    public final int params;
    public final int totalArgs;
    public int currentArg;
    public int currentParam;

    public CmdParameter.ArgCounter(int params, int totalArgs) {
        this.params = params;
        this.totalArgs = totalArgs;
        this.currentArg = 0;
    }
}
