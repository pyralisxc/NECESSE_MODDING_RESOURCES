/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.commands.serverCommands;

import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.IntParameterHandler;
import necesse.engine.commands.parameterHandlers.LevelIdentifierParameterHandler;
import necesse.engine.commands.parameterHandlers.PresetStringParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementVisitorSpawner;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class SpawnSettlerServerCommand
extends ModularChatCommand {
    public SpawnSettlerServerCommand() {
        super("spawnsettler", "Spawns a settler on the level", PermissionLevel.ADMIN, true, new CmdParameter("tileX", new IntParameterHandler(Integer.MIN_VALUE), true, new CmdParameter("tileY", new IntParameterHandler(Integer.MIN_VALUE)), new CmdParameter("level", new LevelIdentifierParameterHandler(null))), new CmdParameter("settler", new PresetStringParameterHandler((String[])SettlerRegistry.getSettlers().stream().map(Settler::getStringID).toArray(String[]::new))), new CmdParameter("recruited", new BoolParameterHandler(true), true, new CmdParameter[0]));
    }

    @Override
    public void runModular(Client client, Server server, ServerClient serverClient, Object[] args, String[] errors, CommandLog logs) {
        int tileX = (Integer)args[0];
        int tileY = (Integer)args[1];
        LevelIdentifier levelIdentifier = (LevelIdentifier)args[2];
        String settlerStringID = (String)args[3];
        boolean recruited = (Boolean)args[4];
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
                Settler settler = SettlerRegistry.getSettler(settlerStringID);
                if (settler != null) {
                    SettlerMob mob = settler.getNewSettlerMob(data);
                    mob.setSettlerSeed(GameRandom.globalRandom.nextInt(), true);
                    if (recruited) {
                        level.entityManager.mobs.add(mob.getMob());
                        data.moveIn(new LevelSettler(data, mob));
                        logs.add("Spawned recruited " + ((Mob)((Object)mob)).getDisplayName() + " at " + levelIdentifier);
                    } else {
                        HumanMob humanMob = (HumanMob)mob.getMob();
                        data.spawnVisitor(new SettlementVisitorSpawner(ServerSettlementData.visitorRecruitsOdds, humanMob));
                        logs.add("Spawned " + ((Mob)((Object)mob)).getDisplayName() + " at " + levelIdentifier);
                    }
                } else {
                    logs.add("Could not find settler with stringID \"" + settlerStringID + "\"");
                }
            } else {
                logs.add("Could not find settlement at " + levelIdentifier + " tile " + tileX + "x" + tileY);
            }
        } else {
            logs.add("Specified level is not loaded");
        }
    }
}

