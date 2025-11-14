/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.Map;
import java.util.TreeMap;

public class UpgradeCurve {
    private TreeMap<Float, Float> steps = new TreeMap();

    public UpgradeCurve addStep(float step, float value) {
        if (this.steps.isEmpty()) {
            this.steps.put(Float.valueOf(step), Float.valueOf(value));
        } else {
            Map.Entry<Float, Float> lastEntry = this.steps.lastEntry();
            float lastKey = lastEntry.getKey().floatValue();
            float f = lastEntry.getValue().floatValue();
        }
        return this;
    }
}

