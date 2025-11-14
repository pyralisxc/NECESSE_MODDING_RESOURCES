/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.registries.LevelDataRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.entity.manager.EntityComponentManager;
import necesse.level.maps.Level;
import necesse.level.maps.levelBuffManager.LevelBuffsEntityComponent;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.RegionLevelDataComponent;
import necesse.level.maps.regionSystem.Region;

public class LevelDataManager {
    public final Level level;
    private final HashMap<String, LevelData> data = new HashMap();
    public final EntityComponentManager<String> componentManager = new EntityComponentManager();

    public LevelDataManager(Level level) {
        this.level = level;
    }

    public void addSaveData(SaveData save) {
        for (Map.Entry<String, LevelData> entry : this.data.entrySet()) {
            if (!entry.getValue().shouldSave()) continue;
            SaveData dataSave = new SaveData(entry.getKey());
            entry.getValue().addSaveData(dataSave);
            save.addSaveData(dataSave);
        }
    }

    public void loadSaveData(LoadData save) {
        for (LoadData saveData : save.getLoadData()) {
            try {
                LevelData loadedData = LevelDataRegistry.loadLevelData(this.level, saveData);
                if (loadedData == null) continue;
                this.level.addLevelData(saveData.getName(), loadedData);
            }
            catch (Exception e) {
                System.err.println("Error loading level data:");
                e.printStackTrace();
            }
        }
    }

    public void onLoadingComplete() {
        for (LevelData value : this.data.values()) {
            value.onLoadingComplete();
        }
    }

    public void addRegionSaveData(Region region, SaveData save) {
        for (RegionLevelDataComponent component : this.componentManager.getAll(RegionLevelDataComponent.class)) {
            LevelData levelData = (LevelData)((Object)component);
            SaveData levelDataSave = new SaveData("LEVELDATA");
            levelDataSave.addSafeString("key", levelData.getManagerKey());
            component.addRegionSaveData(region, levelDataSave);
            if (levelDataSave.getSize() <= 1) continue;
            save.addSaveData(levelDataSave);
        }
    }

    public void applyRegionSaveData(Region region, LoadData save) {
        for (LoadData levelDataSave : save.getLoadDataByName("LEVELDATA")) {
            LevelData data;
            String key = levelDataSave.getSafeString("key", null, false);
            if (key == null || !((data = this.getLevelData(key)) instanceof RegionLevelDataComponent)) continue;
            ((RegionLevelDataComponent)((Object)data)).loadRegionSaveData(region, levelDataSave);
        }
    }

    public void onUnloadedRegion(Region region) {
        for (RegionLevelDataComponent component : this.componentManager.getAll(RegionLevelDataComponent.class)) {
            component.onUnloadedRegion(region);
        }
    }

    public void tick() {
        Performance.record((PerformanceTimerManager)this.level.tickManager(), "levelData", () -> {
            for (LevelData data : this.data.values()) {
                data.tick();
            }
        });
    }

    public LevelData getLevelData(String key) {
        return this.data.get(key);
    }

    public void addLevelData(String key, LevelData levelData) {
        if (!key.matches("[a-zA-Z0-9]+")) {
            throw new IllegalArgumentException("Key \"" + key + "\" contains illegal characters");
        }
        levelData.setLevel(this.level);
        this.data.put(key, levelData);
        levelData.init();
        levelData.setManagerKey(key);
        this.componentManager.add(key, levelData);
        if (levelData instanceof LevelBuffsEntityComponent) {
            this.level.buffManager.updateBuffs();
        }
    }

    public LevelData removeLevelData(String key) {
        LevelData remove = this.data.remove(key);
        if (remove != null) {
            this.componentManager.remove(key, remove);
            if (remove instanceof LevelBuffsEntityComponent) {
                this.level.buffManager.updateBuffs();
            }
        }
        return remove;
    }

    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Level newLevel, Point tileOffset, Point positionOffset) {
        for (LevelData value : this.data.values()) {
            value.migrateToOneWorld(migrationData, oldLevelIdentifier, newLevel, tileOffset, positionOffset);
        }
    }
}

