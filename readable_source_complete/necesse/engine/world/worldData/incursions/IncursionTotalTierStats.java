/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.world.worldData.incursions;

import java.util.TreeMap;

public class IncursionTotalTierStats {
    private final IncursionTotalTierStats parent;
    private final TreeMap<Integer, Integer> totals = new TreeMap();

    public IncursionTotalTierStats(IncursionTotalTierStats parent) {
        this.parent = parent;
    }

    public void clear() {
        this.totals.clear();
    }

    public void add(int tier, int count) {
        if (this.parent != null) {
            this.parent.add(tier, count);
        }
        this.totals.put(tier, this.totals.getOrDefault(tier, 0) + count);
    }

    public int get(boolean includeParent, int tier) {
        int parentTotal = includeParent && this.parent != null ? this.parent.get(includeParent, tier) : 0;
        return parentTotal + this.totals.getOrDefault(tier, 0);
    }

    public void combine(IncursionTotalTierStats other) {
        other.totals.forEach((key, value) -> this.totals.put((Integer)key, this.totals.getOrDefault(key, 0) + value));
    }

    public int getTotalBelow(boolean includeParent, int tier, boolean inclusive) {
        int parentTotal = includeParent && this.parent != null ? this.parent.getTotalBelow(includeParent, tier, inclusive) : 0;
        return parentTotal + this.totals.headMap(tier, inclusive).values().stream().mapToInt(v -> v).sum();
    }

    public int getTotalAbove(boolean includeParent, int tier, boolean inclusive) {
        int parentTotal = includeParent && this.parent != null ? this.parent.getTotalAbove(includeParent, tier, inclusive) : 0;
        return parentTotal + this.totals.tailMap(tier, inclusive).values().stream().mapToInt(v -> v).sum();
    }
}

