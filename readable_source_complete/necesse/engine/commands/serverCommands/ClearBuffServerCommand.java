/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BuffParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class ClearBuffServerCommand
extends ModularChatCommand {
    public ClearBuffServerCommand() {
        super("clearbuff", "Clears buff from player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("buff", new BuffParameterHandler(false)));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        Buff buff = (Buff)args[1];
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        if (player.playerMob.buffManager.hasBuff(buff.getID())) {
            player.playerMob.buffManager.removeBuff(buff.getID(), true);
            logs.add("Cleared " + buff.getDisplayName() + " from " + player.getName());
        } else {
            logs.add(player.getName() + " does not have " + buff.getDisplayName());
        }
    }
}

