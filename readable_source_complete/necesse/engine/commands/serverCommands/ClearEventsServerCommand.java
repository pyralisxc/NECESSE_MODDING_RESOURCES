/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.StringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.level.maps.Level;

public class ClearEventsServerCommand
extends ModularChatCommand {
    public ClearEventsServerCommand() {
        super("clearevents", "Clears all events on your level or on all loaded levels", PermissionLevel.ADMIN, true, new CmdParameter("global", new BoolParameterHandler(), true, new CmdParameter("type", new StringParameterHandler(), true, new CmdParameter[0])));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int amount = 0;
        int levels = 0;
        boolean global = (Boolean)args[0];
        String type = (String)args[1];
        if (type != null) {
            if (global || serverClient == null) {
                for (Level level : server.world.levelManager.getLoadedLevels()) {
                    ++levels;
                    amount += level.entityManager.events.clearLevelEvents(type);
                }
            } else {
                ++levels;
                amount += server.world.getLevel((ServerClient)serverClient).entityManager.events.clearLevelEvents(type);
            }
        } else if (global || serverClient == null) {
            for (Level level : server.world.levelManager.getLoadedLevels()) {
                ++levels;
                amount += level.entityManager.events.clearLevelEvents();
            }
        } else {
            ++levels;
            amount += server.world.getLevel((ServerClient)serverClient).entityManager.events.clearLevelEvents();
        }
        logs.add("Cleared " + amount + " events on " + levels + " levels.");
    }
}

