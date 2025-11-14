/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public abstract class SettlementRequestOptions {
    public final int minAmount;
    public final int maxAmount;

    public SettlementRequestOptions(int minAmount, int maxAmount) {
        this.minAmount = minAmount;
        this.maxAmount = maxAmount;
    }

    public abstract SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords var1);
}

