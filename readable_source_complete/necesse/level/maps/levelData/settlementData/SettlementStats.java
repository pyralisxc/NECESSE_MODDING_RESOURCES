/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.stats.RaidEventsStat;

public class SettlementStats
extends EmptyStats {
    public final RaidEventsStat spawned_raids = this.addStat(new RaidEventsStat(this, "spawned_raids"));

    public SettlementStats() {
        super(false, EmptyStats.Mode.READ_AND_WRITE);
    }
}

