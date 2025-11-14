/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.expeditions;

import necesse.engine.expeditions.SettlerExpedition;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;

public abstract class FishingTripExpedition
extends SettlerExpedition {
    @Override
    public float getSuccessChance(ServerSettlementData settlement) {
        return 1.0f;
    }
}

