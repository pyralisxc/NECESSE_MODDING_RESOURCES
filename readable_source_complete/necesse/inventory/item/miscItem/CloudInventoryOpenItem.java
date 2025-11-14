/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import java.util.function.Supplier;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.ContainerActionResult;
import necesse.inventory.container.item.CloudItemContainer;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class CloudInventoryOpenItem
extends Item {
    private final boolean easyInsert;
    private final int startSlot;
    private final int endSlot;

    public CloudInventoryOpenItem(boolean easyInsert, int startSlot, int endSlot) {
        super(1);
        this.easyInsert = easyInsert;
        this.startSlot = startSlot;
        this.endSlot = endSlot;
        this.worldDrawSize = 32;
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        ServerClient client;
        if (level.isServer() && attackerMob.isPlayer && (!((client = ((PlayerMob)attackerMob).getServerClient()).getContainer() instanceof CloudItemContainer) || ((CloudItemContainer)client.getContainer()).itemID != this.getID())) {
            PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.CLOUD_INVENTORY_CONTAINER, CloudItemContainer.getContainerContent(client, this, this.startSlot, this.endSlot));
            ContainerRegistry.openAndSendContainer(client, p);
        }
        return item;
    }

    @Override
    public Supplier<ContainerActionResult> getInventoryRightClickAction(Container container, InventoryItem item, int slotIndex, ContainerSlot slot) {
        return () -> {
            ServerClient client;
            if (container.getClient().isServer() && (!((client = container.getClient().getServerClient()).getContainer() instanceof CloudItemContainer) || ((CloudItemContainer)client.getContainer()).itemID != this.getID())) {
                PacketOpenContainer p = new PacketOpenContainer(ContainerRegistry.CLOUD_INVENTORY_CONTAINER, CloudItemContainer.getContainerContent(client, this, this.startSlot, this.endSlot));
                ContainerRegistry.openAndSendContainer(client, p);
            }
            return new ContainerActionResult(208675834);
        };
    }

    @Override
    public boolean canCombineItem(Level level, PlayerMob player, InventoryItem me, InventoryItem them, String purpose) {
        if (them == null) {
            return false;
        }
        return this.isSameItem(level, me, them, purpose) || this.easyInsert && player != null && (purpose.equals("leftclick") || purpose.equals("leftclickinv") || purpose.equals("rightclick"));
    }

    @Override
    public boolean onCombine(Level level, PlayerMob player, Inventory myInventory, int mySlot, InventoryItem me, InventoryItem other, int maxStackSize, int amount, boolean combineIsNew, String purpose, InventoryAddConsumer addConsumer) {
        if (this.easyInsert && player != null && (purpose.equals("leftclick") || purpose.equals("leftclickinv") || purpose.equals("rightclick"))) {
            PlayerInventory internalInventory = player.getInv().cloud;
            int startAmount = Math.min(amount, other.getAmount());
            InventoryItem copy = other.copy(startAmount);
            internalInventory.addItem(level, player, copy, "pouchinsert", addConsumer);
            if (copy.getAmount() != startAmount) {
                int diff = startAmount - copy.getAmount();
                other.setAmount(other.getAmount() - diff);
                return true;
            }
            return false;
        }
        return super.onCombine(level, player, myInventory, mySlot, me, other, maxStackSize, amount, combineIsNew, purpose, addConsumer);
    }
}

