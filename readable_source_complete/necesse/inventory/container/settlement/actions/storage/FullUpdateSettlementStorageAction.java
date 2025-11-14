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
import necesse.inventory.container.settlement.events.SettlementStorageFullUpdateEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class FullUpdateSettlementStorageAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public FullUpdateSettlementStorageAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int tileX, int tileY, ItemCategoriesFilter filter, int priority) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextInt(priority);
        filter.writePacket(writer);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int tileX = reader.getNextInt();
        int tileY = reader.getNextInt();
        int priority = reader.getNextInt();
        if (this.container.client.isServer()) {
            ServerClient client = this.container.client.getServerClient();
            ServerSettlementData serverData = this.container.getServerData();
            if (serverData != null) {
                if (!serverData.networkData.doesClientHaveAccess(client)) {
                    new SettlementDataEvent(serverData).applyAndSendToClient(client);
                    return;
                }
                SettlementInventory storage = serverData.storageManager.getStorage(tileX, tileY);
                if (storage != null) {
                    storage.priority = priority;
                    storage.filter.readPacket(reader);
                    new SettlementStorageFullUpdateEvent(serverData, tileX, tileY, storage.filter, storage.priority).applyAndSendToClientsAtExcept(client);
                } else {
                    new SettlementSingleStorageEvent(serverData, tileX, tileY).applyAndSendToClient(client);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

