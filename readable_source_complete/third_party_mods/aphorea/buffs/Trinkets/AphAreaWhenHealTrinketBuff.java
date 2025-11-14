/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.buffs.BuffEventSubscriber
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 *  org.jetbrains.annotations.Nullable
 */
package aphorea.buffs.Trinkets;

import aphorea.utils.area.AphAreaList;
import aphorea.utils.magichealing.AphMagicHealingBuff;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;
import necesse.inventory.item.trinketItem.TrinketItem;
import org.jetbrains.annotations.Nullable;

public abstract class AphAreaWhenHealTrinketBuff
extends TrinketBuff
implements AphMagicHealingBuff {
    public int healingToArea;
    public Map<String, Integer> healingDone = new HashMap<String, Integer>();
    public AphAreaList areaList;

    public AphAreaWhenHealTrinketBuff(int healingToArea, AphAreaList areaList) {
        this.healingToArea = healingToArea;
        this.areaList = areaList;
    }

    public void init(ActiveBuff activeBuff, BuffEventSubscriber buffEventSubscriber) {
    }

    @Override
    public void onMagicalHealing(ActiveBuff activeBuff, Mob healer, Mob target, int healing, int realHealing, @Nullable ToolItem toolItem, @Nullable InventoryItem item) {
        if (healer.isServer()) {
            String playerName = ((PlayerMob)healer).playerName;
            int thisHealingDone = this.healingDone.getOrDefault(playerName, 0) + realHealing;
            if (thisHealingDone >= this.healingToArea) {
                thisHealingDone = 0;
                this.areaList.execute(healer, true);
            }
            this.healingDone.put(playerName, thisHealingDone);
        }
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        this.areaList.addAreasToolTip(tooltips, (Attacker)perspective, true, null, null);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"areawhenheal", (String)"magichealing", (Object)this.healingToArea));
        return tooltips;
    }
}

