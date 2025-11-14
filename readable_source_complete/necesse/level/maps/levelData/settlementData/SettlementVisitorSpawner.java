/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementVisitorOdds;

public class SettlementVisitorSpawner {
    public final SettlementVisitorOdds odds;
    public final HumanMob mob;

    public SettlementVisitorSpawner(SettlementVisitorOdds odds, HumanMob mob) {
        this.odds = odds;
        this.mob = mob;
    }

    public Point findRandomSpawnLocation(ServerSettlementData settlement) {
        if (this.mob == null) {
            Point spawnTile = settlement.findRandomSpawnTile(tp -> !tp.isSolidTile(), true);
            if (spawnTile != null) {
                return new Point(spawnTile.x * 32 + 16, spawnTile.y * 32 + 16);
            }
            return null;
        }
        return settlement.findRandomSpawnLevelPos(this.mob, true);
    }

    public void onSpawned(Level level, ServerSettlementData data, Point spawnPos) {
    }

    public GameMessage getArriveMessage(ServerSettlementData data) {
        return new LocalMessage("settlement", "travelingarrive", "mob", this.mob.getDisplayName(), "settlement", data.networkData.getSettlementName());
    }
}

