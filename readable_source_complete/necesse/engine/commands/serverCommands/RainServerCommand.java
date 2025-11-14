/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.Level;

public class RainServerCommand
extends ModularChatCommand {
    public RainServerCommand() {
        super("rain", "Sets the rain on the level", PermissionLevel.ADMIN, true, new CmdParameter("islandX", new IntParameterHandler(Integer.MIN_VALUE), true, new CmdParameter("islandY", new IntParameterHandler()), new CmdParameter("dimension", new IntParameterHandler())), new CmdParameter("start/clear", new PresetStringParameterHandler("start", "clear")));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        LevelIdentifier levelIdentifier = new LevelIdentifier((Integer)args[0], (Integer)args[1], (Integer)args[2]);
        String action = (String)args[3];
        boolean active = action.equals("start");
        if (levelIdentifier.getIslandX() == Integer.MIN_VALUE) {
            if (serverClient == null) {
                logs.add("Please specify island coordinates and dimension");
                return;
            }
            levelIdentifier = serverClient.getLevelIdentifier();
        }
        if (server.world.levelManager.isLoaded(levelIdentifier)) {
            Level level = server.world.getLevel(levelIdentifier);
            if (active && !level.canRain()) {
                logs.add("Level does not allow rain");
            } else {
                level.weatherLayer.setRaining(active);
                level.weatherLayer.resetRainTimer();
                if (level.weatherLayer.isRaining()) {
                    logs.add("Started rain session");
                } else {
                    logs.add("Cleared rain session");
                }
            }
        } else {
            logs.add("Specified level is not loaded");
        }
    }
}

