/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class ResetHealthUpgradesServerCommand
extends ModularChatCommand {
    public ResetHealthUpgradesServerCommand() {
        super("resethealthupgrades", "Resets the health upgrades of a player", PermissionLevel.OWNER, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        if (target == null) {
            logs.add("Must specify <player>");
            return;
        }
        target.playerMob.healthUpgradeManager.resetHealthUpgrades();
        server.network.sendToAllClientsExcept(new PacketPlayerGeneral(target), target);
        logs.add("Successfully reset health upgrades for " + target.getName());
    }
}

