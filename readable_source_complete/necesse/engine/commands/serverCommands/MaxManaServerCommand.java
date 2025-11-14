/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class MaxManaServerCommand
extends ModularChatCommand {
    public MaxManaServerCommand() {
        super("maxmana", "Sets the max mana of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("mana", new IntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient player = (ServerClient)args[0];
        int amount = (Integer)args[1];
        if (player == null) {
            logs.add("Must specify <player>");
            return;
        }
        player.playerMob.setMaxMana(amount);
        server.network.sendToAllClients(new PacketPlayerGeneral(player));
        logs.add("Set " + player.getName() + " max mana to " + player.playerMob.getMaxManaFlat());
    }
}

