/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import necesse.engine.playerStats.EmptyStats;
import necesse.engine.playerStats.PlayerStats;

public class LevelStats
extends PlayerStats {
    public LevelStats() {
        super(false, EmptyStats.Mode.READ_AND_WRITE);
    }
}

