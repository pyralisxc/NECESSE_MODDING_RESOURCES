/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.item;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemString;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.StringCustomAction;
import necesse.inventory.container.slots.ContainerSlot;

public class RenameItemContainer
extends Container {
    public static int MAX_NAME_LENGTH = 40;
    public final StringCustomAction renameButton;
    public final ContainerSlot itemSlot;

    public RenameItemContainer(final NetworkClient client, int uniqueSeed, Packet content) {
        super(client, uniqueSeed);
        PacketReader reader = new PacketReader(content);
        int itemSlotIndex = reader.getNextInt();
        ContainerSlot slot = this.getSlot(itemSlotIndex);
        if (slot != null && !slot.isClear()) {
            this.lockSlot(itemSlotIndex);
            this.itemSlot = slot;
        } else {
            this.itemSlot = null;
        }
        this.renameButton = this.registerAction(new StringCustomAction(){

            @Override
            protected void run(String value) {
                if (client.isServer()) {
                    ServerClient serverClient = client.getServerClient();
                    if (RenameItemContainer.this.canRename(value) && RenameItemContainer.this.isValid(serverClient) && RenameItemContainer.this.itemSlot != null) {
                        InventoryItem item = RenameItemContainer.this.itemSlot.getItem();
                        if (item != null) {
                            if (value.isEmpty()) {
                                item.getGndData().setItem("name", null);
                            } else {
                                item.getGndData().setItem("name", (GNDItem)new GNDItemString(value));
                            }
                            RenameItemContainer.this.itemSlot.markDirty();
                        }
                        RenameItemContainer.this.close();
                    } else {
                        RenameItemContainer.this.close();
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
        if (this.itemSlot == null) {
            return false;
        }
        return !this.itemSlot.isClear();
    }

    public boolean canRename(String name) {
        return name.length() <= MAX_NAME_LENGTH;
    }

    public static Packet getContainerContent(int itemSlotIndex) {
        Packet p = new Packet();
        PacketWriter writer = new PacketWriter(p);
        writer.putNextInt(itemSlotIndex);
        return p;
    }
}

