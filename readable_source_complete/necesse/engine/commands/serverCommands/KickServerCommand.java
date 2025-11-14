/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.RestStringParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketDisconnect;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class KickServerCommand
extends ModularChatCommand {
    public KickServerCommand() {
        super("kick", "Kicks player from the server", PermissionLevel.MODERATOR, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("message/reason", new RestStringParameterHandler(), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        String reason = (String)args[1];
        if (reason == null) {
            reason = "No message";
        }
        server.disconnectClient(target, PacketDisconnect.kickPacket(target.slot, reason));
    }
}

