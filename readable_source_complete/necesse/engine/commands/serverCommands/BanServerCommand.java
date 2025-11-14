/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.GameAuth;
import necesse.engine.Settings;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.MultiParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class BanServerCommand
extends ModularChatCommand {
    public BanServerCommand() {
        super("ban", "Bans a player", PermissionLevel.ADMIN, false, new CmdParameter("authentication/name", new MultiParameterHandler(new ServerClientParameterHandler(false, true), new StringParameterHandler())));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Object[] multi = (Object[])args[0];
        ServerClient target = (ServerClient)multi[0];
        String authTarget = (String)multi[1];
        if (target != null) {
            if (target.authentication == GameAuth.getAuthentication()) {
                logs.add("Cannot ban yourself");
                return;
            }
            Settings.addBanned(target.getName());
            logs.add("Banned " + target.getName() + ".");
            server.disconnectClient(target, PacketDisconnect.kickPacket(target.slot, "You have been banned."));
            return;
        }
        if (String.valueOf(GameAuth.getAuthentication()).equals(authTarget)) {
            logs.add("Cannot ban your own authentication");
            return;
        }
        if (!Settings.isBanned(authTarget)) {
            Settings.addBanned(authTarget);
            logs.add("Banned " + authTarget + ".");
        } else {
            logs.add(authTarget + " is already banned.");
        }
    }
}

