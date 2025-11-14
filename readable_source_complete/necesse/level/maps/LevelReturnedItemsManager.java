/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps;

import java.util.ArrayList;
import java.util.Map;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.MobSave;
import necesse.engine.save.levelData.PickupEntitySave;
import necesse.engine.util.GameMath;
import necesse.engine.util.HashMapArrayList;
import necesse.engine.world.ReturnedObjects;
import necesse.entity.mobs.Mob;
import necesse.entity.pickup.PickupEntity;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.Region;

public class LevelReturnedItemsManager {
    public final Level level;
    protected HashMapArrayList<Long, InventoryItem> regionItems = new HashMapArrayList();
    protected HashMapArrayList<Long, Mob> regionMobs = new HashMapArrayList();
    protected HashMapArrayList<Long, PickupEntity> regionPickups = new HashMapArrayList();

    public LevelReturnedItemsManager(Level level) {
        this.level = level;
    }

    public void onRegionUnloading(Region region) {
        if (this.level.isClient()) {
            return;
        }
        if (!this.level.keepTrackOfReturnedItems) {
            return;
        }
        long key = GameMath.getUniqueLongKey(region.regionX, region.regionY);
        ArrayList items = (ArrayList)this.regionItems.get(key);
        items.clear();
        this.addRegionReturnedItems(region, items);
        if (items.isEmpty()) {
            this.regionItems.clear(key);
        }
        ArrayList mobs = (ArrayList)this.regionMobs.get(key);
        for (Mob mob : this.level.entityManager.mobs.getSaveToRegion(region.regionX, region.regionY)) {
            if (!mob.shouldAddToDeletedLevelReturnedMobs()) continue;
            mobs.add(mob);
        }
        if (mobs.isEmpty()) {
            this.regionMobs.clear(key);
        }
        ArrayList pickups = (ArrayList)this.regionPickups.get(key);
        for (PickupEntity pickupEntity : this.level.entityManager.pickups.getSaveToRegion(region.regionX, region.regionY)) {
            if (!pickupEntity.shouldAddToDeletedLevelReturnedPickups()) continue;
            pickups.add(pickupEntity);
        }
        if (pickups.isEmpty()) {
            this.regionPickups.clear(key);
        }
    }

    protected void addRegionReturnedItems(Region region, ArrayList<InventoryItem> items) {
        for (int regionTileX = 0; regionTileX < region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < region.tileHeight; ++regionTileY) {
                int tileX = region.tileXOffset + regionTileX;
                int tileY = region.tileYOffset + regionTileY;
                for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                    GameObject object = region.objectLayer.getObjectByRegion(layer, regionTileX, regionTileY);
                    if (!object.shouldReturnOnDeletedLevels(this.level, layer, tileX, tileY)) continue;
                    for (InventoryItem item : object.getCombinedDroppedItems(this.level, layer, tileX, tileY, "returnedItems")) {
                        item.combineOrAddToList(this.level, null, items, "add");
                    }
                }
                GameTile tile = region.tileLayer.getTileByRegion(regionTileX, regionTileY);
                if (!tile.shouldReturnOnDeletedLevels(this.level, tileX, tileY)) continue;
                for (InventoryItem item : tile.getDroppedItems(this.level, tileX, tileY)) {
                    item.combineOrAddToList(this.level, null, items, "add");
                }
            }
        }
    }

    public void onRegionLoaded(Region region) {
        if (this.level.isClient()) {
            return;
        }
        long key = GameMath.getUniqueLongKey(region.regionX, region.regionY);
        this.regionItems.clear(key);
        this.regionMobs.clear(key);
        this.regionPickups.clear(key);
    }

    public void addSaveData(SaveData save) {
        if (!this.level.keepTrackOfReturnedItems) {
            return;
        }
        save.addBoolean("keepTrackOfReturnedItems", true);
        SaveData returnedItemsSave = new SaveData("RETURNEDITEMS");
        for (Map.Entry entry : this.regionItems.entrySet()) {
            Long l = (Long)entry.getKey();
            int regionX = GameMath.getXFromUniqueLongKey(l);
            int regionY = GameMath.getYFromUniqueLongKey(l);
            ArrayList items = (ArrayList)entry.getValue();
            if (items.isEmpty()) continue;
            SaveData regionSave = new SaveData(regionX + "x" + regionY);
            for (Object item : items) {
                regionSave.addSaveData(((InventoryItem)item).getSaveData("ITEM"));
            }
            returnedItemsSave.addSaveData(regionSave);
        }
        if (!returnedItemsSave.isEmpty()) {
            save.addSaveData(returnedItemsSave);
        }
        SaveData returnedMobsSave = new SaveData("RETURNEDMOBS");
        for (Map.Entry entry : this.regionMobs.entrySet()) {
            Long key = (Long)entry.getKey();
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            ArrayList mobs = (ArrayList)entry.getValue();
            if (mobs.isEmpty()) continue;
            SaveData regionSave = new SaveData(regionX + "x" + regionY);
            for (Mob mob : mobs) {
                regionSave.addSaveData(MobSave.getSave("MOB", mob));
            }
            returnedMobsSave.addSaveData(regionSave);
        }
        if (!returnedMobsSave.isEmpty()) {
            save.addSaveData(returnedMobsSave);
        }
        SaveData saveData = new SaveData("RETURNEDPICKUPS");
        for (Map.Entry entry : this.regionPickups.entrySet()) {
            Long key = (Long)entry.getKey();
            int regionX = GameMath.getXFromUniqueLongKey(key);
            int regionY = GameMath.getYFromUniqueLongKey(key);
            ArrayList pickups = (ArrayList)entry.getValue();
            if (pickups.isEmpty()) continue;
            SaveData regionSave = new SaveData(regionX + "x" + regionY);
            for (PickupEntity pickup : pickups) {
                regionSave.addSaveData(PickupEntitySave.getSave("PICKUP", pickup));
            }
            saveData.addSaveData(regionSave);
        }
        if (!saveData.isEmpty()) {
            save.addSaveData(saveData);
        }
    }

    public void loadSaveData(LoadData save) {
        LoadData loadData;
        this.level.keepTrackOfReturnedItems = save.getBoolean("keepTrackOfReturnedItems", false, false);
        LoadData returnedItemsSave = save.getFirstLoadDataByName("RETURNEDITEMS");
        if (returnedItemsSave == null) {
            return;
        }
        for (LoadData loadData2 : returnedItemsSave.getLoadData()) {
            String regionSaveName = loadData2.getName();
            int splitIndex = regionSaveName.indexOf("x");
            if (splitIndex == -1) {
                System.err.println("Invalid load data name for returned items: " + regionSaveName);
                continue;
            }
            try {
                int regionX = Integer.parseInt(regionSaveName.substring(0, splitIndex));
                int regionY = Integer.parseInt(regionSaveName.substring(splitIndex + 1));
                long key = GameMath.getUniqueLongKey(regionX, regionY);
                ArrayList items = (ArrayList)this.regionItems.get(key);
                for (LoadData loadData3 : loadData2.getLoadDataByName("ITEM")) {
                    InventoryItem item = InventoryItem.fromLoadData(loadData3);
                    if (item == null) continue;
                    items.add(item);
                }
                if (!items.isEmpty()) continue;
                this.regionItems.clear(key);
            }
            catch (Exception e) {
                System.err.println("Error loading returned items for region: " + regionSaveName);
            }
        }
        LoadData returnedMobsSave = save.getFirstLoadDataByName("RETURNEDMOBS");
        if (returnedMobsSave != null) {
            for (LoadData regionSave : returnedMobsSave.getLoadData()) {
                String regionSaveName = regionSave.getName();
                int splitIndex = regionSaveName.indexOf("x");
                if (splitIndex == -1) {
                    System.err.println("Invalid load data name for returned mobs: " + regionSaveName);
                    continue;
                }
                try {
                    int regionX = Integer.parseInt(regionSaveName.substring(0, splitIndex));
                    int regionY = Integer.parseInt(regionSaveName.substring(splitIndex + 1));
                    long key = GameMath.getUniqueLongKey(regionX, regionY);
                    ArrayList mobs = (ArrayList)this.regionMobs.get(key);
                    for (LoadData mobSave : regionSave.getLoadDataByName("MOB")) {
                        Mob mob = MobSave.loadSave(mobSave, this.level);
                        if (mob == null) continue;
                        mobs.add(mob);
                    }
                    if (!mobs.isEmpty()) continue;
                    this.regionMobs.clear(key);
                }
                catch (Exception e) {
                    System.err.println("Error loading returned mobs for region: " + regionSaveName);
                }
            }
        }
        if ((loadData = save.getFirstLoadDataByName("RETURNEDPICKUPS")) != null) {
            for (LoadData regionSave : loadData.getLoadData()) {
                String regionSaveName = regionSave.getName();
                int splitIndex = regionSaveName.indexOf("x");
                if (splitIndex == -1) {
                    System.err.println("Invalid load data name for returned pickups: " + regionSaveName);
                    continue;
                }
                try {
                    int regionX = Integer.parseInt(regionSaveName.substring(0, splitIndex));
                    int regionY = Integer.parseInt(regionSaveName.substring(splitIndex + 1));
                    long key = GameMath.getUniqueLongKey(regionX, regionY);
                    ArrayList arrayList = (ArrayList)this.regionPickups.get(key);
                    for (LoadData pickupSave : regionSave.getLoadDataByName("PICKUP")) {
                        PickupEntity pickup = PickupEntitySave.loadSave(pickupSave, this.level);
                        if (pickup == null) continue;
                        arrayList.add(pickup);
                    }
                    if (!arrayList.isEmpty()) continue;
                    this.regionPickups.clear(key);
                }
                catch (Exception e) {
                    System.err.println("Error loading returned pickups for region: " + regionSaveName);
                }
            }
        }
    }

    public void addAllReturnedItems(ReturnedObjects returnedObjects) {
        if (!this.level.keepTrackOfReturnedItems) {
            return;
        }
        this.level.regionManager.forEachLoadedRegions(region -> {
            this.regionItems.clear(GameMath.getUniqueLongKey(region.regionX, region.regionY));
            this.addRegionReturnedItems((Region)region, returnedObjects.items);
        });
        for (ArrayList regionItems : this.regionItems.values()) {
            returnedObjects.items.addAll(regionItems);
        }
        this.level.entityManager.addReturnedEntities(returnedObjects);
        for (ArrayList regionMobs : this.regionMobs.values()) {
            returnedObjects.mobs.addAll(regionMobs);
        }
        for (ArrayList regionPickups : this.regionPickups.values()) {
            returnedObjects.pickups.addAll(regionPickups);
        }
    }
}

