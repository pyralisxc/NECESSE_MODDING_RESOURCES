/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs;

import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffEventSubscriber;
import necesse.entity.mobs.buffs.BuffManager;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class SummonersBestiaryTrinketBuff
extends TrinketBuff {
    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "summonersbestiarytip"), 400);
        return tooltips;
    }

    @Override
    public void init(ActiveBuff buff, BuffEventSubscriber eventSubscriber) {
    }

    @Override
    public void clientTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    @Override
    public void serverTick(ActiveBuff buff) {
        this.updateModifiers(buff);
    }

    public void updateModifiers(ActiveBuff buff) {
        int uniqueFollowerCount;
        if (buff.owner instanceof PlayerMob && (uniqueFollowerCount = ((PlayerMob)buff.owner).serverFollowersManager.getUniqueFollowerCount()) > 0) {
            BuffManager buffManager = buff.owner.buffManager;
            if (buffManager.hasBuff(BuffRegistry.SUMMONERS_BESTIARY)) {
                buffManager.removeBuff(BuffRegistry.SUMMONERS_BESTIARY, true);
            }
            ActiveBuff ab = new ActiveBuff(BuffRegistry.SUMMONERS_BESTIARY, buff.owner, 1000, null);
            ab.setStacks(uniqueFollowerCount, 100, null);
            buffManager.addBuff(ab, true);
        }
    }
}

