/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData;

import java.util.Collection;
import java.util.LinkedList;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashMap;
import necesse.level.maps.levelData.LevelData;
import necesse.level.maps.levelData.RegionLevelDataComponent;
import necesse.level.maps.regionSystem.Region;

public abstract class MultiRegionBasedLevelData<T extends RegionData>
extends LevelData
implements RegionLevelDataComponent {
    public PointHashMap<LinkedList<T>> regionData = new PointHashMap();

    @Override
    public void addRegionSaveData(Region region, SaveData save) {
        LinkedList<T> list = this.regionData.get(region.regionX, region.regionY);
        if (list != null) {
            for (RegionData regionData : list) {
                SaveData regionDataSave = new SaveData("DATA");
                this.addRegionSaveData(regionDataSave, regionData);
                if (regionDataSave.isEmpty()) continue;
                save.addSaveData(regionDataSave);
            }
        }
    }

    @Override
    public void loadRegionSaveData(Region region, LoadData save) {
        for (LoadData regionDataSave : save.getLoadDataByName("DATA")) {
            T regionData = this.loadRegionData(region, regionDataSave);
            if (regionData == null) continue;
            this.addDataInRegion(region.regionX, region.regionY, regionData);
        }
    }

    @Override
    public void onUnloadedRegion(Region region) {
        this.regionData.remove(region.regionX, region.regionY);
    }

    protected abstract void addRegionSaveData(SaveData var1, T var2);

    protected abstract T loadRegionData(Region var1, LoadData var2);

    protected Iterable<T> getDataInRegion(int regionX, int regionY) {
        return this.regionData.getOrDefault(regionX, regionY, new LinkedList());
    }

    protected void addDataInRegion(int regionX, int regionY, T data) {
        LinkedList list = this.regionData.compute(regionX, regionY, (key, value) -> {
            if (value == null) {
                value = new LinkedList();
            }
            return value;
        });
        list.add(data);
    }

    public Iterable<T> getAllData() {
        return () -> this.regionData.values().stream().flatMap(Collection::stream).iterator();
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

