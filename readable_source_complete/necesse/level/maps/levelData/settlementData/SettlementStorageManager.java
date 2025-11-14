/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.LinkedList;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashMap;
import necesse.inventory.container.settlement.events.SettlementSingleStorageEvent;
import necesse.inventory.container.settlement.events.SettlementSingleWorkstationsEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;

public class SettlementStorageManager {
    public final ServerSettlementData data;
    private PointHashMap<SettlementInventory> storage = new PointHashMap();
    private PointHashMap<SettlementWorkstation> workstations = new PointHashMap();

    public SettlementStorageManager(ServerSettlementData data) {
        this.data = data;
    }

    public void addSaveData(SaveData save) {
        SaveData storageSave = new SaveData("STORAGE");
        for (SettlementInventory sInv : this.storage.values()) {
            SaveData inventorySave = new SaveData("INVENTORY");
            sInv.addSaveData(inventorySave);
            storageSave.addSaveData(inventorySave);
        }
        save.addSaveData(storageSave);
        SaveData stationsSave = new SaveData("WORKSTATIONS");
        for (SettlementWorkstation cStation : this.workstations.values()) {
            SaveData stationSave = new SaveData("WORKSTATION");
            cStation.addSaveData(stationSave);
            stationsSave.addSaveData(stationSave);
        }
        save.addSaveData(stationsSave);
    }

    public void applyLoadData(LoadData save, int tileXOffset, int tileYOffset) {
        this.storage = new PointHashMap();
        LoadData storageSave = save.getFirstLoadDataByName("STORAGE");
        if (storageSave != null) {
            storageSave.getLoadDataByName("INVENTORY").stream().filter(LoadData::isArray).forEachOrdered(c -> {
                try {
                    SettlementInventory inventory = SettlementInventory.fromLoadData(this.data.getLevel(), c, tileXOffset, tileYOffset);
                    this.storage.put(inventory.tileX, inventory.tileY, inventory);
                }
                catch (LoadDataException e) {
                    System.err.println("Could not load settlement inventory at level " + this.data.getLevel().getIdentifier() + ": " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Unknown error loading settlement inventory at level " + this.data.getLevel().getIdentifier());
                    e.printStackTrace();
                }
            });
        }
        this.workstations = new PointHashMap();
        LoadData stationsSave = save.getFirstLoadDataByName("WORKSTATIONS");
        if (stationsSave != null) {
            stationsSave.getLoadDataByName("WORKSTATION").stream().filter(LoadData::isArray).forEachOrdered(c -> {
                try {
                    SettlementWorkstation station = new SettlementWorkstation(this.data, (LoadData)c, tileXOffset, tileYOffset);
                    this.workstations.put(station.tileX, station.tileY, station);
                }
                catch (LoadDataException e) {
                    System.err.println("Could not load settlement work station at level " + this.data.getLevel().getIdentifier() + ": " + e.getMessage());
                }
                catch (Exception e) {
                    System.err.println("Unknown error loading settlement work station at level " + this.data.getLevel().getIdentifier());
                    e.printStackTrace();
                }
            });
        }
    }

    public void clearOutsideBounds(Rectangle tileRectangle) {
        LinkedList<Point> storageRemoves = new LinkedList<Point>();
        for (SettlementInventory inventory : this.storage.values()) {
            if (tileRectangle.contains(inventory.tileX, inventory.tileY)) continue;
            storageRemoves.add(new Point(inventory.tileX, inventory.tileY));
        }
        for (Point tile : storageRemoves) {
            this.storage.remove(tile.x, tile.y);
            new SettlementSingleStorageEvent(this.data, tile.x, tile.y).applyAndSendToClientsAt(this.data.getLevel());
        }
        LinkedList<Point> workstationRemoves = new LinkedList<Point>();
        for (SettlementWorkstation workstation : this.workstations.values()) {
            if (tileRectangle.contains(workstation.tileX, workstation.tileY)) continue;
            workstationRemoves.add(new Point(workstation.tileX, workstation.tileY));
        }
        for (Point tile : workstationRemoves) {
            this.workstations.remove(tile.x, tile.y);
            new SettlementSingleWorkstationsEvent(this.data, tile.x, tile.y).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public void clearInvalids() {
        LinkedList<Point> storageRemoves = new LinkedList<Point>();
        Rectangle tileRectangle = this.data.networkData.getTileRectangle();
        for (SettlementInventory inventory : this.storage.values()) {
            if (tileRectangle.contains(inventory.tileX, inventory.tileY) && inventory.isValid() && inventory.getInventoryRange() != null) continue;
            storageRemoves.add(new Point(inventory.tileX, inventory.tileY));
        }
        for (Point tile : storageRemoves) {
            this.storage.remove(tile.x, tile.y);
            new SettlementSingleStorageEvent(this.data, tile.x, tile.y).applyAndSendToClientsAt(this.data.getLevel());
        }
        LinkedList<Point> workstationRemoves = new LinkedList<Point>();
        for (SettlementWorkstation workstation : this.workstations.values()) {
            if (tileRectangle.contains(workstation.tileX, workstation.tileY) && workstation.isValid()) continue;
            workstationRemoves.add(new Point(workstation.tileX, workstation.tileY));
        }
        for (Point tile : workstationRemoves) {
            this.workstations.remove(tile.x, tile.y);
            new SettlementSingleWorkstationsEvent(this.data, tile.x, tile.y).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public boolean hasInventory(SettlementInventory inventory) {
        return this.storage.get(inventory.tileX, inventory.tileY) == inventory;
    }

    public boolean hasWorkstation(SettlementWorkstation workstation) {
        return this.workstations.get(workstation.tileX, workstation.tileY) == workstation;
    }

    public Collection<SettlementInventory> getStorage() {
        return this.storage.values();
    }

    public Collection<SettlementWorkstation> getWorkstations() {
        return this.workstations.values();
    }

    public SettlementInventory assignStorage(int tileX, int tileY, boolean sendUpdate) {
        SettlementInventory next;
        if (!this.data.networkData.isTileWithinBounds(tileX, tileY)) {
            return null;
        }
        SettlementInventory out = this.storage.get(tileX, tileY);
        if (out == null && (next = new SettlementInventory(this.data.getLevel(), tileX, tileY)).isValid()) {
            this.storage.put(tileX, tileY, next);
            out = next;
        }
        if (sendUpdate) {
            new SettlementSingleStorageEvent(this.data, tileX, tileY).applyAndSendToClientsAt(this.data.getLevel());
        }
        return out;
    }

    public void removeStorage(int tileX, int tileY, boolean sendUpdate) {
        this.storage.remove(tileX, tileY);
        if (sendUpdate) {
            new SettlementSingleStorageEvent(this.data, tileX, tileY).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public SettlementInventory getStorage(int tileX, int tileY) {
        return this.storage.get(tileX, tileY);
    }

    public SettlementWorkstation assignWorkstation(int tileX, int tileY, boolean sendUpdate) {
        SettlementWorkstation next;
        if (!this.data.networkData.isTileWithinBounds(tileX, tileY)) {
            return null;
        }
        SettlementWorkstation out = this.workstations.get(tileX, tileY);
        if (out == null && (next = new SettlementWorkstation(this.data, tileX, tileY)).isValid()) {
            this.workstations.put(tileX, tileY, next);
            out = next;
        }
        if (sendUpdate) {
            new SettlementSingleWorkstationsEvent(this.data, tileX, tileY).applyAndSendToClientsAt(this.data.getLevel());
        }
        return out;
    }

    public void removeWorkstation(int tileX, int tileY, boolean sendUpdate) {
        this.workstations.remove(tileX, tileY);
        if (sendUpdate) {
            new SettlementSingleWorkstationsEvent(this.data, tileX, tileY).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public SettlementWorkstation getWorkstation(int tileX, int tileY) {
        return this.workstations.get(tileX, tileY);
    }
}

