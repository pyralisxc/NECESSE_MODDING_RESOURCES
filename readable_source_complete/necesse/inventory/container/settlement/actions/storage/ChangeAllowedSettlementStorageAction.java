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
import necesse.inventory.container.settlement.events.SettlementStorageChangeAllowedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;

public class ChangeAllowedSettlementStorageAction
extends ContainerCustomAction {
    public final SettlementDependantContainer container;

    public ChangeAllowedSettlementStorageAction(SettlementDependantContainer container) {
        this.container = container;
    }

    public void runAndSend(int tileX, int tileY, Item[] items, boolean allowed) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(allowed);
        writer.putNextBoolean(true);
        writer.putNextShortUnsigned(items.length);
        for (Item item : items) {
            writer.putNextShortUnsigned(item.getID());
        }
        this.runAndSendAction(content);
    }

    public void runAndSend(int tileX, int tileY, ItemCategoriesFilter.ItemCategoryFilter category, boolean allowed) {
        Packet content = new Packet();
        PacketWriter writer = new PacketWriter(content);
        writer.putNextInt(tileX);
        writer.putNextInt(tileY);
        writer.putNextBoolean(allowed);
        writer.putNextBoolean(false);
        writer.putNextShortUnsigned(category.category.id);
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
                    boolean allowed = reader.getNextBoolean();
                    boolean isItems = reader.getNextBoolean();
                    if (isItems) {
                        int itemsLength = reader.getNextShortUnsigned();
                        Item[] items = new Item[itemsLength];
                        for (int i = 0; i < itemsLength; ++i) {
                            Item item;
                            int itemID = reader.getNextShortUnsigned();
                            items[i] = item = ItemRegistry.getItem(itemID);
                            storage.filter.setItemAllowed(item, allowed);
                        }
                        new SettlementStorageChangeAllowedEvent(serverData, tileX, tileY, items, allowed).applyAndSendToClientsAtExcept(client);
                    } else {
                        int categoryID = reader.getNextShortUnsigned();
                        ItemCategoriesFilter.ItemCategoryFilter category = storage.filter.getItemCategory(categoryID);
                        category.setAllowed(allowed);
                        new SettlementStorageChangeAllowedEvent(serverData, tileX, tileY, category.category, allowed).applyAndSendToClientsAtExcept(client);
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

