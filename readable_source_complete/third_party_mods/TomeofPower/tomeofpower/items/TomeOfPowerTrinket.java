/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.message.GameMessage
 *  necesse.engine.localization.message.LocalMessage
 *  necesse.engine.registries.BuffRegistry
 *  necesse.entity.mobs.buffs.staticBuffs.Buff
 *  necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff
 *  necesse.gfx.gameTexture.GameTexture
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 */
package tomeofpower.items;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.Buff;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import tomeofpower.config.TomeConfig;
import tomeofpower.items.base.BaseEnchantmentTrinket;

public class TomeOfPowerTrinket
extends BaseEnchantmentTrinket {
    public TomeOfPowerTrinket() {
        super(Item.Rarity.EPIC, 100, TomeConfig.TRINKET_SLOT_COUNT);
    }

    @Override
    public GameMessage getLocalization(InventoryItem item) {
        return new LocalMessage("item", "tomeofpower");
    }

    @Override
    protected String getTooltipKey() {
        return "tomeofpower";
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        Buff buffObj = BuffRegistry.getBuff((String)"tomeofpowerbuff");
        if (buffObj instanceof TrinketBuff) {
            return new TrinketBuff[]{(TrinketBuff)buffObj};
        }
        return new TrinketBuff[0];
    }

    public GameTexture getTexture() {
        return GameTexture.fromFile((String)"items/tomeofpower");
    }
}

