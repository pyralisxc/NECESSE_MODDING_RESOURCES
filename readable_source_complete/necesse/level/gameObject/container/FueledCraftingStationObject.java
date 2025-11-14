/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Rectangle;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.AnyLogFueledInventoryObjectEntity;
import necesse.entity.objectEntity.FueledInventoryObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.recipe.Recipe;
import necesse.level.gameObject.container.CraftingStationObject;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledCraftingStationObject
extends CraftingStationObject {
    public FueledCraftingStationObject() {
    }

    public FueledCraftingStationObject(Rectangle collision) {
        super(collision);
    }

    public FueledInventoryObjectEntity getFueledObjectEntity(Level level, int x, int y) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(x, y);
        if (objectEntity instanceof FueledInventoryObjectEntity) {
            return (FueledInventoryObjectEntity)objectEntity;
        }
        return null;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        super.interact(level, x, y, player);
        if (level.isServer()) {
            CraftingStationContainer.openAndSendContainer(ContainerRegistry.FUELED_CRAFTING_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new AnyLogFueledInventoryObjectEntity(level, this.getStringID(), x, y, false);
    }

    @Override
    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        return fueledObjectEntity != null && (fueledObjectEntity.isFueled() || fueledObjectEntity.canFuel());
    }

    @Override
    public void tickCrafting(Level level, int tileX, int tileY, Recipe recipe) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        if (fueledObjectEntity != null && !fueledObjectEntity.isFueled()) {
            fueledObjectEntity.useFuel();
        }
    }

    @Override
    public void onCraftFinished(Level level, int tileX, int tileY, Recipe recipe) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        if (fueledObjectEntity != null && !fueledObjectEntity.isFueled()) {
            fueledObjectEntity.useFuel();
        }
    }

    @Override
    public SettlementRequestOptions getFuelRequestOptions(Level level, int tileX, int tileY) {
        return new SettlementRequestOptions(5, 10){

            @Override
            public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords records) {
                return records.getIndex(SettlementStorageGlobalIngredientIDIndex.class).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
            }
        };
    }

    @Override
    public InventoryRange getFuelInventoryRange(Level level, int tileX, int tileY) {
        FueledInventoryObjectEntity fueledObjectEntity = this.getFueledObjectEntity(level, tileX, tileY);
        Inventory inventory = fueledObjectEntity.getInventory();
        if (inventory != null) {
            return new InventoryRange(inventory);
        }
        return null;
    }
}

