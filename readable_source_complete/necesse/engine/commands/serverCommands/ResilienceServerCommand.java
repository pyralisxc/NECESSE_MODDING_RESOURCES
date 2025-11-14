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
import necesse.engine.network.packet.PacketMobResilience;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ResilienceServerCommand
extends ModularChatCommand {
    public ResilienceServerCommand(String name) {
        super(name, "Sets the resilience of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("resilience", new IntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        int amount = (Integer)args[1];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        target.playerMob.setResilienceHidden(amount);
        server.network.sendToClientsWithEntity(new PacketMobResilience(target.playerMob, true), target.playerMob);
        logs.add("Set " + target.getName() + " resilience to " + target.playerMob.getResilience());
    }
}

