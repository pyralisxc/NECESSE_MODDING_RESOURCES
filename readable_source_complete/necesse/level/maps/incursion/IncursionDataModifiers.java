/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;

public class IncursionDataModifiers {
    public static final ModifierList LIST = new ModifierList();
    public static final Modifier<Float> MODIFIER_EXTRACTION_DROPS = new Modifier<Float>(LIST, "extractiondrops", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("extractiondrops"), ModifierLimiter.NORMAL_PERC_LIMITER("extractiondrops"));
    public static final Modifier<Float> MODIFIER_HUNT_DROPS = new Modifier<Float>(LIST, "huntdrops", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("huntdrops"), ModifierLimiter.NORMAL_PERC_LIMITER("huntdrops"));
}

