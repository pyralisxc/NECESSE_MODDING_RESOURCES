/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.commands.CommandLog;
import necesse.engine.commands.clientCommands.BoolClientCommand;
import necesse.engine.network.client.Client;

@FunctionalInterface
public static interface BoolClientCommand.BoolCommandLogic {
    public void apply(Client var1, CommandLog var2, BoolClientCommand.BoolCommandResult var3);
}
