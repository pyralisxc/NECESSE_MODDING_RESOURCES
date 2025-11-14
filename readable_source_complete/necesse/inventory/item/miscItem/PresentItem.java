/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventoryItem;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class PresentItem
extends Item {
    public PresentItem() {
        super(1);
        this.rarity = Item.Rarity.COMMON;
        this.setItemCategory("misc");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        GNDItemMap gndData = item.getGndData();
        String message = gndData.getString("message");
        if (message != null && !message.isEmpty()) {
            tooltips.add(message, 292);
        }
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            GNDItemMap gndData = item.getGndData();
            GNDItem content = gndData.getItem("content");
            InventoryItem contentItem = null;
            if (content instanceof GNDItemInventoryItem) {
                contentItem = ((GNDItemInventoryItem)content).invItem;
            }
            if (item.getAmount() > 1) {
                if (contentItem != null && container.getClient().isServer()) {
                    container.getClient().playerMob.getInv().addItemsDropRemaining(contentItem, "addback", container.getClient().playerMob, false, false);
                }
                slot.setAmount(item.getAmount() - 1);
                return new ContainerActionResult(60793022 * (item.getAmount() + GameRandom.prime(4)));
            }
            slot.setItem(contentItem);
            return new ContainerActionResult(154456235 * (item.getAmount() + GameRandom.prime(4)));
        };
    }

    @Override
    public float getBrokerValue(InventoryItem item) {
        InventoryItem contentItem;
        GNDItemMap gndData = item.getGndData();
        GNDItem content = gndData.getItem("content");
        if (content instanceof GNDItemInventoryItem && (contentItem = ((GNDItemInventoryItem)content).invItem) != null) {
            return super.getBrokerValue(item) + contentItem.getBrokerValue();
        }
        return super.getBrokerValue(item);
    }

    public static void setupPresent(InventoryItem item, InventoryItem content, String message) {
        GNDItemMap gndData = item.getGndData();
        gndData.setItem("content", (GNDItem)new GNDItemInventoryItem(content));
        gndData.setString("message", message);
    }
}

