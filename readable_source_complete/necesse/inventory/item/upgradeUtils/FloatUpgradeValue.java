/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.upgradeUtils;

import java.util.Map;
import java.util.TreeMap;
import necesse.engine.util.GameMath;
import necesse.inventory.item.upgradeUtils.AbstractUpgradeValue;

public class FloatUpgradeValue
extends AbstractUpgradeValue<Float> {
    public boolean inverse;
    public float defaultValue;
    public float defaultLevelIncreaseMultiplier;
    private TreeMap<Float, Float> map = new TreeMap();

    public FloatUpgradeValue(boolean inverse, float defaultValue, float defaultLevelIncreaseMultiplier) {
        this.inverse = inverse;
        this.defaultValue = defaultValue;
        this.defaultLevelIncreaseMultiplier = defaultLevelIncreaseMultiplier;
    }

    public FloatUpgradeValue(float defaultValue, float defaultLevelIncreaseMultiplier) {
        this(false, defaultValue, defaultLevelIncreaseMultiplier);
    }

    public FloatUpgradeValue() {
        this(false, 0.0f, 0.0f);
    }

    public FloatUpgradeValue setBaseValue(float value) {
        return this.setUpgradedValue(0.0f, value);
    }

    public FloatUpgradeValue setUpgradedValue(float tier, float value) {
        this.map.put(Float.valueOf(tier), Float.valueOf(value));
        return this;
    }

    public boolean isEmpty() {
        return this.map.isEmpty();
    }

    public boolean hasMoreThanOneValue() {
        return this.map.size() > 1;
    }

    @Override
    public Float getValue(float tier) {
        Map.Entry<Float, Float> higher;
        Map.Entry<Float, Float> floorEntry = null;
        Map.Entry<Float, Float> ceilEntry = null;
        Map.Entry<Float, Float> closestEntry = this.map.floorEntry(Float.valueOf(tier));
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
                float valueAtTier1;
                if (ceilEntry.getKey().floatValue() > 1.0f) {
                    valueAtTier1 = ceilEntry.getValue().floatValue() / (1.0f + this.defaultLevelIncreaseMultiplier * ceilEntry.getKey().floatValue());
                } else if (ceilEntry.getKey().floatValue() < 1.0f) {
                    float tier1UpgradePercentage = GameMath.getPercentageBetweenTwoNumbers(1.0f, floorEntry.getKey().floatValue(), ceilEntry.getKey().floatValue());
                    valueAtTier1 = GameMath.lerp(tier1UpgradePercentage, floorEntry.getValue().floatValue(), ceilEntry.getValue().floatValue());
                } else {
                    valueAtTier1 = ceilEntry.getValue().floatValue();
                }
                if (tier > 1.0f) {
                    float modifier = 1.0f + (tier - 1.0f) * this.defaultLevelIncreaseMultiplier;
                    if (this.inverse) {
                        return Float.valueOf(valueAtTier1 * (1.0f / modifier));
                    }
                    return Float.valueOf(valueAtTier1 * modifier);
                }
                float upgradeLevelPercentage = GameMath.getPercentageBetweenTwoNumbers(tier, floorEntry.getKey().floatValue(), 1.0f);
                return Float.valueOf(GameMath.lerp(upgradeLevelPercentage, floorEntry.getValue().floatValue(), valueAtTier1));
            }
            float upgradeLevelPercentage = GameMath.getPercentageBetweenTwoNumbers(tier, floorEntry.getKey().floatValue(), ceilEntry.getKey().floatValue());
            return Float.valueOf(GameMath.lerp(upgradeLevelPercentage, floorEntry.getValue().floatValue(), ceilEntry.getValue().floatValue()));
        }
        if (floorEntry != null) {
            return floorEntry.getValue();
        }
        if (ceilEntry != null) {
            return (Float)ceilEntry.getValue();
        }
        return Float.valueOf(this.defaultValue);
    }
}

