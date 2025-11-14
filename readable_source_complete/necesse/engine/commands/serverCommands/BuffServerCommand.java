/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BuffParameterHandler;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.staticBuffs.Buff;

public class BuffServerCommand
extends ModularChatCommand {
    public BuffServerCommand() {
        super("buff", "Gives buff to player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("buff", new BuffParameterHandler(false)), new CmdParameter("seconds", new FloatParameterHandler(Float.valueOf(1.0f)), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        Buff buff = (Buff)args[1];
        float seconds = ((Float)args[2]).floatValue();
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        player.playerMob.buffManager.addBuff(new ActiveBuff(buff, (Mob)player.playerMob, seconds, null), true);
        logs.add("Gave " + seconds + " seconds of " + buff.getDisplayName() + " to " + player.getName());
    }
}

