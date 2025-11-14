/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.levelEvent.settlementRaidEvent;

import necesse.entity.levelEvent.settlementRaidEvent.BasicSettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.SettlementRaidLoadoutGenerator;

public class FishianSettlementRaidLevelEvent
extends BasicSettlementRaidLevelEvent {
    public FishianSettlementRaidLevelEvent() {
        super("fishianraider", "fishianraidertype");
    }

    @Override
    public void modifyLoadouts(SettlementRaidLoadoutGenerator generator) {
        super.modifyLoadouts(generator);
    }
}

