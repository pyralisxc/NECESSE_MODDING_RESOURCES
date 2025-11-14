/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;

public class NetworkServerCommand
extends ModularChatCommand {
    public NetworkServerCommand() {
        super("network", "Shows network usage this session", PermissionLevel.MODERATOR, false, new CmdParameter[0]);
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        logs.add("Server received: " + server.packetManager.getAverageIn() + "/s (" + server.packetManager.getAverageInPackets() + "), Total: " + server.packetManager.getTotalIn() + " (" + server.packetManager.getTotalInPackets() + ")");
        logs.add("Server sent: " + server.packetManager.getAverageOut() + "/s (" + server.packetManager.getAverageOutPackets() + "), Total: " + server.packetManager.getTotalOut() + " (" + server.packetManager.getTotalOutPackets() + ")");
    }
}

