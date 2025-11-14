/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.LinkedList;
import java.util.stream.Stream;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashMap;
import necesse.engine.util.PointHashSet;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementRoom;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.SubRegion;

public class SettlementRoomsManager {
    public final ServerSettlementData data;
    private final PointHashMap<SettlementRoom> rooms = new PointHashMap();

    public SettlementRoomsManager(ServerSettlementData data) {
        this.data = data;
    }

    public void addSaveData(SaveData save) {
        SaveData roomsSave = new SaveData("ROOMS");
        PointHashSet savedTiles = new PointHashSet();
        for (SettlementRoom room : this.rooms.values()) {
            if (!savedTiles.add(room.tileX, room.tileY)) continue;
            SaveData roomSave = new SaveData("ROOM");
            roomSave.addPoint("tile", new Point(room.tileX, room.tileY));
            roomsSave.addSaveData(roomSave);
        }
        save.addSaveData(roomsSave);
    }

    public void loadSaveData(LoadData save, int tileXOffset, int tileYOffset) {
        Stream<Object> roomSaves = Stream.empty();
        roomSaves = Stream.concat(roomSaves, save.getLoadDataByName("ROOM").stream().filter(LoadData::isArray));
        LoadData roomsSave = save.getFirstLoadDataByName("ROOMS");
        if (roomsSave != null) {
            roomSaves = Stream.concat(roomSaves, roomsSave.getLoadDataByName("ROOM").stream().filter(LoadData::isArray));
        }
        roomSaves.forEach(c -> {
            block11: {
                try {
                    Point tile = c.getPoint("tile", null, false);
                    if (tile == null) {
                        int x = c.getInt("x", Integer.MIN_VALUE, false);
                        int y = c.getInt("y", Integer.MIN_VALUE, false);
                        if (x != Integer.MIN_VALUE && y != Integer.MIN_VALUE) {
                            tile = new Point(x, y);
                        }
                    }
                    if (tile != null) {
                        tile.translate(tileXOffset, tileYOffset);
                        SettlementRoom room = this.getRoom(tile.x, tile.y);
                        if (room != null) {
                            room.calculateStats();
                        }
                    }
                    if (!c.hasLoadDataByName("SETTLER")) break block11;
                    try {
                        ConnectedSubRegionsResult room;
                        LevelSettler settler = new LevelSettler(this.data, c.getFirstLoadDataByName("SETTLER"), tileXOffset, tileYOffset);
                        this.data.settlers.add(settler);
                        if (tile != null && !this.data.getLevel().isOutside(tile.x, tile.y) && (room = this.data.getLevel().regionManager.getRoomConnectedByTile(tile.x, tile.y, true, 2000)) != null) {
                            boolean found = false;
                            for (SubRegion roomSr : room.connectedRegions) {
                                for (Point p : roomSr.getLevelTiles()) {
                                    SettlementBed loadedBed = this.data.addOrValidateBed(p.x, p.y);
                                    if (loadedBed == null || loadedBed.isLocked || loadedBed.getSettler() != null) continue;
                                    settler.bed = loadedBed;
                                    loadedBed.settler = settler;
                                    found = true;
                                    break;
                                }
                                if (!found) continue;
                            }
                        }
                    }
                    catch (Exception e) {
                        System.err.println("Could not load settlement settler at level " + this.data.getLevel().getIdentifier());
                        e.printStackTrace();
                    }
                }
                catch (Exception e) {
                    System.err.println("Could not load settlement room at level " + this.data.getLevel().getIdentifier());
                }
            }
        });
    }

    public void refreshRooms(Iterable<Point> tiles) {
        for (Point tile : tiles) {
            SettlementRoom room = this.rooms.remove(tile.x, tile.y);
            if (room == null) continue;
            room.invalidate();
        }
    }

    public void recalculateStats(Iterable<Point> tiles) {
        for (Point tile : tiles) {
            SettlementRoom room = this.rooms.get(tile.x, tile.y);
            if (room == null) continue;
            room.recalculateStats();
        }
    }

    public void findAndCalculateRoom(int tileX, int tileY) {
        SettlementRoom room = this.getRoom(tileX, tileY);
        if (room != null) {
            room.calculateStats();
        }
    }

    public SettlementRoom getRoom(int tileX, int tileY) {
        if (!this.data.networkData.isTileWithinBounds(tileX, tileY)) {
            SettlementRoom room = this.rooms.remove(tileX, tileY);
            if (room != null) {
                room.invalidate();
            }
            return null;
        }
        if (this.data.getLevel().isOutside(tileX, tileY)) {
            return null;
        }
        return this.rooms.compute(tileX, tileY, (k, v) -> {
            if (v == null || v.isInvalidated()) {
                return new SettlementRoom(this.data, this.rooms, tileX, tileY);
            }
            return v;
        });
    }

    public void clearOutsideBounds(Rectangle tileRectangle) {
        LinkedList<Point> invalidRoomTiles = new LinkedList<Point>();
        for (Point tile : this.rooms.getKeys()) {
            if (tileRectangle.contains(tile.x, tile.y)) continue;
            invalidRoomTiles.add(tile);
        }
        if (!invalidRoomTiles.isEmpty()) {
            for (Point tile : invalidRoomTiles) {
                SettlementRoom room = this.rooms.remove(tile.x, tile.y);
                room.invalidate();
            }
        }
    }
}

