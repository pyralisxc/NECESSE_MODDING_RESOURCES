/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashMap;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.RegionLevelDataComponent;
import necesse.level.maps.regionSystem.Region;

public abstract class SingleRegionBasedLevelData<T extends RegionData>
extends LevelData
implements RegionLevelDataComponent {
    protected PointHashMap<T> regionData = new PointHashMap();

    @Override
    public void addRegionSaveData(Region region, SaveData save) {
        RegionData data = (RegionData)this.regionData.get(region.regionX, region.regionY);
        if (data != null) {
            SaveData regionDataSave = new SaveData("DATA");
            this.addRegionSaveData(regionDataSave, data);
            if (!regionDataSave.isEmpty()) {
                save.addSaveData(regionDataSave);
            }
        }
    }

    @Override
    public void loadRegionSaveData(Region region, LoadData save) {
        T regionData;
        LoadData regionDataSave = save.getFirstLoadDataByName("DATA");
        if (regionDataSave != null && (regionData = this.loadRegionData(region, regionDataSave)) != null) {
            this.setDataInRegion(region.regionX, region.regionY, regionData);
        }
    }

    @Override
    public void onUnloadedRegion(Region region) {
        this.regionData.remove(region.regionX, region.regionY);
    }

    protected abstract void addRegionSaveData(SaveData var1, T var2);

    protected abstract T loadRegionData(Region var1, LoadData var2);

    protected T computeDataInRegion(int regionX, int regionY, PointHashMap.PointRemappingFunction<T> remappingFunction) {
        return (T)((RegionData)this.regionData.compute(regionX, regionY, remappingFunction));
    }

    protected T getDataInRegion(int regionX, int regionY) {
        return (T)((RegionData)this.regionData.get(regionX, regionY));
    }

    protected void setDataInRegion(int regionX, int regionY, T data) {
        this.regionData.put(regionX, regionY, data);
    }

    public Iterable<T> getAllData() {
        return () -> this.regionData.values().iterator();
    }

    public static class RegionData {
        public final int regionX;
        public final int regionY;

        public RegionData(int regionX, int regionY) {
            this.regionX = regionX;
            this.regionY = regionY;
        }
    }
}

