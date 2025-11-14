/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.mapData;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.zip.DataFormatException;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.ClientLevelManager;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.InvalidLevelIdentifierException;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.World;
import necesse.level.maps.mapData.BasicDiscoveredMap;
import necesse.level.maps.mapData.MapRegionFileGetter;

public class BasicDiscoveredMapManager {
    private final HashMap<LevelIdentifier, BasicDiscoveredMap> maps = new HashMap();

    public void addSaveData(SaveData save) {
        for (Map.Entry<LevelIdentifier, BasicDiscoveredMap> entry : this.maps.entrySet()) {
            SaveData mapSave = new SaveData(entry.getKey().stringID);
            entry.getValue().addSaveData(mapSave);
            if (mapSave.isEmpty()) continue;
            save.addSaveData(mapSave);
        }
    }

    public void applySaveData(LoadData save) {
        for (LoadData mapSave : save.getLoadData()) {
            try {
                LevelIdentifier levelIdentifier = new LevelIdentifier(mapSave.getName());
                BasicDiscoveredMap discoveredMap = new BasicDiscoveredMap();
                discoveredMap.applySaveData(mapSave);
                this.maps.put(levelIdentifier, discoveredMap);
            }
            catch (Exception e) {
                System.err.println("Error loading discovered map data for " + mapSave.getName());
                e.printStackTrace();
            }
        }
    }

    public void loadFromFileSystem(String rootFolderPath) {
        File file = new File(rootFolderPath);
        if (file.exists() && file.isDirectory()) {
            File[] files = file.listFiles();
            if (files == null) {
                return;
            }
            for (File levelFile : files) {
                if (!levelFile.isDirectory()) continue;
                try {
                    LevelIdentifier identifier = new LevelIdentifier(levelFile.getName());
                    File[] mapRegionFiles = levelFile.listFiles();
                    if (mapRegionFiles == null) continue;
                    for (File mapRegionFile : mapRegionFiles) {
                        Matcher matcher = ClientLevelManager.mapRegionFilePattern.matcher(mapRegionFile.getName());
                        if (!matcher.matches()) continue;
                        try {
                            int mapRegionX = Integer.parseInt(matcher.group(1));
                            int mapRegionY = Integer.parseInt(matcher.group(2));
                            BasicDiscoveredMap map = this.maps.computeIfAbsent(identifier, levelIdentifier -> new BasicDiscoveredMap());
                            map.applySaveData(mapRegionX, mapRegionY, new LoadData(mapRegionFile));
                            if (!map.isEmpty()) continue;
                            this.maps.remove(identifier);
                        }
                        catch (Exception e) {
                            GameLog.warn.println("Found invalid map region file at: " + mapRegionFile);
                        }
                    }
                }
                catch (InvalidLevelIdentifierException invalidLevelIdentifierException) {
                    // empty catch block
                }
            }
        }
    }

    public void saveToFileSystem(MapRegionFileGetter mapRegionFileGetter) {
        for (Map.Entry<LevelIdentifier, BasicDiscoveredMap> entry : this.maps.entrySet()) {
            entry.getValue().saveToFileSystem(entry.getKey(), mapRegionFileGetter);
        }
    }

    public void writePacketData(PacketWriter writer) throws DataFormatException, IOException {
        writer.putNextShortUnsigned(this.maps.size());
        for (Map.Entry<LevelIdentifier, BasicDiscoveredMap> entry : this.maps.entrySet()) {
            LevelIdentifier identifier = entry.getKey();
            BasicDiscoveredMap map = entry.getValue();
            identifier.writePacket(writer);
            map.writePacketData(writer);
        }
    }

    public boolean readPacketData(PacketReader reader) throws DataFormatException, IOException {
        boolean changed = false;
        int size = reader.getNextShortUnsigned();
        for (int i = 0; i < size; ++i) {
            LevelIdentifier identifier = new LevelIdentifier(reader);
            BasicDiscoveredMap map = this.maps.computeIfAbsent(identifier, levelIdentifier -> new BasicDiscoveredMap());
            changed = map.applyPacketData(reader, true) || changed;
        }
        return changed;
    }

    public boolean combine(BasicDiscoveredMapManager other) {
        boolean changed = false;
        for (Map.Entry<LevelIdentifier, BasicDiscoveredMap> entry : other.maps.entrySet()) {
            LevelIdentifier identifier = entry.getKey();
            BasicDiscoveredMap otherMap = entry.getValue();
            BasicDiscoveredMap myMap = this.maps.computeIfAbsent(identifier, levelIdentifier -> new BasicDiscoveredMap());
            changed = myMap.combine(otherMap) || changed;
        }
        return changed;
    }

    public void makeFinal() {
        for (BasicDiscoveredMap map : this.maps.values()) {
            map.makeFinal();
        }
    }

    public void clearRemovedLevelIdentifiers(World world) {
        HashSet<LevelIdentifier> removes = new HashSet<LevelIdentifier>();
        for (LevelIdentifier levelIdentifier : this.maps.keySet()) {
            if (world.levelExists(levelIdentifier)) continue;
            removes.add(levelIdentifier);
        }
        for (LevelIdentifier remove : removes) {
            this.maps.remove(remove);
        }
    }
}

