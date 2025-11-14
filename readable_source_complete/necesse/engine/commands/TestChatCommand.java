/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands;

import java.util.Objects;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class TestChatCommand
extends ModularChatCommand {
    public TestChatCommand() {
        super("test", "Tests the command", PermissionLevel.USER, true, new CmdParameter("x", new FloatParameterHandler(), new CmdParameter("y", new FloatParameterHandler())), new CmdParameter("text", new StringParameterHandler("def", new String[0])), new CmdParameter("clear/set/random", new PresetStringParameterHandler(true, "clear", "set", "random", "actual"), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        logs.addConsole("Successful test run with " + args.length + " arguments:");
        for (int i = 0; i < args.length; ++i) {
            logs.addConsole("Arg " + i + " (" + (args[i] == null ? "null" : args[i].getClass().getSimpleName()) + "): " + Objects.toString(args[i]));
        }
    }
}

