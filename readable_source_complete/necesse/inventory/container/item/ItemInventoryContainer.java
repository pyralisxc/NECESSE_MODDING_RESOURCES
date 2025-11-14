/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.Container;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.InternalInventoryItemContainerSlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;

public class ItemInventoryContainer
extends Container {
    public StringCustomAction renameButton;
    public final BooleanCustomAction setPickupDisabled;
    public final EmptyCustomAction quickStackButton;
    public final EmptyCustomAction transferAll;
    public final EmptyCustomAction restockButton;
    public final EmptyCustomAction lootButton;
    public final EmptyCustomAction sortButton;
    public final Inventory inventory;
    public final int itemID;
    public final InternalInventoryItemInterface inventoryItem;
    public final PlayerInventorySlot inventoryItemSlot;
    private InventoryItem item;
    public int INVENTORY_START = -1;
    public int INVENTORY_END = -1;

    public ItemInventoryContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        this.itemID = reader.getNextShortUnsigned();
        int itemInventoryID = reader.getNextInt();
        int itemInventorySlot = reader.getNextInt();
        this.inventoryItemSlot = new PlayerInventorySlot(itemInventoryID, itemInventorySlot);
        this.item = this.inventoryItemSlot.getItem(client.playerMob.getInv());
        if (this.item != null && this.item.item.getID() == this.itemID && this.item.item instanceof InternalInventoryItemInterface) {
            this.inventoryItem = (InternalInventoryItemInterface)((Object)this.item.item);
            this.lockSlot(this.inventoryItemSlot);
            InternalInventoryItemInterface internalInventoryItemInterface = (InternalInventoryItemInterface)((Object)this.item.item);
            this.inventory = internalInventoryItemInterface.getInternalInventory(this.item);
            for (int i = 0; i < this.inventory.getSize(); ++i) {
                int index = this.addSlot(this.getItemContainerSlot(this.inventory, i, internalInventoryItemInterface));
                if (this.INVENTORY_START == -1) {
                    this.INVENTORY_START = index;
                }
                if (this.INVENTORY_END == -1) {
                    this.INVENTORY_END = index;
                }
                this.INVENTORY_START = Math.min(this.INVENTORY_START, index);
                this.INVENTORY_END = Math.max(this.INVENTORY_END, index);
            }
            this.addInventoryQuickTransfer(this.INVENTORY_START, this.INVENTORY_END);
        } else {
            this.inventoryItem = null;
            this.inventory = null;
        }
        this.renameButton = this.registerAction(new StringCustomAction(){

            @Override
            protected void run(String value) {
                if (ItemInventoryContainer.this.inventoryItem != null && ItemInventoryContainer.this.item != null && ItemInventoryContainer.this.inventoryItem.canChangePouchName()) {
                    ItemInventoryContainer.this.inventoryItem.setPouchName(ItemInventoryContainer.this.item, value);
                    if (client.isServer()) {
                        ItemInventoryContainer.this.inventoryItemSlot.markDirty(client.playerMob.getInv());
                    }
                }
            }
        });
        this.setPickupDisabled = this.registerAction(new BooleanCustomAction(){

            @Override
            protected void run(boolean value) {
                if (ItemInventoryContainer.this.inventoryItem != null && ItemInventoryContainer.this.item != null && ItemInventoryContainer.this.inventoryItem.canDisablePickup()) {
                    ItemInventoryContainer.this.inventoryItem.setPouchPickupDisabled(ItemInventoryContainer.this.item, value);
                    if (client.isServer()) {
                        ItemInventoryContainer.this.inventoryItemSlot.markDirty(client.playerMob.getInv());
                    }
                }
            }
        });
        this.quickStackButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(ItemInventoryContainer.this.inventory)));
                ItemInventoryContainer.this.quickStackToInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.transferAll = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = ItemInventoryContainer.this.CLIENT_INVENTORY_START; i <= ItemInventoryContainer.this.CLIENT_INVENTORY_END; ++i) {
                    if (ItemInventoryContainer.this.getSlot(i).isItemLocked()) continue;
                    ItemInventoryContainer.this.transferToSlots(ItemInventoryContainer.this.getSlot(i), ItemInventoryContainer.this.INVENTORY_START, ItemInventoryContainer.this.INVENTORY_END, "transferall");
                }
            }
        });
        this.restockButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(ItemInventoryContainer.this.inventory)));
                ItemInventoryContainer.this.restockFromInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.lootButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = ItemInventoryContainer.this.INVENTORY_START; i <= ItemInventoryContainer.this.INVENTORY_END; ++i) {
                    if (ItemInventoryContainer.this.getSlot(i).isItemLocked()) continue;
                    ItemInventoryContainer.this.transferToSlots(ItemInventoryContainer.this.getSlot(i), Arrays.asList(new SlotIndexRange(ItemInventoryContainer.this.CLIENT_HOTBAR_START, ItemInventoryContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(ItemInventoryContainer.this.CLIENT_INVENTORY_START, ItemInventoryContainer.this.CLIENT_INVENTORY_END)), "lootallpouch");
                }
            }
        });
        this.sortButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ItemInventoryContainer.this.inventory.sortItems(client.playerMob.getLevel(), client.playerMob);
            }
        });
    }

    public ContainerSlot getItemContainerSlot(Inventory inventory, int slot, InternalInventoryItemInterface internalInventoryItemInterface) {
        return new InternalInventoryItemContainerSlot(inventory, slot, internalInventoryItemInterface);
    }

    public InventoryItem getInventoryItem() {
        return this.item;
    }

    @Override
    public void lootAllControlPressed() {
        this.lootButton.runAndSend();
    }

    @Override
    public void sortInventoryControlPressed() {
        this.sortButton.runAndSend();
    }

    @Override
    public void quickStackControlPressed() {
        this.quickStackButton.runAndSend();
    }

    @Override
    public void tick() {
        InventoryItem item;
        super.tick();
        if (this.client.isClient()) {
            if (this.inventory == null) {
                this.client.getClientClient().getClient().closeContainer(true);
                return;
            }
            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (this.item != item && item != null) {
                if (!(item.item instanceof InternalInventoryItemInterface)) {
                    this.client.getClientClient().getClient().closeContainer(true);
                    return;
                }
                this.inventory.override(((InternalInventoryItemInterface)((Object)item.item)).getInternalInventory(item));
                this.item = item;
            }
        }
        if (this.inventory.isDirty()) {
            item = this.inventoryItemSlot.getItem(this.client.playerMob.getInv());
            if (item != null) {
                ((InternalInventoryItemInterface)((Object)item.item)).saveInternalInventory(item, this.inventory);
            }
            this.inventory.clean();
            this.inventoryItemSlot.markDirty(this.client.playerMob.getInv());
        }
    }

    @Override
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

    public static Packet getContainerContent(InternalInventoryItemInterface item, PlayerInventorySlot inventorySlot) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextShortUnsigned(((Item)((Object)item)).getID());
        writer.putNextInt(inventorySlot.inventoryID);
        writer.putNextInt(inventorySlot.slot);
        return p;
    }
}

