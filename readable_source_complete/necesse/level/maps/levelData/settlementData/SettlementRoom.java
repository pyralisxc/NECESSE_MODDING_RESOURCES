/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.util.PointHashMap;
import necesse.entity.mobs.friendly.human.HappinessModifier;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.furniture.RoomFurniture;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.settler.RoomQuality;
import necesse.level.maps.levelData.settlementData.settler.RoomSize;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;

public class SettlementRoom {
    public final ServerSettlementData data;
    private final PointHashMap<SettlementRoom> roomsMap;
    public final int tileX;
    public final int tileY;
    private boolean calculatedStats = false;
    private boolean invalidated;
    private final HashMap<String, Integer> roomProperties = new HashMap();
    private final HashMap<String, Integer> furnitureTypes = new HashMap();
    private final ArrayList<SettlementBed> beds = new ArrayList();
    private final List<HappinessModifier> happinessModifiers = new ArrayList<HappinessModifier>();
    private int happinessScore = 0;

    public SettlementRoom(ServerSettlementData data, PointHashMap<SettlementRoom> roomsMap, int tileX, int tileY) {
        this.data = data;
        this.roomsMap = roomsMap;
        this.tileX = tileX;
        this.tileY = tileY;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected void calculateStats() {
        try {
            if (this.calculatedStats) {
                return;
            }
            this.roomProperties.clear();
            this.furnitureTypes.clear();
            this.beds.clear();
            this.happinessModifiers.clear();
            if (this.getLevel().isOutside(this.tileX, this.tileY)) {
                return;
            }
            ConnectedSubRegionsResult room = this.getLevel().regionManager.getRoomConnectedByTile(this.tileX, this.tileY, true, 2000);
            if (room == null) {
                return;
            }
            int size = this.getLevel().getRoomSize(this.tileX, this.tileY);
            this.addRoomProperty("size", size);
            HashSet<SubRegion> doors = new HashSet<SubRegion>();
            for (SubRegion roomSr : room.connectedRegions) {
                for (SubRegion adj : roomSr.getAdjacentRegions()) {
                    if (adj.getType() != RegionType.DOOR || doors.contains(adj) || !adj.streamAdjacentRegions().anyMatch(adjDoor -> adjDoor.getType().roomInt == RegionType.OPEN.roomInt && !room.connectedRegions.contains(adjDoor))) continue;
                    doors.add(adj);
                }
                for (Point p : roomSr.getLevelTiles()) {
                    SettlementBed bed;
                    if (!this.data.networkData.isTileWithinBounds(p.x, p.y)) continue;
                    for (int layer = 0; layer < ObjectLayerRegistry.getTotalLayers(); ++layer) {
                        String type;
                        GameObject object = this.getLevel().getObject(layer, p.x, p.y);
                        object.roomProperties.forEach(s -> this.addRoomProperty((String)s, 1));
                        if (!(object instanceof RoomFurniture) || (type = ((RoomFurniture)((Object)object)).getFurnitureType()) == null) continue;
                        this.addFurnitureType(type, 1);
                    }
                    GameTile tile = this.getLevel().getTile(p.x, p.y);
                    tile.roomProperties.forEach(s -> this.addRoomProperty((String)s, 1));
                    if (!tile.isFloor) {
                        this.addRoomProperty("outsidefloor", 1);
                    }
                    if ((bed = this.data.addOrValidateBed(p.x, p.y, true)) != null) {
                        this.beds.add(bed);
                    }
                    if (this.roomsMap == null) continue;
                    this.roomsMap.put(p.x, p.y, this);
                }
            }
            this.addRoomProperty("doors", doors.size());
        }
        finally {
            this.calculatedStats = true;
        }
        this.calculateHappinessModifiers();
    }

    public void invalidate() {
        this.invalidated = true;
    }

    public boolean isInvalidated() {
        return this.invalidated;
    }

    public void recalculateStats() {
        this.calculatedStats = false;
    }

    public ServerSettlementData getData() {
        return this.data;
    }

    public Level getLevel() {
        return this.data.getLevel();
    }

    private void addRoomProperty(String property, int amount) {
        this.roomProperties.compute(property, (key, i) -> {
            if (i == null) {
                return amount;
            }
            return i + amount;
        });
    }

    private void addFurnitureType(String type, int amount) {
        this.furnitureTypes.compute(type, (key, i) -> {
            if (i == null) {
                return amount;
            }
            return i + amount;
        });
    }

    public int getRoomProperty(String property) {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return this.roomProperties.getOrDefault(property, 0);
    }

    public int getFurnitureTypes(String type) {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return this.furnitureTypes.getOrDefault(type, 0);
    }

    public int getFurnitureScore() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return this.furnitureTypes.values().stream().mapToInt(i -> (int)Math.pow(i.intValue(), 0.44)).sum();
    }

    public int getRoomSize() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return this.roomProperties.getOrDefault("size", 0);
    }

    public int getOccupiedBeds() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return (int)this.beds.stream().filter(b -> b.getSettler() != null).count();
    }

    public Collection<SubRegion> getSubRegions() {
        if (this.getLevel().isOutside(this.tileX, this.tileY)) {
            return new HashSet<SubRegion>();
        }
        ConnectedSubRegionsResult room = this.data.getLevel().regionManager.getRoomConnectedByTile(this.tileX, this.tileY, true, 2000);
        if (room == null) {
            return new HashSet<SubRegion>();
        }
        return room.connectedRegions;
    }

    private void calculateHappinessModifiers() {
        int occupiedBeds;
        RoomQuality roomQuality;
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        this.happinessModifiers.clear();
        this.happinessScore = 0;
        RoomSize roomSize = Settler.getRoomSize(this.getRoomSize());
        if (roomSize != null) {
            this.happinessModifiers.add(roomSize.getModifier());
        }
        if ((roomQuality = Settler.getRoomQuality(this.getFurnitureScore())) != null) {
            this.happinessModifiers.add(roomQuality.getModifier());
        }
        if (this.getRoomProperty("outsidefloor") > 0) {
            this.happinessModifiers.add(new HappinessModifier(-10, new GameMessageBuilder().append("settlement", "roommissingfloor")));
        }
        if (this.getRoomProperty("lights") <= 0) {
            this.happinessModifiers.add(new HappinessModifier(-10, new GameMessageBuilder().append("settlement", "roommissinglights")));
        }
        if ((occupiedBeds = this.getOccupiedBeds()) > 1) {
            int penalty = Math.min(10 + (occupiedBeds - 1) * 10, 50);
            this.happinessModifiers.add(new HappinessModifier(-penalty, new GameMessageBuilder().append("settlement", "sharingroom").append(" (" + (occupiedBeds - 1) + ")")));
        }
        this.happinessScore = this.happinessModifiers.stream().mapToInt(m -> m.happiness).sum();
    }

    public List<HappinessModifier> getHappinessModifiers() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        return this.happinessModifiers;
    }

    public int getHappinessScore() {
        return this.happinessScore;
    }

    public List<String> getDebugTooltips() {
        if (!this.calculatedStats) {
            this.calculateStats();
        }
        LinkedList<String> list = new LinkedList<String>();
        if (!this.roomProperties.isEmpty()) {
            list.add("Room properties:");
            for (Map.Entry<String, Integer> e : this.roomProperties.entrySet()) {
                list.add("  " + e.getKey() + ": " + e.getValue());
            }
        }
        if (!this.furnitureTypes.isEmpty()) {
            list.add("Furniture score: " + this.getFurnitureScore());
            for (Map.Entry<String, Integer> e : this.furnitureTypes.entrySet()) {
                list.add("  " + e.getKey() + ": " + e.getValue());
            }
        }
        return list;
    }
}

