/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.LevelIdentifierParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementVisitorOdds;
import necesse.level.maps.levelData.settlementData.SettlementVisitorSpawner;

public class SpawnVisitorServerCommand
extends ModularChatCommand {
    public SpawnVisitorServerCommand() {
        super("spawnvisitor", "Spawns a settlement visitor on the level", PermissionLevel.ADMIN, true, new CmdParameter("tileX", new IntParameterHandler(Integer.MIN_VALUE), true, new CmdParameter("tileY", new IntParameterHandler(Integer.MIN_VALUE)), new CmdParameter("level", new LevelIdentifierParameterHandler(null))), new CmdParameter("visitor", new PresetStringParameterHandler((String[])ServerSettlementData.visitorOdds.stream().map(v -> v.identifier).toArray(String[]::new)), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int tileX = (Integer)args[0];
        int tileY = (Integer)args[1];
        LevelIdentifier levelIdentifier = (LevelIdentifier)args[2];
        String visitorIdentifier = (String)args[3];
        if (levelIdentifier == null) {
            if (serverClient == null) {
                logs.add("Please specify level and tile coordinates");
                return;
            }
            levelIdentifier = serverClient.getLevelIdentifier();
        }
        if (tileX == Integer.MIN_VALUE || tileY == Integer.MIN_VALUE) {
            if (serverClient == null) {
                logs.add("Please specify level and tile coordinates");
                return;
            }
            tileX = serverClient.playerMob.getTileX();
            tileY = serverClient.playerMob.getTileY();
        }
        if (server.world.levelManager.isLoaded(levelIdentifier)) {
            Level level = server.world.getLevel(levelIdentifier);
            ServerSettlementData data = SettlementsWorldData.getSettlementsData(level).getServerDataAtTile(level.getIdentifier(), tileX, tileY);
            if (data != null) {
                if (visitorIdentifier != null) {
                    SettlementVisitorOdds visitor = ServerSettlementData.visitorOdds.stream().filter(v -> v.identifier.equals(visitorIdentifier)).findFirst().orElse(null);
                    if (visitor != null) {
                        SettlementVisitorSpawner spawner = visitor.getNewVisitorSpawner(data);
                        if (spawner != null) {
                            if (data.spawnVisitor(spawner)) {
                                logs.add("Spawned visitor: " + visitorIdentifier);
                            } else {
                                logs.add("Could not spawn visitor \"" + visitorIdentifier + "\"");
                            }
                        }
                    } else {
                        logs.add("Could not find visitor \"" + visitorIdentifier + "\"");
                    }
                } else if (data.spawnNextVisitor()) {
                    logs.add("Spawned next visitor");
                } else {
                    logs.add("Could not spawn next visitor");
                }
            } else {
                logs.add("Could not find settlement at " + levelIdentifier + " tile " + tileX + "x" + tileY);
            }
        } else {
            logs.add("Specified level is not loaded");
        }
    }
}

