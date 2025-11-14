/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.incursion;

import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.inventory.lootTable.lootItem.LootItemList;

public class UniqueIncursionReward
implements IDDataContainer {
    public final IDData idData = new IDData();
    public final LootItemList lootItemList;

    public UniqueIncursionReward(LootItemList lootItemList) {
        this.lootItemList = lootItemList;
    }

    @Override
    public IDData getIDData() {
        return this.idData;
    }

    @Override
    public String getStringID() {
        return this.idData.getStringID();
    }

    @Override
    public int getID() {
        return this.idData.getID();
    }
}

