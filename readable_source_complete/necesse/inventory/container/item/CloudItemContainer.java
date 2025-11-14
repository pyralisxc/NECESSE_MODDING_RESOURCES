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
import necesse.inventory.InventoryRange;
import necesse.inventory.PlayerInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.SlotIndexRange;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.CloudContainerSlot;
import necesse.inventory.item.Item;

public class CloudItemContainer
extends Container {
    public final EmptyCustomAction quickStackButton;
    public final EmptyCustomAction transferAll;
    public final EmptyCustomAction restockButton;
    public final EmptyCustomAction lootButton;
    public final EmptyCustomAction sortButton;
    public final int itemID;
    public final int startSlot;
    public final int endSlot;
    public int CLOUD_START = -1;
    public int CLOUD_END = -1;

    public CloudItemContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        this.itemID = reader.getNextShortUnsigned();
        PlayerInventory inventory = client.playerMob.getInv().cloud;
        this.startSlot = Math.max(0, reader.getNextShortUnsigned());
        this.endSlot = Math.max(reader.getNextShortUnsigned(), this.startSlot);
        if (inventory.getSize() <= this.endSlot) {
            inventory.changeSize(this.endSlot + 1);
        }
        for (int i = this.startSlot; i <= this.endSlot; ++i) {
            int index = this.addSlot(new CloudContainerSlot(inventory, i, this.itemID));
            if (this.CLOUD_START == -1) {
                this.CLOUD_START = index;
            }
            if (this.CLOUD_END == -1) {
                this.CLOUD_END = index;
            }
            this.CLOUD_START = Math.min(this.CLOUD_START, index);
            this.CLOUD_END = Math.max(this.CLOUD_END, index);
        }
        this.addInventoryQuickTransfer(this.CLOUD_START, this.CLOUD_END);
        this.quickStackButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(client.playerMob.getInv().cloud, CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot)));
                CloudItemContainer.this.quickStackToInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.transferAll = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = CloudItemContainer.this.CLIENT_INVENTORY_START; i <= CloudItemContainer.this.CLIENT_INVENTORY_END; ++i) {
                    if (CloudItemContainer.this.getSlot(i).isItemLocked()) continue;
                    CloudItemContainer.this.transferToSlots(CloudItemContainer.this.getSlot(i), CloudItemContainer.this.CLOUD_START, CloudItemContainer.this.CLOUD_END, "transferall");
                }
            }
        });
        this.restockButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                ArrayList<InventoryRange> targets = new ArrayList<InventoryRange>(Collections.singleton(new InventoryRange(client.playerMob.getInv().cloud, CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot)));
                CloudItemContainer.this.restockFromInventories(targets, client.playerMob.getInv().main);
            }
        });
        this.lootButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                for (int i = CloudItemContainer.this.CLOUD_START; i <= CloudItemContainer.this.CLOUD_END; ++i) {
                    if (CloudItemContainer.this.getSlot(i).isItemLocked()) continue;
                    CloudItemContainer.this.transferToSlots(CloudItemContainer.this.getSlot(i), Arrays.asList(new SlotIndexRange(CloudItemContainer.this.CLIENT_HOTBAR_START, CloudItemContainer.this.CLIENT_HOTBAR_END), new SlotIndexRange(CloudItemContainer.this.CLIENT_INVENTORY_START, CloudItemContainer.this.CLIENT_INVENTORY_END)), "lootall");
                }
            }
        });
        this.sortButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                client.playerMob.getInv().cloud.sortItems(client.playerMob.getLevel(), client.playerMob, CloudItemContainer.this.startSlot, CloudItemContainer.this.endSlot);
            }
        });
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

    public static Packet getContainerContent(ServerClient client, Item item, int startSlot, int endSlot) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextShortUnsigned(item.getID());
        writer.putNextShortUnsigned(startSlot);
        writer.putNextShortUnsigned(endSlot);
        return p;
    }
}

