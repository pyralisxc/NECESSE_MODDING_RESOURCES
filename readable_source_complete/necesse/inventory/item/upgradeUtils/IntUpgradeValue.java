/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.upgradeUtils;

import java.util.Map;
import java.util.TreeMap;
import necesse.engine.util.GameMath;
import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class IntUpgradeValue
extends AbstractUpgradeValue<Integer> {
    public boolean inverse;
    public int defaultValue;
    public float defaultLevelIncreaseMultiplier;
    private TreeMap<Float, Integer> map = new TreeMap();

    public IntUpgradeValue(boolean inverse, int defaultValue, float defaultLevelIncreaseMultiplier) {
        this.inverse = inverse;
        this.defaultValue = defaultValue;
        this.defaultLevelIncreaseMultiplier = defaultLevelIncreaseMultiplier;
    }

    public IntUpgradeValue(int defaultValue, float defaultLevelIncreaseMultiplier) {
        this(false, defaultValue, defaultLevelIncreaseMultiplier);
    }

    public IntUpgradeValue() {
        this(false, 0, 0.0f);
    }

    public IntUpgradeValue setBaseValue(int value) {
        return this.setUpgradedValue(0.0f, value);
    }

    public IntUpgradeValue setUpgradedValue(float tier, int value) {
        this.map.put(Float.valueOf(tier), value);
        return this;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean hasMoreThanOneValue() {
        return this.map.size() > 1;
    }

    @Override
    public Integer getValue(float tier) {
        Map.Entry<Float, Integer> higher;
        Map.Entry<Float, Integer> floorEntry = null;
        Map.Entry<Float, Integer> ceilEntry = null;
        Map.Entry<Float, Integer> closestEntry = this.map.floorEntry(Float.valueOf(tier));
        if (closestEntry != null) {
            if (tier == closestEntry.getKey().floatValue()) {
                return closestEntry.getValue();
            }
            floorEntry = closestEntry;
            higher = this.map.higherEntry(Float.valueOf(tier));
            if (higher != null) {
                ceilEntry = higher;
            } else {
                floorEntry = this.map.lowerEntry(closestEntry.getKey());
                ceilEntry = closestEntry;
            }
        } else {
            higher = this.map.higherEntry(Float.valueOf(tier));
            if (higher != null) {
                floorEntry = higher;
                ceilEntry = this.map.higherEntry(higher.getKey());
            }
        }
        if (floorEntry != null && ceilEntry != null) {
            if (floorEntry.getKey().floatValue() < 1.0f) {
                int valueAtTier1;
                if (ceilEntry.getKey().floatValue() > 1.0f) {
                    valueAtTier1 = Math.round((float)ceilEntry.getValue().intValue() / (1.0f + this.defaultLevelIncreaseMultiplier * ceilEntry.getKey().floatValue()));
                } else if (ceilEntry.getKey().floatValue() < 1.0f) {
                    float tier1UpgradePercentage = GameMath.getPercentageBetweenTwoNumbers(1.0f, floorEntry.getKey().floatValue(), ceilEntry.getKey().floatValue());
                    valueAtTier1 = GameMath.lerp(tier1UpgradePercentage, floorEntry.getValue(), ceilEntry.getValue());
                } else {
                    valueAtTier1 = ceilEntry.getValue();
                }
                if (tier > 1.0f) {
                    float modifier = 1.0f + (tier - 1.0f) * this.defaultLevelIncreaseMultiplier;
                    if (this.inverse) {
                        return Math.round((float)valueAtTier1 * (1.0f / modifier));
                    }
                    return Math.round((float)valueAtTier1 * modifier);
                }
                float upgradeLevelPercentage = GameMath.getPercentageBetweenTwoNumbers(tier, floorEntry.getKey().floatValue(), 1.0f);
                return GameMath.lerp(upgradeLevelPercentage, floorEntry.getValue(), valueAtTier1);
            }
            float upgradeLevelPercentage = GameMath.getPercentageBetweenTwoNumbers(tier, floorEntry.getKey().floatValue(), ceilEntry.getKey().floatValue());
            return GameMath.lerp(upgradeLevelPercentage, floorEntry.getValue(), ceilEntry.getValue());
        }
        if (floorEntry != null) {
            return floorEntry.getValue();
        }
        if (ceilEntry != null) {
            return (Integer)ceilEntry.getValue();
        }
        return this.defaultValue;
    }
}

