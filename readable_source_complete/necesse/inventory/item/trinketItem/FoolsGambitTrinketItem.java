/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.trinketItem;

import java.util.Arrays;
import necesse.engine.localization.Localization;
import necesse.engine.registries.BuffRegistry;
import necesse.entity.mobs.buffs.staticBuffs.armorBuffs.trinketBuffs.TrinketBuff;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.trinketItem.TrinketItem;
import necesse.inventory.lootTable.presets.TrinketsLootTable;

public class FoolsGambitTrinketItem
extends TrinketItem {
    private String[] buffStringIDs;

    public FoolsGambitTrinketItem(Item.Rarity rarity, String[] buffStringIDs, int enchantCost) {
        super(rarity, enchantCost, TrinketsLootTable.trinkets);
        this.buffStringIDs = buffStringIDs;
    }

    public FoolsGambitTrinketItem(Item.Rarity rarity, String buffStringID, int enchantCost) {
        this(rarity, new String[]{buffStringID}, enchantCost);
    }

    @Override
    public TrinketBuff[] getBuffs(InventoryItem item) {
        return (TrinketBuff[])Arrays.stream(this.buffStringIDs).map(s -> (TrinketBuff)BuffRegistry.getBuff(s)).toArray(TrinketBuff[]::new);
    }

    @Override
    public void addTrinketAbilityHotkeyTooltip(ListGameTooltips tooltips, InventoryItem item) {
    }

    @Override
    public boolean isAbilityTrinket(InventoryItem item) {
        return true;
    }

    @Override
    public String getInvalidInSlotError(Container container, ContainerSlot slot, InventoryItem item) {
        String superInvalidError = super.getInvalidInSlotError(container, slot, item);
        if (superInvalidError != null) {
            return superInvalidError;
        }
        if (slot.getContainerIndex() == container.CLIENT_TRINKET_ABILITY_SLOT) {
            return null;
        }
        return Localization.translate("itemtooltip", "foolsgambiterrortip");
    }
}

