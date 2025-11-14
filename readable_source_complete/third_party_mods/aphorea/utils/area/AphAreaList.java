/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.gfx.GameColor
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.DoubleItemStatTip
 *  necesse.inventory.item.ItemStatTip
 *  necesse.inventory.item.ItemStatTipList
 *  necesse.inventory.item.LocalMessageDoubleItemStatTip
 *  necesse.inventory.item.LocalMessageStringItemStatTip
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.level.maps.Level
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package aphorea.utils.area;

import aphorea.packets.AphAreaShowPacket;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaType;
import aphorea.utils.magichealing.AphMagicHealing;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.gfx.GameColor;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.DoubleItemStatTip;
import necesse.inventory.item.ItemStatTip;
import necesse.inventory.item.ItemStatTipList;
import necesse.inventory.item.LocalMessageDoubleItemStatTip;
import necesse.inventory.item.LocalMessageStringItemStatTip;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.level.maps.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AphAreaList {
    public final List<AphArea> areas = new ArrayList<AphArea>();

    public AphAreaList(AphArea ... areas) {
        for (AphArea area : areas) {
            this.addArea(area);
        }
    }

    public AphAreaList addAreas(AphArea ... areas) {
        for (AphArea area : areas) {
            this.addArea(area);
        }
        return this;
    }

    public AphAreaList addArea(@NotNull AphArea area) {
        if (this.areas.isEmpty()) {
            area.position = 0;
            area.antRange = 0.0f;
            area.currentRange = area.range;
        } else {
            AphArea antArea = this.areas.get(this.areas.size() - 1);
            area.position = antArea.position + 1;
            area.antRange = antArea.range;
            area.currentRange = area.range;
            area.range += area.antRange;
        }
        this.areas.add(area);
        return this;
    }

    public AphAreaList setOnlyVision(boolean onlyVision) {
        this.areas.forEach(area -> area.setOnlyVision(onlyVision));
        return this;
    }

    public AphAreaList setIgnoreLight(boolean ignoreLight) {
        this.areas.forEach(area -> area.setIgnoreLight(ignoreLight));
        return this;
    }

    public void executeClient(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier, int particleTime) {
        this.areas.forEach(area -> area.showParticles(level, x, y, null, rangeModifier, borderParticleModifier, innerParticleModifier, particleTime));
    }

    public void executeClient(Level level, float x, float y, float rangeModifier, float borderParticleModifier, float innerParticleModifier) {
        this.executeClient(level, x, y, rangeModifier, borderParticleModifier, innerParticleModifier, (int)(Math.random() * 200.0) + 900);
    }

    public void executeClient(Level level, float x, float y, float rangeModifier) {
        this.executeClient(level, x, y, rangeModifier, 1.0f, 0.2f, (int)(Math.random() * 200.0) + 900);
    }

    public void executeClient(Level level, float x, float y) {
        this.executeClient(level, x, y, 1.0f);
    }

    public void executeServer(@NotNull Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        if (attacker.isServer()) {
            int range = Math.round(this.areas.get((int)(this.areas.size() - 1)).range * rangeModifier);
            attacker.getLevel().entityManager.streamAreaMobsAndPlayers(x, y, range).forEach(target -> {
                for (AphArea area : this.areas) {
                    area.executeServer(attacker, (Mob)target, x, y, rangeModifier, item, toolItem);
                }
            });
        }
    }

    public void executeServer(Mob attacker, float x, float y, float rangeModifier) {
        this.executeServer(attacker, x, y, rangeModifier, null, null);
    }

    public void executeServer(Mob attacker, float x, float y) {
        this.executeServer(attacker, x, y, 1.0f);
    }

    public void executeServer(Mob attacker) {
        this.executeServer(attacker, attacker.x, attacker.y);
    }

    public void sendExecutePacket(@NotNull Level level, float x, float y, float rangeModifier) {
        if (level.isServer()) {
            level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphAreaShowPacket(x, y, this, rangeModifier), level);
        }
    }

    public void sendExecutePacket(@NotNull Level level, float x, float y) {
        this.sendExecutePacket(level, x, y, 1.0f);
    }

    public void execute(@NotNull Mob attacker, float x, float y, float rangeModifier, @Nullable InventoryItem item, @Nullable ToolItem toolItem, boolean sendPacket) {
        if (attacker.isServer()) {
            this.executeServer(attacker, x, y, rangeModifier, item, toolItem);
            if (sendPacket) {
                this.sendExecutePacket(attacker.getLevel(), x, y, rangeModifier);
            }
        }
        if (!sendPacket && attacker.isClient()) {
            this.executeClient(attacker.getLevel(), x, y, rangeModifier);
        }
    }

    public void execute(Mob attacker, float x, float y, float rangeModifier, boolean sendPacket) {
        this.execute(attacker, x, y, rangeModifier, null, null, sendPacket);
    }

    public void execute(Mob attacker, float x, float y, boolean sendPacket) {
        this.execute(attacker, x, y, 1.0f, sendPacket);
    }

    public void execute(Mob attacker, boolean sendPacket) {
        this.execute(attacker, attacker.x, attacker.y, sendPacket);
    }

    public boolean someType(AphAreaType type) {
        return this.areas.stream().anyMatch(a -> a.areaTypes.contains((Object)type));
    }

    public void addAreasToolTip(ListGameTooltips tooltips, Attacker attacker, boolean forceLines, @Nullable InventoryItem item, @Nullable ToolItem toolItem) {
        boolean lines;
        boolean bl = lines = this.areas.size() > 1 || forceLines;
        if (lines) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"line"));
        }
        for (int i = 0; i < this.areas.size(); ++i) {
            AphArea area = this.areas.get(i);
            if (lines && this.areas.size() > 1) {
                tooltips.add(Localization.translate((String)"itemtooltip", (String)"areatip", (String)"number", (Object)(i + 1)));
            }
            if (area.areaTypes.contains((Object)AphAreaType.DAMAGE)) {
                tooltips.add((Object)area.getDamage().type.getDamageTip((int)area.getDamage().damage).toTooltip((Color)GameColor.GREEN.color.get(), (Color)GameColor.RED.color.get(), (Color)GameColor.YELLOW.color.get(), false));
            }
            if (area.areaTypes.contains((Object)AphAreaType.HEALING)) {
                int healing = AphMagicHealing.getMagicHealing((Mob)attacker, null, area.getHealing(), toolItem, item);
                tooltips.add(Localization.translate((String)"itemtooltip", (String)"magichealingtip", (String)"health", (Object)healing));
            }
            if (area.areaTypes.contains((Object)AphAreaType.BUFF)) {
                Arrays.stream(area.buffs).forEach(buffID -> {
                    Buff buff = BuffRegistry.getBuff((String)buffID);
                    tooltips.add(Localization.translate((String)"itemtooltip", (String)"areabuff", (String)"buff", (String)Localization.translate((String)"itemtooltip", (String)"areabuffdisplay", (Object[])new Object[]{"buff", buff.getLocalization(), "duration", Float.valueOf((float)area.buffDuration / 1000.0f)})));
                });
            }
            if (area.areaTypes.contains((Object)AphAreaType.DEBUFF)) {
                Arrays.stream(area.debuffs).forEach(buffID -> {
                    Buff buff = BuffRegistry.getBuff((String)buffID);
                    tooltips.add(Localization.translate((String)"itemtooltip", (String)"areadebuff", (String)"buff", (String)Localization.translate((String)"itemtooltip", (String)"areabuffdisplay", (Object[])new Object[]{"buff", buff.getLocalization(), "duration", Float.valueOf((float)area.debuffDuration / 1000.0f)})));
                });
            }
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"rangetip", (String)"range", (Object)Float.valueOf(area.currentRange)));
            if (!lines) continue;
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"line"));
        }
    }

    public static void addAreasStatTip(ItemStatTipList list, AphAreaList currentAreas, AphAreaList lastAreas, Attacker attacker, boolean forceAdd, @Nullable InventoryItem lastItem, @Nullable InventoryItem currentItem, @Nullable ToolItem toolItem) {
        AphAreaList.addAreasStatTip(list, currentAreas, lastAreas, attacker, forceAdd, lastItem, currentItem, toolItem, 2000);
    }

    public static void addAreasStatTip(ItemStatTipList list, AphAreaList currentAreas, AphAreaList lastAreas, Attacker attacker, boolean forceAdd, @Nullable InventoryItem lastItem, @Nullable InventoryItem currentItem, @Nullable ToolItem toolItem, int priority) {
        boolean multipleAreas = currentAreas.areas.size() > 1;
        LocalMessageStringItemStatTip lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
        list.add(priority, (ItemStatTip)lineTip);
        for (int i = 0; i < currentAreas.areas.size(); ++i) {
            AphArea lastArea;
            AphArea currentArea = currentAreas.areas.get(i);
            AphArea aphArea = lastArea = lastAreas == null ? null : lastAreas.areas.get(i);
            if (multipleAreas) {
                LocalMessageStringItemStatTip areasTip = new LocalMessageStringItemStatTip("itemtooltip", "areatip", "number", String.valueOf(i + 1));
                list.add(priority, (ItemStatTip)areasTip);
            }
            if (currentArea.areaTypes.contains((Object)AphAreaType.DAMAGE)) {
                float lastDamage;
                float damage = currentArea.getDamage().damage;
                float f = lastDamage = lastArea == null ? -1.0f : lastArea.getDamage().damage;
                if (damage > 0.0f || lastDamage > 0.0f || forceAdd) {
                    DoubleItemStatTip tip = currentArea.getDamage().type.getDamageTip((int)damage);
                    if (lastArea != null) {
                        tip.setCompareValue((double)lastDamage);
                    }
                    list.add(priority, (ItemStatTip)tip);
                }
            }
            if (currentArea.areaTypes.contains((Object)AphAreaType.HEALING)) {
                int healing = AphMagicHealing.getMagicHealing((Mob)attacker, null, currentArea.getHealing(), toolItem, currentItem);
                LocalMessageDoubleItemStatTip tip = new LocalMessageDoubleItemStatTip("itemtooltip", "magichealingtip", "health", (double)healing, 0);
                if (lastArea != null) {
                    int lastHealing = AphMagicHealing.getMagicHealing((Mob)attacker, null, lastArea.getHealing(), toolItem, lastItem);
                    tip.setCompareValue((double)lastHealing);
                }
                list.add(priority, (ItemStatTip)tip);
            }
            if (currentArea.areaTypes.contains((Object)AphAreaType.BUFF)) {
                Arrays.stream(currentArea.buffs).forEach(buffID -> {
                    Buff buff = BuffRegistry.getBuff((String)buffID);
                    LocalMessageStringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areabuff", "buff", Localization.translate((String)"itemtooltip", (String)"areabuffdisplay", (Object[])new Object[]{"buff", buff.getLocalization(), "duration", Float.valueOf((float)currentArea.buffDuration / 1000.0f)}));
                    list.add(priority, (ItemStatTip)tip);
                });
            }
            if (currentArea.areaTypes.contains((Object)AphAreaType.DEBUFF)) {
                Arrays.stream(currentArea.debuffs).forEach(debuffID -> {
                    Buff debuff = BuffRegistry.getBuff((String)debuffID);
                    LocalMessageStringItemStatTip tip = new LocalMessageStringItemStatTip("itemtooltip", "areadebuff", "buff", Localization.translate((String)"itemtooltip", (String)"areabuffdisplay", (Object[])new Object[]{"buff", debuff.getLocalization(), "duration", Float.valueOf((float)currentArea.buffDuration / 1000.0f)}));
                    list.add(priority, (ItemStatTip)tip);
                });
            }
            LocalMessageDoubleItemStatTip rangeTip = new LocalMessageDoubleItemStatTip("itemtooltip", "rangetip", "range", (double)currentArea.currentRange, 0);
            list.add(priority, (ItemStatTip)rangeTip);
            lineTip = new LocalMessageStringItemStatTip("itemtooltip", "line", "none", "none");
            list.add(priority, (ItemStatTip)lineTip);
        }
    }
}

