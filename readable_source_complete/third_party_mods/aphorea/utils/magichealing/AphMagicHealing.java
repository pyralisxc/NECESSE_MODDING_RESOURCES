/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.levelEvent.LevelEvent
 *  necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.LocalMessageDoubleItemStatTip
 *  necesse.inventory.item.toolItem.ToolItem
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package aphorea.utils.magichealing;

import aphorea.items.tools.healing.AphMagicHealingToolItem;
import aphorea.registry.AphModifiers;
import aphorea.utils.magichealing.AphMagicHealingBuff;
import java.util.HashMap;
import java.util.Map;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.MobHealthChangeEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AphMagicHealing {
    static Map<Mob, Long> cooldowns = new HashMap<Mob, Long>();

    public static boolean canHealMob(Mob healer, @NotNull Mob target) {
        return !(target.getHealthPercent() == 1.0f || healer != target && target.canBeTargeted(healer, healer.isPlayer ? ((PlayerMob)healer).getNetworkClient() : null) || cooldowns.containsKey(target) && target.getWorldTime() < cooldowns.get(target));
    }

    public static void healMob(Mob healer, Mob target, int healing) {
        AphMagicHealing.healMob(healer, target, healing, null, null);
    }

    public static void healMob(Mob healer, Mob target, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        AphMagicHealing.healMobExecute(healer, target, AphMagicHealing.getMagicHealing(healer, target, healing, toolItem, item), item, toolItem);
    }

    public static void healMobExecute(Mob healer, Mob target, int healing) {
        AphMagicHealing.healMobExecute(healer, target, healing, null, null);
    }

    public static void healMobExecute(Mob healer, Mob target, int healing, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        for (Object buff : healer.buffManager.getArrayBuffs()) {
            if (!(((ActiveBuff)buff).buff instanceof AphMagicHealingBuff)) continue;
            healing = ((AphMagicHealingBuff)((ActiveBuff)buff).buff).onBeforeMagicalHealing((ActiveBuff)buff, healer, target, healing, toolItem, item);
        }
        int realHealing = Math.min(healing, target.getMaxHealth() - target.getHealth());
        if (realHealing > 0) {
            int magicalHealingGrace;
            int realHealingGrace;
            target.getLevel().entityManager.events.add((LevelEvent)new MobHealthChangeEvent(target, realHealing));
            for (ActiveBuff buff : healer.buffManager.getArrayBuffs()) {
                if (!(buff.buff instanceof AphMagicHealingBuff)) continue;
                ((AphMagicHealingBuff)buff.buff).onMagicalHealing(buff, healer, target, healing, realHealing, toolItem, item);
            }
            cooldowns.put(target, target.getWorldTime() + 50L);
            if (healer.getID() != target.getID() && (realHealingGrace = Math.min(magicalHealingGrace = (int)((float)realHealing * AphMagicHealing.getHealingGrace(healer, toolItem, item)), healer.getMaxHealth() - healer.getHealth())) > 0) {
                target.getLevel().entityManager.events.add((LevelEvent)new MobHealthChangeEvent(healer, realHealingGrace));
            }
        }
    }

    public static int getMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing) {
        return AphMagicHealing.getMagicHealing(healer, target, healing, null, null);
    }

    public static int getMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        return (int)((float)AphMagicHealing.getFlatMagicHealing(healer, target, healing) * AphMagicHealing.getMagicHealingMod(healer, target, toolItem, item));
    }

    public static float getMagicHealingMod(@Nullable Mob healer, @Nullable Mob target, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        float mod = 1.0f;
        if (healer != null) {
            mod += ((Float)healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING)).floatValue();
        }
        if (target != null) {
            mod += ((Float)target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED)).floatValue();
        }
        if (toolItem != null && item != null) {
            mod += ((Float)toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING)).floatValue();
            if (healer == target) {
                mod += ((Float)toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_RECEIVED)).floatValue();
            }
        }
        return mod;
    }

    public static int getFlatMagicHealing(@Nullable Mob healer, @Nullable Mob target, int healing) {
        return healing + AphMagicHealing.getFlatMagicHealingMod(healer, target);
    }

    public static int getFlatMagicHealingMod(@Nullable Mob healer, @Nullable Mob target) {
        int mod = 0;
        if (healer != null) {
            mod += ((Integer)healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_FLAT)).intValue();
        }
        if (target != null) {
            mod += ((Integer)target.buffManager.getModifier(AphModifiers.MAGIC_HEALING_RECEIVED_FLAT)).intValue();
        }
        return mod;
    }

    public static float getHealingGrace(@Nullable Mob healer, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        float grace = 0.0f;
        if (healer != null) {
            grace += ((Float)healer.buffManager.getModifier(AphModifiers.MAGIC_HEALING_GRACE)).floatValue();
        }
        if (toolItem != null && item != null) {
            grace += ((Float)toolItem.getEnchantment(item).getModifier(AphModifiers.TOOL_MAGIC_HEALING_GRACE)).floatValue();
        }
        return grace;
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent) {
        return AphMagicHealing.getMagicHealingToolTipPercent(healer, target, healingPercent, null, null);
    }

    public static String getMagicHealingToolTipPercent(@Nullable Mob healer, @Nullable Mob target, float healingPercent, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        float finalHealingPercent = healingPercent * AphMagicHealing.getMagicHealingMod(healer, target, toolItem, item);
        if (finalHealingPercent < 0.0f) {
            return "0%";
        }
        String value = String.format("%.2f", Float.valueOf(finalHealingPercent * 100.0f));
        return (value.endsWith(".00") ? value.substring(0, value.length() - 3) : value) + "%";
    }

    public static void addMagicHealingTip(AphMagicHealingToolItem aphoreaMagicHealingToolItem, ItemStatTipList list, InventoryItem currentItem, InventoryItem lastItem, Mob perspective) {
        int healing = AphMagicHealing.getMagicHealing(perspective, null, aphoreaMagicHealingToolItem.getHealing(currentItem), aphoreaMagicHealingToolItem, currentItem);
        LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", (double)healing, 0);
        if (lastItem != null) {
            int lastHealing = AphMagicHealing.getMagicHealing(perspective, null, aphoreaMagicHealingToolItem.getHealing(lastItem), aphoreaMagicHealingToolItem, lastItem);
            tip.setCompareValue((double)lastHealing);
        }
        list.add(100, (ItemStatTip)tip);
    }
}

