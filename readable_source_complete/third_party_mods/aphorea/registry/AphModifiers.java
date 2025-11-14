/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.engine.modifiers.ModifierLimiter
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.inventory.enchants.ToolItemModifiers
 */
package aphorea.registry;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.enchants.ToolItemModifiers;

public class AphModifiers {
    public static final Modifier<Float> MAGIC_HEALING = new Modifier(BuffModifiers.LIST, "magichealing", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealing"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealing"));
    public static final Modifier<Integer> MAGIC_HEALING_FLAT = new Modifier(BuffModifiers.LIST, "magichealingflat", (Object)0, (Object)0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER((String)"magichealingflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER((String)"magichealingflat"));
    public static final Modifier<Float> MAGIC_HEALING_RECEIVED = new Modifier(BuffModifiers.LIST, "magichealingreceived", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealingreceived"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealingreceived"));
    public static final Modifier<Integer> MAGIC_HEALING_RECEIVED_FLAT = new Modifier(BuffModifiers.LIST, "magichealingreceivedflat", (Object)0, (Object)0, Modifier.INT_ADD_APPEND, Modifier.NORMAL_FLAT_INT_PARSER((String)"magichealingreceivedflat"), ModifierLimiter.NORMAL_FLAT_INT_LIMITER((String)"magichealingreceivedflat"));
    public static final Modifier<Float> MAGIC_HEALING_GRACE = new Modifier(BuffModifiers.LIST, "magichealinggrace", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealinggrace"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealinggrace"));
    public static final Modifier<Float> TOOL_MAGIC_HEALING = new Modifier(ToolItemModifiers.LIST, "magichealing", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealing"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealing"));
    public static final Modifier<Float> TOOL_MAGIC_HEALING_RECEIVED = new Modifier(ToolItemModifiers.LIST, "magichealingreceived", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealingreceived"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealingreceived"));
    public static final Modifier<Float> TOOL_MAGIC_HEALING_GRACE = new Modifier(ToolItemModifiers.LIST, "magichealinggrace", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"magichealinggrace"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"magichealinggrace"));
    public static final Modifier<Float> TOOL_AREA_RANGE = new Modifier(ToolItemModifiers.LIST, "arearange", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"arearange"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"arearange"));
    public static final Modifier<Boolean> LOYAL = new Modifier(ToolItemModifiers.LIST, "loyal", (Object)false, (Object)false, Modifier.OR_APPEND, Modifier.BOOL_PARSER((String)"loyal"), null);
    public static final Modifier<Float> INSPIRATION_DAMAGE = new Modifier(BuffModifiers.LIST, "inspirationdamage", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> v, Modifier.NORMAL_PERC_PARSER((String)"inspirationdamage"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"inspirationdamage"));
    public static final Modifier<Float> INSPIRATION_CRIT_CHANCE = new Modifier(BuffModifiers.LIST, "inspirationcritchance", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> v, Modifier.NORMAL_PERC_PARSER((String)"inspirationcritchance"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"inspirationcritchance"));
    public static final Modifier<Float> INSPIRATION_CRIT_DAMAGE = new Modifier(BuffModifiers.LIST, "inspirationcritdamage", (Object)Float.valueOf(0.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> v, Modifier.NORMAL_PERC_PARSER((String)"inspirationcritdamage"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"inspirationcritdamage"));
    public static final Modifier<Float> INSPIRATION_EFFECT = new Modifier(BuffModifiers.LIST, "inspirationeffect", (Object)Float.valueOf(1.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"inspirationeffect"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"inspirationeffect"));
    public static final Modifier<Float> INSPIRATION_ABILITY_SPEED = new Modifier(BuffModifiers.LIST, "inspirationabilityspeed", (Object)Float.valueOf(1.0f), (Object)Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER((String)"inspirationabilityspeed"), ModifierLimiter.NORMAL_PERC_LIMITER((String)"inspirationabilityspeed"));
}

