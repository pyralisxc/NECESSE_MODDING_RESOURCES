/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.GameDamage;

public class StaticDamageServerCommand
extends ModularChatCommand {
    public StaticDamageServerCommand() {
        super("staticdamage", "Makes all damage not be randomized", PermissionLevel.OWNER, true, new CmdParameter("value", new BoolParameterHandler(true)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        GameDamage.staticDamage = (Boolean)args[0];
        logs.add("Static damage set to " + GameDamage.staticDamage);
    }
}

