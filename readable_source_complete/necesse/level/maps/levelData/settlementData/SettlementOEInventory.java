/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import necesse.engine.localization.message.GameMessage;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.LevelStorage;

public abstract class SettlementOEInventory
extends LevelStorage {
    public OEInventory oeInventory;

    public SettlementOEInventory(Level level, int tileX, int tileY, boolean setup) {
        super(level, tileX, tileY);
        if (setup) {
            this.refreshOEInventory();
        }
    }

    public void refreshOEInventory() {
        ObjectEntity objectEntity = this.level.entityManager.getObjectEntity(this.tileX, this.tileY);
        this.oeInventory = objectEntity instanceof OEInventory ? (OEInventory)((Object)objectEntity) : null;
    }

    @Override
    public GameMessage getInventoryName() {
        this.refreshOEInventory();
        if (this.oeInventory == null) {
            return this.level.getObjectName(this.tileX, this.tileY);
        }
        return this.oeInventory.getInventoryName();
    }
}

