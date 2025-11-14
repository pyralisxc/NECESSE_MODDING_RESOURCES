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
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.WrappingPaperContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;

public class WrappingPaperItem
extends Item {
    public String presentItemStringID;

    public WrappingPaperItem(String presentItemStringID) {
        super(100);
        this.presentItemStringID = presentItemStringID;
        this.rarity = Item.Rarity.COMMON;
        this.setItemCategory("misc");
        this.addGlobalIngredient("anywrappingpaper");
    }

    @Override
    public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "wrappingpapertip"));
        tooltips.add(Localization.translate("itemtooltip", "rclickinvopentip"));
        return tooltips;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            if (slot.getInventory() == container.getClient().playerMob.getInv().main) {
                if (container.getClient().isServer()) {
                    ServerClient client = container.getClient().getServerClient();
                    PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.WRAPPING_PAPER_CONTAINER, WrappingPaperContainer.getContainerContent(client, slotIndex));
                    ContainerRegistry.openAndSendContainer(client, p);
                }
                return new ContainerActionResult(181503442);
            }
            return new ContainerActionResult(188304063, Localization.translate("itemtooltip", "rclickinvopenerror"));
        };
    }
}

