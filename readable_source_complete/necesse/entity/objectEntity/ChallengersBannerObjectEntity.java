/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.objectEntity.BannerObjectEntity;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public class ChallengersBannerObjectEntity
extends BannerObjectEntity {
    protected int tickBuffer = 0;

    public ChallengersBannerObjectEntity(Level level, int x, int y) {
        super(level, "challengersbanner", x, y);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        ++this.tickBuffer;
        if (this.tickBuffer >= 20) {
            this.tickBuffer -= 20;
            ServerSettlementData settlement = SettlementsWorldData.getSettlementsData(this.getLevel()).getServerDataAtTile(this.getLevel().getIdentifier(), this.tileX, this.tileY);
            if (settlement != null) {
                settlement.reduceRaidTimer(1);
            }
        }
    }
}

