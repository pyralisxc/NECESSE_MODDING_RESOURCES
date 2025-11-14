/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.settler;

import necesse.engine.playerStats.PlayerStats;
import necesse.entity.mobs.friendly.human.ElderHumanMob;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;

public class ElderSettler
extends Settler {
    public ElderSettler() {
        super("elderhuman");
    }

    @Override
    public float getArriveAsRecruitAfterDeathChance(ServerSettlementData settlement) {
        return 0.0f;
    }

    @Override
    public boolean canSpawnInSettlement(ServerSettlementData settlement, PlayerStats stats) {
        return settlement.countSettlersWithoutBed() < 2 && !settlement.hasSettler(this);
    }

    @Override
    public boolean canMoveOut(LevelSettler settler, ServerSettlementData settlement) {
        return false;
    }

    @Override
    public boolean canBanish(LevelSettler settler, ServerSettlementData settlement) {
        return false;
    }

    @Override
    public SettlerMob getNewSettlerMob(Level level, ServerSettlementData settlement) {
        if (settlement == null) {
            return super.getNewSettlerMob(level, null);
        }
        return level.entityManager.mobs.streamInRegionsShape(settlement.networkData.getLevelRectangle(), 0).filter(m -> m.getStringID().equals(this.mobStringID)).map(m -> (ElderHumanMob)m).filter(m -> !m.isSettler()).map(m -> m).findFirst().filter(m -> !m.isSettler()).orElseGet(() -> super.getNewSettlerMob(level, settlement));
    }
}

