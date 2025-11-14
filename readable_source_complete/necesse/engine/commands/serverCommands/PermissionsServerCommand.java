/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.PermissionLevelParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class PermissionsServerCommand
extends ModularChatCommand {
    public PermissionsServerCommand() {
        super("permissions", "Sets a players permissions", PermissionLevel.OWNER, false, new CmdParameter("list/set/get", new PresetStringParameterHandler("list", "set", "get")), new CmdParameter("authentication/name", new ServerClientParameterHandler(false, true), true, new CmdParameter("permissions", new PermissionLevelParameterHandler(), true, new CmdParameter[0])));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        String action;
        switch (action = (String)args[0]) {
            case "list": {
                logs.add("Permission levels:");
                StringBuilder out = new StringBuilder();
                PermissionLevel[] levels = PermissionLevel.values();
                for (int i = 0; i < levels.length; ++i) {
                    out.append(levels[i].name.translate());
                    if (i >= levels.length - 1) continue;
                    out.append(", ");
                }
                logs.add(out.toString());
                break;
            }
            case "set": {
                ServerClient target = (ServerClient)args[1];
                PermissionLevel targetLevel = (PermissionLevel)((Object)args[2]);
                if (target == null) {
                    logs.add("Missing authentication/name");
                    return;
                }
                if (targetLevel == null) {
                    logs.add("Missing permissions");
                    return;
                }
                target.setPermissionLevel(targetLevel, true);
                logs.add("Changed " + target.getName() + " permissions to " + targetLevel.name.translate());
                break;
            }
            case "get": {
                ServerClient target = (ServerClient)args[1];
                if (target == null) {
                    logs.add("Missing authentication/name");
                    return;
                }
                logs.add(target.getName() + " has " + target.getPermissionLevel().name.translate() + " permissions");
                break;
            }
        }
    }
}

