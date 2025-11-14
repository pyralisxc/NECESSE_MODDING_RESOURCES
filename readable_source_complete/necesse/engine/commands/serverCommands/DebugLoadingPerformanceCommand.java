/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class DebugLoadingPerformanceCommand
extends ModularChatCommand {
    public DebugLoadingPerformanceCommand(String name) {
        super(name, "Record and print the debug loading performance timer over a period on current level", PermissionLevel.USER, false, new CmdParameter("intervalSeconds", new IntParameterHandler(10)), new CmdParameter("maxPrints", new IntParameterHandler(0), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        Level level;
        int seconds = (Integer)args[0];
        int maxPrints = (Integer)args[1];
        if (client != null) {
            level = client.getLevel();
        } else if (serverClient != null) {
            level = serverClient.getLevel();
        } else {
            logs.add("Cannot run this command from the server");
            return;
        }
        if (seconds == 0) {
            logs.add("Stopping debug loading performance timer");
            level.endDebugLoadingTimer(true);
        } else {
            logs.add("Starting debug loading performance with print interval of " + seconds + " seconds" + (maxPrints > 0 ? " and a maximum of " + maxPrints + " prints" : ""));
            level.startDebugLoadingTimer(seconds * 1000, maxPrints);
        }
    }
}

