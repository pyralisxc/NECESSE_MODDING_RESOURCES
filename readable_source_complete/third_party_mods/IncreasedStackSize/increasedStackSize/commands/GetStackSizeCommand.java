/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.commands.CmdParameter
 *  necesse.engine.commands.CommandLog
 *  necesse.engine.commands.ModularChatCommand
 *  necesse.engine.commands.PermissionLevel
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.client.Client
 *  necesse.engine.network.server.Server
 *  necesse.engine.network.server.ServerClient
 */
package increasedStackSize.commands;

import increasedStackSize.IncreasedStackSize;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.localization.Localization;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class GetStackSizeCommand
extends ModularChatCommand {
    public GetStackSizeCommand() {
        super("getstacksizemultiplier", "Get stack size multiplier", PermissionLevel.USER, false, new CmdParameter[0]);
    }

    public void runModular(Client client, Server server, ServerClient serverClient, Object[] objects, String[] strings, CommandLog commandLog) {
        commandLog.add(Localization.translate((String)"increasedstacksize", (String)"newcommand"));
        commandLog.add(Localization.translate((String)"increasedstacksize", (String)"getstacksize", (String)"stacksize", (Object)IncreasedStackSize.stackSizeMultiplier));
    }
}

