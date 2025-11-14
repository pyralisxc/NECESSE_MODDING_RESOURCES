/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.incursionPerkTree;

import necesse.level.maps.incursion.IncursionBiome;

class IncursionAndTierRequirement {
    IncursionBiome incursionBiome;
    int tier;
    int amount;

    public IncursionAndTierRequirement(IncursionBiome incursionBiome, int tier) {
        this.incursionBiome = incursionBiome;
        this.tier = tier;
    }

    public IncursionAndTierRequirement(int tier, int amount) {
        this.tier = tier;
        this.amount = amount;
    }
}

