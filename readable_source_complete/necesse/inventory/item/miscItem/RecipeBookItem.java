/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.localization.Localization;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.RecipeBookContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class RecipeBookItem
extends Item {
    public RecipeBookItem() {
        super(1);
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "recipebooktip"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            PlayerInventorySlot playerSlot = null;
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().main, slot.getInventorySlot());
            }
            if (slot.getInventory() == container.getClient().playerMob.getInv().cloud) {
                playerSlot = new PlayerInventorySlot(container.getClient().playerMob.getInv().cloud, slot.getInventorySlot());
            }
            if (playerSlot != null) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.RECIPE_BOOK_CONTAINER, RecipeBookContainer.getContainerContent(client, playerSlot));
                    ContainerRegistry.openAndSendContainer(client, p);
                }
                return new ContainerActionResult(-551066694);
            }
            return new ContainerActionResult(3784210, Localization.translate("itemtooltip", "rclickinvopenerror"));
        };
    }
}

