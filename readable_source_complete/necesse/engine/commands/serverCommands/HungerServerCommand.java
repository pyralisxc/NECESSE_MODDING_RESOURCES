/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.FloatParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class HungerServerCommand
extends ModularChatCommand {
    public HungerServerCommand() {
        super("hunger", "Sets the hunger percent of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("hunger", new FloatParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        float amount = ((Float)args[1]).floatValue();
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        player.playerMob.hungerLevel = amount / 100.0f;
        player.playerMob.sendHungerPacket();
        logs.add("Set " + player.getName() + " hunger level to " + GameUtils.formatNumber(amount) + "%");
    }
}

