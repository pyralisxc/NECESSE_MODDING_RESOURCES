/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.LevelIdentifierParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;

public class SetLevelServerCommand
extends ModularChatCommand {
    public SetLevelServerCommand() {
        super("setlevel", "Changes the level of the player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("level", new LevelIdentifierParameterHandler(null)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        LevelIdentifier levelIdentifier = (LevelIdentifier)args[1];
        if (target.getLevelIdentifier().equals(levelIdentifier)) {
            logs.add(target.getName() + " is already at " + levelIdentifier);
            return;
        }
        target.changeLevel(levelIdentifier);
        logs.add("Set " + target.getName() + " level to  " + levelIdentifier);
    }
}

