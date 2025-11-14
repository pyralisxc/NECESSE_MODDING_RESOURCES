/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.friendly.human.humanShop.HunterHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class HunterSettler
extends Settler {
    public static ArrayList<String> spawnBiomes = new ArrayList<String>(Arrays.asList("forest", "plains", "desert", "swamp", "snow"));
    public static double spawnChance = GameMath.getAverageSuccessRuns(120.0);

    public HunterSettler() {
        super("hunterhuman");
    }

    @Override
    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "huntertip");
    }

    @Override
    public double getSpawnChance(Server server, ServerClient client, Level level) {
        if (!level.isCave && spawnBiomes.contains(level.getBiome(client.playerMob.getTileX(), client.playerMob.getTileY()).getStringID())) {
            return spawnChance;
        }
        return 0.0;
    }

    @Override
    public void spawnAtClient(Server server, ServerClient client, Level level) {
        if (level.entityManager.mobs.streamInRegionsInTileRange(client.playerMob.getX(), client.playerMob.getY(), HunterSettler.SETTLER_SPAWN_AREA.maxSpawnDistance * 2).anyMatch(m -> m.getStringID().equals(this.mobStringID))) {
            return;
        }
        HunterHumanMob mob = (HunterHumanMob)MobRegistry.getMob(this.mobStringID, level);
        mob.setLost(true);
        Point spawnPos = this.getSpawnLocation(client, level, mob, SETTLER_SPAWN_AREA);
        if (spawnPos != null) {
            level.entityManager.addMob(mob, spawnPos.x, spawnPos.y);
        }
    }
}

