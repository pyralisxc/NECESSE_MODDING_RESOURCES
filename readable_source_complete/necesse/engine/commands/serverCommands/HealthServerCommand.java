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
import necesse.engine.network.packet.PacketMobHealth;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class HealthServerCommand
extends ModularChatCommand {
    public HealthServerCommand(String name) {
        super(name, "Sets the health of player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("health", new IntParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        int amount = (Integer)args[1];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        target.playerMob.setHealthHidden(amount, 0.0f, 0.0f, null, true);
        server.network.sendToClientsWithEntity(new PacketMobHealth(target.playerMob, true), target.playerMob);
        logs.add("Set " + target.getName() + " health to " + target.playerMob.getHealth());
    }
}

