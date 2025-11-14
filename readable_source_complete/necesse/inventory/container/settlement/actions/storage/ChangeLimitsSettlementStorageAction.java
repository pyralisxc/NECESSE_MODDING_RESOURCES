/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.container.settlement.actions.storage;

import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.ItemRegistry;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.settlement.SettlementDependantContainer;
import necesse.inventory.container.settlement.events.SettlementDataEvent;
import necesse.inventory.container.settlement.events.SettlementRemovedEvent;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementStorageLimitsEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class ChangeLimitsSettlementStorageAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public ChangeLimitsSettlementStorageAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int tileX, int tileY, Item item, ItemCategoriesFilter.ItemLimits limits) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(true);
        writer.putNextShortUnsigned(item.getID());
        limits.writePacket(writer);
        this.runAndSendAction(content);
    }

    public void runAndSend(int tileX, int tileY, ItemCategoriesFilter.ItemCategoryFilter category, int maxItems) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(false);
        writer.putNextShortUnsigned(category.category.id);
        writer.putNextInt(maxItems);
        this.runAndSendAction(content);
    }

    @Override
    public void executePacket(PacketReader reader) {
        int tileX = reader.getNextInt();
        int tileY = reader.getNextInt();
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
                    boolean isItems = reader.getNextBoolean();
                    if (isItems) {
                        int itemID = reader.getNextShortUnsigned();
                        Item item = ItemRegistry.getItem(itemID);
                        ItemCategoriesFilter.ItemLimits limits = new ItemCategoriesFilter.ItemLimits();
                        limits.readPacket(reader);
                        storage.filter.setItemAllowed(item, limits);
                        new SettlementStorageLimitsEvent(serverData, tileX, tileY, item, limits).applyAndSendToClientsAtExcept(client);
                    } else {
                        int categoryID = reader.getNextShortUnsigned();
                        int maxAmount = reader.getNextInt();
                        ItemCategoriesFilter.ItemCategoryFilter category = storage.filter.getItemCategory(categoryID);
                        category.setMaxItems(maxAmount);
                        new SettlementStorageLimitsEvent(serverData, tileX, tileY, category.category, maxAmount).applyAndSendToClientsAtExcept(client);
                    }
                } else {
                    new SettlementSingleStorageEvent(serverData, tileX, tileY).applyAndSendToClient(client);
                }
            } else {
                new SettlementRemovedEvent(0).applyAndSendToClient(client);
            }
        }
    }
}

