/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import necesse.engine.util.GameLinkedList;
import necesse.inventory.InventoryItem;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;

public class ValidatedSettlementStorageRecord {
    public final GameLinkedList.Element element;
    public final SettlementStorageRecord record;
    public final InventoryItem invItem;

    public ValidatedSettlementStorageRecord(SettlementStorageRecord record, InventoryItem invItem) {
        this.element = null;
        this.record = record;
        this.invItem = invItem;
    }

    public ValidatedSettlementStorageRecord(GameLinkedList.Element element, InventoryItem invItem) {
        this.element = element;
        this.record = (SettlementStorageRecord)element.object;
        this.invItem = invItem;
    }
}

