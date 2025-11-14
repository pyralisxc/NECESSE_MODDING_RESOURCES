/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.network.NetworkClient
 *  necesse.engine.network.Packet
 *  necesse.engine.network.PacketReader
 *  necesse.engine.network.server.ServerClient
 *  necesse.entity.mobs.PlayerMob
 *  necesse.inventory.Inventory
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.PlayerInventorySlot
 *  necesse.inventory.container.Container
 *  necesse.inventory.container.slots.ContainerSlot
 *  necesse.inventory.item.miscItem.InternalInventoryItemInterface
 */
package tomeofpower.containers.trinket;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import tomeofpower.containers.trinket.LimitedEnchantmentSlot;

public class TrinketInventoryContainer
extends Container {
    private int INVENTORY_START = 0;
    private int INVENTORY_END = 0;
    private int[] trinketSlotIndices = new int[0];
    private Inventory inventory;
    private int itemID;
    private InternalInventoryItemInterface inventoryItem;
    private PlayerInventorySlot inventoryItemSlot;
    private InventoryItem item;

    public TrinketInventoryContainer(NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        this.init(content, client.playerMob);
    }

    private void init(Packet content, PlayerMob player) {
        PacketReader reader = new PacketReader(content);
        this.itemID = reader.getNextShortUnsigned();
        int itemInventoryID = reader.getNextInt();
        int itemInventorySlot = reader.getNextInt();
        this.inventoryItemSlot = new PlayerInventorySlot(itemInventoryID, itemInventorySlot);
        this.item = this.inventoryItemSlot.getItem(player.getInv());
        if (this.item != null && this.item.item.getID() == this.itemID && this.item.item instanceof InternalInventoryItemInterface) {
            this.inventoryItem = (InternalInventoryItemInterface)this.item.item;
            this.lockSlot(this.inventoryItemSlot);
            this.inventory = this.inventoryItem.getInternalInventory(this.item);
            this.trinketSlotIndices = new int[this.inventory.getSize()];
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                int index;
                ContainerSlot slot = this.getItemContainerSlot(this.inventory, i, this.inventoryItem);
                this.trinketSlotIndices[i] = index = this.addSlot(slot);
                if (i == 0) {
                    this.INVENTORY_START = index;
                }
                if (i != this.inventory.getSize() - 1) continue;
                this.INVENTORY_END = index;
            }
            this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
        }
    }

    protected ContainerSlot getItemContainerSlot(Inventory inventory, int index, InternalInventoryItemInterface internalInventoryItemInterface) {
        return new LimitedEnchantmentSlot(inventory, index, internalInventoryItemInterface);
    }

    public void tick() {
        super.tick();
        if (this.client.isClient()) {
            if (this.inventory == null) {
                this.client.getClientClient().getClient().closeContainer(false);
                return;
            }
            InventoryItem currentItem = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (currentItem == null || currentItem.item.getID() != this.itemID) {
                this.client.getClientClient().getClient().closeContainer(false);
                return;
            }
            if (this.item != currentItem) {
                this.inventory.override(this.inventoryItem.getInternalInventory(currentItem));
                this.item = currentItem;
            }
        } else {
            InventoryItem currentItem = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (currentItem == null || currentItem.item.getID() != this.itemID) {
                this.client.getServerClient().closeContainer(false);
            } else if (this.inventory.isDirty()) {
                this.inventoryItem.saveInternalInventory(currentItem, this.inventory);
                this.inventory.clean();
                this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
            }
        }
    }

    public InventoryItem getInventoryItem() {
        return this.item;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public int getTrinketSlotIndex(int trinketSlot) {
        if (trinketSlot >= 0 && trinketSlot < this.trinketSlotIndices.length) {
            return this.trinketSlotIndices[trinketSlot];
        }
        return trinketSlot;
    }

    public int getInventorySize() {
        return this.inventory != null ? this.inventory.getSize() : 0;
    }

    public void onClose() {
        InventoryItem currentItem;
        super.onClose();
        if (!this.client.isClient() && this.inventory != null && this.inventoryItem != null && (currentItem = this.inventoryItemSlot.getItem(this.client.playerMob.getInv())) != null) {
            this.inventoryItem.saveInternalInventory(currentItem, this.inventory);
            this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
        }
    }

    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (this.inventoryItemSlot == null) {
            return false;
        }
        InventoryItem invItem = this.inventoryItemSlot.getItem(client.playerMob.getInv());
        return invItem != null && invItem.item.getID() == this.itemID && invItem.item instanceof InternalInventoryItemInterface;
    }
}

