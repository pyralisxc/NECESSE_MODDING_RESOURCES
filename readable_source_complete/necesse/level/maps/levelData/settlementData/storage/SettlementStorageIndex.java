/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.storage;

import necesse.engine.registries.IDData;
import necesse.inventory.InventoryItem;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecord;

public abstract class SettlementStorageIndex {
    public final IDData idData = new IDData();
    public final Level level;

    public final String getStringID() {
        return this.idData.getStringID();
    }

    public final int getID() {
        return this.idData.getID();
    }

    public SettlementStorageIndex(Level level) {
        this.level = level;
    }

    public abstract void clear();

    public abstract void add(InventoryItem var1, SettlementStorageRecord var2);
}

