/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.stats.IncursionBiomeGameStat;

public class AltarStats
extends EmptyStats {
    public final IncursionBiomeGameStat opened_incursions = this.addStat(new IncursionBiomeGameStat(this, "opened_incursions"));
    public final IncursionBiomeGameStat completed_incursions = this.addStat(new IncursionBiomeGameStat(this, "completed_incursions"));

    public AltarStats(EmptyStats.Mode mode) {
        super(false, mode);
    }
}

