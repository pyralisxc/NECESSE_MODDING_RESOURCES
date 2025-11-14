/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.item.miscItem.PresentItem;
import necesse.inventory.item.miscItem.WrappingPaperItem;

public class WrappingPaperContainer
extends Container {
    public final StringCustomAction wrapButton;
    public final int CONTENT_SLOT;
    public final PlayerTempInventory contentInventory;
    public final ContainerSlot paperSlot;

    public WrappingPaperContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        int scrollSlotIndex = reader.getNextInt();
        Packet tempInvContent = reader.getNextContentPacket();
        this.contentInventory = client.playerMob.getInv().applyTempInventoryPacket(tempInvContent, m -> this.isClosed());
        this.CONTENT_SLOT = this.addSlot(new ContainerSlot(this.contentInventory, 0){

            @Override
            public String getItemInvalidError(InventoryItem item) {
                String superInvalid = super.getItemInvalidError(item);
                if (superInvalid != null) {
                    return superInvalid;
                }
                if (!(item.item instanceof PresentItem)) {
                    return null;
                }
                return "";
            }
        });
        this.addInventoryQuickTransfer(this.CONTENT_SLOT, this.CONTENT_SLOT);
        ContainerSlot slot = this.getSlot(scrollSlotIndex);
        if (slot != null && !slot.isClear() && slot.getItem().item instanceof WrappingPaperItem) {
            this.lockSlot(scrollSlotIndex);
            this.paperSlot = slot;
        } else {
            this.paperSlot = null;
        }
        this.wrapButton = this.registerAction(new StringCustomAction(){

            @Override
            protected void run(String value) {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    if (WrappingPaperContainer.this.canWrap() && WrappingPaperContainer.this.isValid(serverClient) && WrappingPaperContainer.this.paperSlot != null) {
                        InventoryItem paperItem = WrappingPaperContainer.this.paperSlot.getItem();
                        if (paperItem != null && paperItem.item instanceof WrappingPaperItem) {
                            InventoryItem presentItem = new InventoryItem(((WrappingPaperItem)paperItem.item).presentItemStringID);
                            PresentItem.setupPresent(presentItem, WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CONTENT_SLOT).getItem(), value);
                            InventoryItem draggingItem = WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CLIENT_DRAGGING_SLOT).getItem();
                            if (draggingItem != null) {
                                serverClient.playerMob.getInv().addItemsDropRemaining(draggingItem, "addback", client.playerMob, false, true);
                            }
                            WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CONTENT_SLOT).setItem(null);
                            WrappingPaperContainer.this.getSlot(WrappingPaperContainer.this.CLIENT_DRAGGING_SLOT).setItem(presentItem);
                            WrappingPaperContainer.this.paperSlot.setAmount(WrappingPaperContainer.this.paperSlot.getItemAmount() - 1);
                        }
                        WrappingPaperContainer.this.close();
                    } else {
                        WrappingPaperContainer.this.close();
                    }
                }
            }
        });
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        if (this.paperSlot == null) {
            return false;
        }
        if (this.paperSlot.isClear()) {
            return false;
        }
        InventoryItem item = this.paperSlot.getItem();
        return item.item instanceof WrappingPaperItem;
    }

    public boolean canWrap() {
        if (this.getSlot(this.CONTENT_SLOT).isClear()) {
            return false;
        }
        InventoryItem item = this.getSlot(this.CONTENT_SLOT).getItem();
        return !(item.item instanceof PresentItem);
    }

    public static Packet getContainerContent(ServerClient client, int slotIndex) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextInt(slotIndex);
        writer.putNextContentPacket(client.playerMob.getInv().getTempInventoryPacket(1));
        return p;
    }
}

