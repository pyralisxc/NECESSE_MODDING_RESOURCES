/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

public static class BoolClientCommand.BoolCommandResult {
    public final boolean resultGiven;
    public final boolean result;

    private BoolClientCommand.BoolCommandResult(boolean resultGiven, boolean result) {
        this.resultGiven = resultGiven;
        this.result = result;
    }

    public boolean result(boolean current) {
        if (!this.resultGiven) {
            return !current;
        }
        return this.result;
    }
}
