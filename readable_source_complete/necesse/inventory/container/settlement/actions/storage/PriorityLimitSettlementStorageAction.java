/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.storage;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStoragePriorityLimitEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class PriorityLimitSettlementStorageAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public PriorityLimitSettlementStorageAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSendPriority(int tileX, int tileY, int priority) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(true);
        writer.putNextInt(priority);
        this.runAndSendAction(content);
    }

    public void runAndSendLimit(int tileX, int tileY, ItemCategoriesFilter.ItemLimitMode mode, int limit) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(false);
        writer.putNextEnum(mode);
        writer.putNextInt(limit);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        SettlementStorageHandler handler;
        int tileX = reader.getNextInt();
        int tileY = reader.getNextInt();
        boolean isPriority = reader.getNextBoolean();
        if (isPriority) {
            int priority = reader.getNextInt();
            handler = (data, client, storage) -> {
                storage.priority = priority;
                new SettlementStoragePriorityLimitEvent(data, tileX, tileY, priority).applyAndSendToClientsAtExcept(client);
            };
        } else {
            ItemCategoriesFilter.ItemLimitMode mode = reader.getNextEnum(ItemCategoriesFilter.ItemLimitMode.class);
            int limit = reader.getNextInt();
            handler = (data, client, storage) -> {
                storage.filter.limitMode = mode;
                storage.filter.maxAmount = limit;
                new SettlementStoragePriorityLimitEvent(data, tileX, tileY, mode, limit).applyAndSendToClientsAtExcept(client);
            };
        }
        if (this.container.client.isServer()) {
            ServerClient client2 = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client2)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client2);
                    return;
                }
                SettlementInventory storage2 = serverData.storageManager.getStorage(tileX, tileY);
                if (storage2 != null) {
                    handler.accept(serverData, client2, storage2);
                } else {
                    new SettlementSingleStorageEvent(serverData, tileX, tileY).applyAndSendToClient(client2);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client2);
            }
        }
    }

    @FunctionalInterface
    private static interface SettlementStorageHandler {
        public void accept(ServerSettlementData var1, ServerClient var2, SettlementInventory var3);
    }
}

