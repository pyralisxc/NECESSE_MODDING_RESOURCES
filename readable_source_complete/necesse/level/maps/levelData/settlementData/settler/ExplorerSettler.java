/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.TicketSystemList;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;

public class ExplorerSettler
extends Settler {
    public static ArrayList<String> spawnBiomes = new ArrayList<String>(Arrays.asList("desert", "desertvillage"));
    public static double spawnChance = GameMath.getAverageSuccessRuns(120.0);

    public ExplorerSettler() {
        super("explorerhuman");
    }

    @Override
    public GameMessage getAcquireTip() {
        return new LocalMessage("settlement", "explorertip");
    }

    @Override
    public double getSpawnChance(Server server, ServerClient client, Level level) {
        if (level.isCave && spawnBiomes.contains(level.getBiome(client.playerMob.getTileX(), client.playerMob.getTileY()).getStringID())) {
            return spawnChance;
        }
        return 0.0;
    }

    @Override
    public void spawnAtClient(Server server, ServerClient client, Level level) {
        if (client.characterStats().mob_kills.getKills("voidwizard") <= 0) {
            return;
        }
        if (level.entityManager.mobs.streamInRegionsInTileRange(client.playerMob.getX(), client.playerMob.getY(), Settler.SETTLER_SPAWN_AREA.maxSpawnDistance * 2).anyMatch(m -> m.getStringID().equals(this.mobStringID))) {
            return;
        }
        ExplorerHumanMob mob = (ExplorerHumanMob)MobRegistry.getMob(this.mobStringID, level);
        mob.setLost(true);
        Point spawnPos = this.getSpawnLocation(client, level, mob, Settler.SETTLER_SPAWN_AREA);
        if (spawnPos != null) {
            level.entityManager.addMob(mob, spawnPos.x, spawnPos.y);
        }
    }

    @Override
    public void addNewRecruitSettler(ServerSettlementData data, boolean isRandomEvent, TicketSystemList<Supplier<HumanMob>> ticketSystem) {
        if ((isRandomEvent || !this.doesSettlementHaveThisSettler(data)) && data.hasCompletedQuestTier("piratecaptain")) {
            ticketSystem.addObject(100, (Object)this.getNewRecruitMob(data));
        }
    }
}

