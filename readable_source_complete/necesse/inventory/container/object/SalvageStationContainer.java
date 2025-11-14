/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.object;

import java.util.ArrayList;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOpenContainer;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ContainerRegistry;
import necesse.entity.objectEntity.SalvageStationObjectEntity;
import necesse.inventory.InventoryItem;
import necesse.inventory.container.Container;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.slots.SalvagableItemContainerSlot;
import necesse.inventory.item.upgradeUtils.SalvageableItem;
import necesse.level.maps.Level;

public class SalvageStationContainer
extends Container {
    public int SALVAGE_INVENTORY_START = -1;
    public int SALVAGE_INVENTORY_END = -1;
    public final SalvageStationObjectEntity salvageEntity;
    public final EmptyCustomAction salvageButton;

    public SalvageStationContainer(NetworkClient client, int uniqueSeed, SalvageStationObjectEntity salvageEntity, PacketReader reader) {
        super(client, uniqueSeed);
        this.salvageEntity = salvageEntity;
        for (int i = 0; i < salvageEntity.inventory.getSize(); ++i) {
            int index = this.addSlot(new SalvagableItemContainerSlot(salvageEntity.inventory, i));
            if (this.SALVAGE_INVENTORY_START == -1) {
                this.SALVAGE_INVENTORY_START = index;
            }
            if (this.SALVAGE_INVENTORY_END == -1) {
                this.SALVAGE_INVENTORY_END = index;
            }
            this.SALVAGE_INVENTORY_START = Math.min(this.SALVAGE_INVENTORY_START, index);
            this.SALVAGE_INVENTORY_END = Math.max(this.SALVAGE_INVENTORY_END, index);
        }
        this.addInventoryQuickTransfer(this.SALVAGE_INVENTORY_START, this.SALVAGE_INVENTORY_END);
        this.salvageButton = this.registerAction(new EmptyCustomAction(){

            @Override
            protected void run() {
                SalvageStationContainer.this.salvageItems();
            }
        });
    }

    public ArrayList<InventoryItem> getCurrentSalvageRewards(boolean clearSlotsAndAddStat) {
        ArrayList<InventoryItem> rewards = new ArrayList<InventoryItem>();
        for (int i = 0; i < this.salvageEntity.inventory.getSize(); ++i) {
            InventoryItem item = this.salvageEntity.inventory.getItem(i);
            if (item == null || !(item.item instanceof SalvageableItem) || ((SalvageableItem)((Object)item.item)).getCanBeSalvagedError(item) != null) continue;
            for (InventoryItem reward : ((SalvageableItem)((Object)item.item)).getSalvageRewards(item)) {
                reward.combineOrAddToList(this.salvageEntity.getLevel(), null, rewards, "add");
            }
            if (!clearSlotsAndAddStat) continue;
            if (this.client.isServer()) {
                this.client.getServerClient().newStats.items_salvaged.increment(item.getAmount());
            }
            this.salvageEntity.inventory.clearSlot(i);
        }
        return rewards;
    }

    public void salvageItems() {
        ArrayList<InventoryItem> rewards = this.getCurrentSalvageRewards(true);
        if (rewards != null) {
            for (InventoryItem reward : rewards) {
                this.client.playerMob.getInv().addItemsDropRemaining(reward, "salvage", this.client.playerMob, !this.client.isServer(), false, true);
            }
        }
    }

    @Override
    public boolean isValid(ServerClient client) {
        if (!super.isValid(client)) {
            return false;
        }
        return !this.salvageEntity.removed() && this.salvageEntity.getLevelObject().isInInteractRange(client.playerMob);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY, Packet extraContent) {
        if (!level.isServer()) {
            throw new IllegalStateException("Level must be a server level");
        }
        Packet packet = new Packet();
        PacketWriter writer = new PacketWriter(packet);
        if (extraContent != null) {
            writer.putNextContentPacket(extraContent);
        }
        PacketOpenContainer p = PacketOpenContainer.LevelObject(containerID, tileX, tileY, packet);
        ContainerRegistry.openAndSendContainer(client, p);
    }

    public static void openAndSendContainer(int containerID, ServerClient client, Level level, int tileX, int tileY) {
        SalvageStationContainer.openAndSendContainer(containerID, client, level, tileX, tileY, null);
    }
}

