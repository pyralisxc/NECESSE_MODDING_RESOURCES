/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelBuffManager;

import necesse.engine.modifiers.Modifier;
import necesse.engine.modifiers.ModifierLimiter;
import necesse.engine.modifiers.ModifierList;

public class LevelModifiers {
    public static final ModifierList LIST = new ModifierList();
    public static final Modifier<Float> LOOT = new Modifier<Float>(LIST, "loot", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.NORMAL_PERC_PARSER("loot"), ModifierLimiter.NORMAL_PERC_LIMITER("loot"));
    public static final Modifier<Float> ENEMY_DAMAGE = new Modifier<Float>(LIST, "enemydamage", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.INVERSE_PERC_PARSER("enemydamage"), ModifierLimiter.INVERSE_PERC_LIMITER("enemydamage"));
    public static final Modifier<Float> ENEMY_SPEED = new Modifier<Float>(LIST, "enemyspeed", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.INVERSE_PERC_PARSER("enemyspeed"), ModifierLimiter.INVERSE_PERC_LIMITER("enemyspeed"));
    public static final Modifier<Float> ENEMY_MAX_HEALTH = new Modifier<Float>(LIST, "enemymaxhealth", Float.valueOf(1.0f), Float.valueOf(0.0f), Modifier.FLOAT_ADD_APPEND, v -> Float.valueOf(Math.max(0.0f, v.floatValue())), Modifier.INVERSE_PERC_PARSER("enemymaxhealth"), ModifierLimiter.INVERSE_PERC_LIMITER("enemymaxhealth"));
    public static final Modifier<Boolean> ENEMIES_RETREATING = new Modifier<Boolean>(LIST, "enemiesretreating", Boolean.valueOf(false), false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("enemiesretreating"), null);
    public static final Modifier<Boolean> BANNER_OF_WAR_DISABLED = new Modifier<Boolean>(LIST, "bannerofwardisabled", Boolean.valueOf(false), false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("bannerofwardisabled"), ModifierLimiter.BOOL_LIMITER("bannerofwardisabled"));
    public static final Modifier<Boolean> MODIFIERS_AFFECT_ENEMIES = new Modifier<Boolean>(LIST, "modifiersaffectenemies", Boolean.valueOf(false), false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("modifiersaffectenemies"), ModifierLimiter.BOOL_LIMITER("modifiersaffectenemies"));
    public static final Modifier<Boolean> SPIRIT_CORRUPTED = new Modifier<Boolean>(LIST, "spiritcorrupted", Boolean.valueOf(false), false, Modifier.OR_APPEND, Modifier.BOOL_PARSER("spiritcorrupted"), null);
}

