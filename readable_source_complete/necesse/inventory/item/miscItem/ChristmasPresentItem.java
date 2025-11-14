/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.ArrayList;
import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.matItem.MultiTextureMatItem;
import necesse.inventory.lootTable.LootTablePresets;

public class ChristmasPresentItem
extends MultiTextureMatItem {
    public ChristmasPresentItem() {
        super(4, 50, new String[0]);
        this.rarity = Item.Rarity.COMMON;
        this.setItemCategory("misc");
        this.incinerationTimeMillis = 30000;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "christmaspresenttip"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (container.getClient().isServer()) {
                ArrayList<InventoryItem> itemList = new ArrayList<InventoryItem>();
                LootTablePresets.christmasPresents.addItems(itemList, GameRandom.globalRandom, 1.0f, container.getClient());
                for (InventoryItem inventoryItem : itemList) {
                    container.getClient().playerMob.getInv().addItemsDropRemaining(inventoryItem, "addback", container.getClient().playerMob, true, false);
                }
            }
            slot.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                slot.setItem(null);
            }
            return new ContainerActionResult(154617259 * (item.getAmount() + GameRandom.prime(4)));
        };
    }
}

