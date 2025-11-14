/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.DataFormatException;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.mapData.DiscoveredMapData;
import necesse.level.maps.mapData.MapRegionFileGetter;

public class BasicDiscoveredMap {
    private final HashMap<Long, DiscoveredMapData> regions = new HashMap();

    protected DiscoveredMapData loadRegion(int mapRegionX, int mapRegionY) {
        return new DiscoveredMapData(0, 0);
    }

    protected DiscoveredMapData getRegion(int mapRegionX, int mapRegionY, boolean createIfDoesntExist) {
        long key = GameMath.getUniqueLongKey(mapRegionX, mapRegionY);
        DiscoveredMapData region = this.regions.get(key);
        if (region == null && createIfDoesntExist && (region = this.loadRegion(mapRegionX, mapRegionY)) != null) {
            this.regions.put(key, region);
        }
        return region;
    }

    public void addSaveData(SaveData save) {
        for (Map.Entry<Long, DiscoveredMapData> entry : this.regions.entrySet()) {
            long key = entry.getKey();
            int mapRegionX = GameMath.getXFromUniqueLongKey(key);
            int mapRegionY = GameMath.getYFromUniqueLongKey(key);
            DiscoveredMapData data = entry.getValue();
            try {
                save.addSaveData(data.getSaveData(mapRegionX + "x" + mapRegionY));
            }
            catch (IOException e) {
                System.err.println("Could not save discovered map data for region " + mapRegionX + "x" + mapRegionY);
                e.printStackTrace();
            }
        }
    }

    public void applySaveData(LoadData save) {
        for (LoadData loadData : save.getLoadData()) {
            String name = loadData.getName();
            int splitIndex = name.indexOf("x");
            if (splitIndex == -1) {
                System.err.println("Invalid load data name for map region: " + name);
                continue;
            }
            try {
                int mapRegionX = Integer.parseInt(name.substring(0, splitIndex));
                int mapRegionY = Integer.parseInt(name.substring(splitIndex + 1));
                DiscoveredMapData region = this.getRegion(mapRegionX, mapRegionY, true);
                try {
                    region.applySaveData(loadData, true);
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.regions.remove(GameMath.getUniqueLongKey(mapRegionX, mapRegionY));
                }
            }
            catch (NumberFormatException e) {
                System.err.println("Invalid load data name for map region: " + name);
            }
        }
    }

    public void applySaveData(int mapRegionX, int mapRegionY, LoadData save) {
        DiscoveredMapData region = this.getRegion(mapRegionX, mapRegionY, true);
        try {
            region.applySaveData(save, true);
        }
        catch (Exception e) {
            e.printStackTrace();
            this.regions.remove(GameMath.getUniqueLongKey(mapRegionX, mapRegionY));
        }
    }

    public void writePacketData(PacketWriter writer) throws DataFormatException, IOException {
        writer.putNextShortUnsigned(this.regions.size());
        for (Map.Entry<Long, DiscoveredMapData> entry : this.regions.entrySet()) {
            long key = entry.getKey();
            int mapRegionX = GameMath.getXFromUniqueLongKey(key);
            int mapRegionY = GameMath.getYFromUniqueLongKey(key);
            DiscoveredMapData data = entry.getValue();
            writer.putNextInt(mapRegionX);
            writer.putNextInt(mapRegionY);
            data.writePacketData(writer);
        }
    }

    public boolean applyPacketData(PacketReader reader, boolean makeFinal) throws DataFormatException, IOException {
        boolean changed = false;
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            int mapRegionY;
            int mapRegionX = reader.getNextInt();
            DiscoveredMapData region = this.getRegion(mapRegionX, mapRegionY = reader.getNextInt(), true);
            boolean bl = changed = region.applyPacketData(reader) || changed;
            if (!makeFinal) continue;
            region.makeFinal();
        }
        return changed;
    }

    public boolean isEmpty() {
        return this.regions.isEmpty();
    }

    public boolean combine(BasicDiscoveredMap other) {
        boolean changed = false;
        for (Map.Entry<Long, DiscoveredMapData> entry : other.regions.entrySet()) {
            int mapRegionY;
            long key = entry.getKey();
            int mapRegionX = GameMath.getXFromUniqueLongKey(key);
            DiscoveredMapData region = this.getRegion(mapRegionX, mapRegionY = GameMath.getYFromUniqueLongKey(key), true);
            changed = region.combine(entry.getValue()) || changed;
        }
        return changed;
    }

    public void makeFinal() {
        for (DiscoveredMapData region : this.regions.values()) {
            region.makeFinal();
        }
    }

    public void saveToFileSystem(LevelIdentifier identifier, MapRegionFileGetter mapRegionFileGetter) {
        for (Map.Entry<Long, DiscoveredMapData> entry : this.regions.entrySet()) {
            long key = entry.getKey();
            int mapRegionX = GameMath.getXFromUniqueLongKey(key);
            int mapRegionY = GameMath.getYFromUniqueLongKey(key);
            DiscoveredMapData region = entry.getValue();
            try {
                region.saveToFileSystem(mapRegionFileGetter.getFile(identifier, mapRegionX, mapRegionY), mapRegionX, mapRegionY);
            }
            catch (IOException e) {
                System.err.println("Could not save discovered map data for region " + mapRegionX + "x" + mapRegionY);
                e.printStackTrace();
            }
        }
    }
}

