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
import necesse.inventory.container.item.CraftingGuideContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class CraftingGuideBookItem
extends Item {
    public CraftingGuideBookItem() {
        super(1);
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
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
                    PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.CRAFTING_GUIDE_CONTAINER, CraftingGuideContainer.getContainerContent(client, playerSlot));
                    ContainerRegistry.openAndSendContainer(client, p);
                }
                return new ContainerActionResult(1328013989);
            }
            return new ContainerActionResult(60840742, Localization.translate("itemtooltip", "rclickinvopenerror"));
        };
    }
}

