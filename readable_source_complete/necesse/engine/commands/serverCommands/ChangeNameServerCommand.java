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
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerAppearance;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameUtils;

public class ChangeNameServerCommand
extends ModularChatCommand {
    public ChangeNameServerCommand() {
        super("changename", "Changes the name of a player", PermissionLevel.ADMIN, false, new CmdParameter("player", new ServerClientParameterHandler()), new CmdParameter("name", new RestStringParameterHandler()));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ServerClient target = (ServerClient)args[0];
        String name = ((String)args[1]).trim();
        GameMessage validName = GameUtils.isValidPlayerName(name);
        if (validName != null) {
            logs.add(name + " is an invalid name");
        } else {
            for (String str : server.usedNames.values()) {
                if (!str.equalsIgnoreCase(name)) continue;
                logs.add(name + " is already in use");
                return;
            }
            String lastName = target.getName();
            server.usedNames.put(target.authentication, name);
            target.playerMob.playerName = name;
            server.network.sendToAllClients(new PacketPlayerAppearance(target));
            logs.add("Changed " + lastName + "'s name to " + target.getName());
            if (serverClient != target) {
                target.sendChatMessage("Changed your name to " + target.getName());
            }
        }
    }
}

