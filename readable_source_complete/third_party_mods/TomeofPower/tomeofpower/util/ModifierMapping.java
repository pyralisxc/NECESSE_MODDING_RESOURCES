/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.modifiers.Modifier
 *  necesse.entity.mobs.buffs.BuffModifiers
 *  necesse.inventory.enchants.ToolItemModifiers
 */
package tomeofpower.util;

import necesse.engine.modifiers.Modifier;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.inventory.enchants.ToolItemModifiers;

public class ModifierMapping {
    public static Modifier<?> mapToolModifierToBuffModifier(Modifier<?> toolMod) {
        if (toolMod == ToolItemModifiers.DAMAGE) {
            return BuffModifiers.ALL_DAMAGE;
        }
        if (toolMod == ToolItemModifiers.CRIT_CHANCE) {
            return BuffModifiers.CRIT_CHANCE;
        }
        if (toolMod == ToolItemModifiers.ARMOR_PEN) {
            return BuffModifiers.ARMOR_PEN_FLAT;
        }
        if (toolMod == ToolItemModifiers.KNOCKBACK) {
            return BuffModifiers.KNOCKBACK_OUT;
        }
        if (toolMod == ToolItemModifiers.RESILIENCE_GAIN) {
            return BuffModifiers.RESILIENCE_GAIN;
        }
        if (toolMod == ToolItemModifiers.RANGE) {
            return BuffModifiers.TARGET_RANGE;
        }
        if (toolMod == ToolItemModifiers.SUMMONS_TARGET_RANGE) {
            return BuffModifiers.SUMMONS_TARGET_RANGE;
        }
        if (toolMod == ToolItemModifiers.ATTACK_SPEED) {
            return BuffModifiers.ATTACK_SPEED;
        }
        if (toolMod == ToolItemModifiers.SUMMONS_SPEED) {
            return BuffModifiers.SUMMONS_SPEED;
        }
        if (toolMod == ToolItemModifiers.VELOCITY) {
            return BuffModifiers.PROJECTILE_VELOCITY;
        }
        if (toolMod == ToolItemModifiers.MANA_USAGE) {
            return BuffModifiers.MANA_USAGE;
        }
        if (toolMod == ToolItemModifiers.TOOL_DAMAGE) {
            return BuffModifiers.TOOL_DAMAGE;
        }
        if (toolMod == ToolItemModifiers.MINING_SPEED) {
            return BuffModifiers.MINING_SPEED;
        }
        return null;
    }
}

