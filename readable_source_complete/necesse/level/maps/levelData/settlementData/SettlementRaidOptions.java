/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.util.HashSet;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementWealthCounter;

public class SettlementRaidOptions {
    public final ServerSettlementData serverData;
    public SettlementRaidLevelEvent.RaidDir direction = null;
    public float difficultyModifier;
    public boolean dontAutoAttackSettlement;
    public HashSet<String> obtainedItems = new HashSet();
    public SettlementWealthCounter wealthCounter;

    public SettlementRaidOptions(ServerSettlementData serverData, HashSet<String> obtainedItems, SettlementWealthCounter wealthCounter) {
        this.serverData = serverData;
        this.obtainedItems = obtainedItems;
        this.wealthCounter = wealthCounter;
    }
}

