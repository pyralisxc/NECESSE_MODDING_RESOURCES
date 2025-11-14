/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.enchants;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;

public class ToolItemModifiers {
    public static final ModifierList LIST = new ModifierList();
    public static final Modifier<Float> DAMAGE = new Modifier<Float>(LIST, "damage", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("damage"), ModifierLimiter.NORMAL_PERC_LIMITER("damage"));
    public static final Modifier<Float> CRIT_CHANCE = new Modifier<Float>(LIST, "critchance", Float.valueOf(0.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("critchance"), ModifierLimiter.NORMAL_PERC_LIMITER("critchance"));
    public static final Modifier<Integer> ARMOR_PEN = new Modifier<Integer>(LIST, "armorpen", Integer.valueOf(0), 0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER("armorpenflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER("armorpenflat"));
    public static final Modifier<Float> KNOCKBACK = new Modifier<Float>(LIST, "knockback", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("toolknockback"), ModifierLimiter.NORMAL_PERC_LIMITER("toolknockback"));
    public static final Modifier<Float> RESILIENCE_GAIN = new Modifier<Float>(LIST, "resilience", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("resiliencegain"), ModifierLimiter.NORMAL_PERC_LIMITER("resiliencegain"));
    public static final Modifier<Float> RANGE = new Modifier<Float>(LIST, "range", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("toolrange"), ModifierLimiter.NORMAL_PERC_LIMITER("toolrange"));
    public static final Modifier<Float> SUMMONS_TARGET_RANGE = new Modifier<Float>(LIST, "summontargetrange", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("summonstargetrange"), ModifierLimiter.NORMAL_PERC_LIMITER("summonstargetrange"));
    public static final Modifier<Float> ATTACK_SPEED = new Modifier<Float>(LIST, "attackspeed", Float.valueOf(0.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> v, Modifier.NORMAL_PERC_PARSER("attackspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("attackspeed"));
    public static final Modifier<Float> SUMMONS_SPEED = new Modifier<Float>(LIST, "summonsspeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("summonsspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("summonsspeed"));
    public static final Modifier<Float> VELOCITY = new Modifier<Float>(LIST, "velocity", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("projectilevel"), ModifierLimiter.NORMAL_PERC_LIMITER("projectilevel"));
    public static final Modifier<Float> MANA_USAGE = new Modifier<Float>(LIST, "manausage", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.INVERSE_PERC_PARSER("manausage"), ModifierLimiter.NORMAL_PERC_LIMITER("manausage"));
    public static final Modifier<Float> TOOL_DAMAGE = new Modifier<Float>(LIST, "tooldamage", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("tooldamage"), ModifierLimiter.NORMAL_PERC_LIMITER("tooldamage"));
    public static final Modifier<Float> MINING_SPEED = new Modifier<Float>(LIST, "miningspeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("miningspeed"), ModifierLimiter.NORMAL_PERC_LIMITER("miningspeed"));
}

