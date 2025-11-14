/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.clientCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.ClientClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class DebugClientClientCommand
extends ModularChatCommand {
    public DebugClientClientCommand() {
        super("debugclient", "Prints debug information about a client", PermissionLevel.USER, false, new CmdParameter("player", new ClientClientParameterHandler(), false, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        ClientClient target = (ClientClient)args[0];
        ClientClient me = client.getClient();
        Level level = client.getLevel();
        logs.add("hasSpawned: " + target.hasSpawned());
        logs.add("isVisible: " + target.playerMob.isVisible());
        logs.add("isSamePlace: " + target.isSamePlace(level));
        logs.add("isDead: " + target.isDead());
        logs.add("isRemoved: " + target.playerMob.removed());
        logs.add("isDisposed: " + target.playerMob.isDisposed());
    }

    @Override
    public boolean shouldBeListed() {
        return false;
    }
}

