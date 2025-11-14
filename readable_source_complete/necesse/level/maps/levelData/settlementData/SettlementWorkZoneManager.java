/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import necesse.engine.GameLog;
import necesse.engine.network.server.ServerClient;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.container.settlement.events.SettlementWorkZoneChangedEvent;
import necesse.inventory.container.settlement.events.SettlementWorkZoneRemovedEvent;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZoneRegistry;

public class SettlementWorkZoneManager {
    public final ServerSettlementData data;
    protected HashMap<Integer, SettlementWorkZone> zones = new HashMap();

    public SettlementWorkZoneManager(ServerSettlementData data) {
        this.data = data;
    }

    public void addSaveData(SaveData save) {
        for (SettlementWorkZone zone : this.zones.values()) {
            if (zone.shouldRemove()) continue;
            SaveData zoneSave = new SaveData("ZONE");
            zoneSave.addUnsafeString("stringID", zone.getStringID());
            SaveData zoneData = new SaveData("DATA");
            zone.addSaveData(zoneData);
            zoneSave.addSaveData(zoneData);
            save.addSaveData(zoneSave);
        }
    }

    public SettlementWorkZoneManager(ServerSettlementData data, LoadData save, int tileXOffset, int tileYOffset) {
        this.data = data;
        save.getLoadDataByName("ZONE").stream().filter(c -> c.isArray()).forEachOrdered(c -> {
            try {
                String zoneStringID = c.getUnsafeString("stringID", null);
                if (zoneStringID == null) {
                    throw new LoadDataException("Could not load zone stringID");
                }
                LoadData zoneSave = c.getFirstLoadDataByName("DATA");
                if (zoneSave == null) {
                    throw new LoadDataException("Could not lod zone data");
                }
                int zoneID = SettlementWorkZoneRegistry.getZoneID(zoneStringID);
                if (zoneID == -1) {
                    throw new LoadDataException("Could not find zone with stringID " + zoneStringID);
                }
                SettlementWorkZone zone = SettlementWorkZoneRegistry.getNewZone(zoneID);
                zone.applySaveData(zoneSave, this.zones.values(), tileXOffset, tileYOffset);
                if (!zone.shouldRemove()) {
                    zone.fixOverlaps((x, y) -> this.zones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                    this.zones.put(zone.getUniqueID(), zone);
                    zone.init(this);
                }
            }
            catch (LoadDataException e) {
                System.err.println("Could not load work zone at level " + data.getLevel().getIdentifier() + ": " + e.getMessage());
            }
            catch (Exception e) {
                System.err.println("Unknown error loading work zone at level " + data.getLevel().getIdentifier());
                e.printStackTrace();
            }
        });
    }

    public void tickSecond() {
        LinkedList<Integer> removedZones = new LinkedList<Integer>();
        for (Map.Entry<Integer, SettlementWorkZone> entry : this.zones.entrySet()) {
            SettlementWorkZone zone = entry.getValue();
            if (zone.getUniqueID() != entry.getKey().intValue()) {
                GameLog.warn.println("Settlement zone had their unique ID changed, causing it to be removed");
                removedZones.add(entry.getKey());
                continue;
            }
            if (zone.isRemoved() || zone.shouldRemove()) {
                removedZones.add(entry.getKey());
                continue;
            }
            zone.tickSecond();
        }
        Iterator<Map.Entry<Integer, SettlementWorkZone>> iterator = removedZones.iterator();
        while (iterator.hasNext()) {
            int zoneUniqueID = (Integer)((Object)iterator.next());
            SettlementWorkZone removed = this.zones.remove(zoneUniqueID);
            if (removed != null) {
                removed.remove();
            }
            new SettlementWorkZoneRemovedEvent(this.data, zoneUniqueID).applyAndSendToClientsAt(this.data.getLevel());
        }
    }

    public void tickJobs() {
        for (SettlementWorkZone zone : this.zones.values()) {
            zone.tickJobs();
        }
    }

    public HashMap<Integer, SettlementWorkZone> getZones() {
        return this.zones;
    }

    public boolean clearOutsideBounds(Rectangle tileRectangle) {
        boolean changed = false;
        ArrayList<Integer> invalidZones = new ArrayList<Integer>();
        for (SettlementWorkZone zone : this.zones.values()) {
            if (!zone.limitZoneToBounds(tileRectangle, this.data.getFlagTile())) continue;
            if (zone.shouldRemove()) {
                invalidZones.add(zone.getUniqueID());
            } else {
                new SettlementWorkZoneChangedEvent(this.data, zone).applyAndSendToClientsAt(this.data.getLevel());
            }
            changed = true;
        }
        Iterator<SettlementWorkZone> iterator = invalidZones.iterator();
        while (iterator.hasNext()) {
            int zoneUniqueID = (Integer)((Object)iterator.next());
            SettlementWorkZone zone = this.zones.remove(zoneUniqueID);
            zone.remove();
            new SettlementWorkZoneRemovedEvent(this.data, zoneUniqueID).applyAndSendToClientsAt(this.data.getLevel());
        }
        return changed;
    }

    public SettlementWorkZone getZone(int uniqueID) {
        return this.zones.get(uniqueID);
    }

    public boolean removeZone(int uniqueID) {
        SettlementWorkZone zone = this.getZone(uniqueID);
        if (zone == null) {
            return false;
        }
        this.zones.remove(uniqueID);
        zone.remove();
        new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClientsAt(this.data.getLevel());
        return true;
    }

    public SettlementWorkZone expandZone(int uniqueID, Rectangle rectangle, Point anchor, ServerClient client) {
        SettlementWorkZone zone = this.getZone(uniqueID);
        if (zone == null) {
            if (client != null) {
                new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClient(client);
            }
        } else {
            Rectangle tileRectangle = this.data.networkData.getTileRectangle();
            if (zone.expandZone(this.data.getLevel(), rectangle, anchor, (Integer x, Integer y) -> !tileRectangle.contains((int)x, (int)y) || this.zones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)))) {
                new SettlementWorkZoneChangedEvent(this.data, zone).applyAndSendToClientsAt(this.data.getLevel());
            }
            return zone;
        }
        return null;
    }

    public void shrinkZone(int uniqueID, Rectangle rectangle, ServerClient client) {
        SettlementWorkZone zone = this.getZone(uniqueID);
        if (zone == null) {
            if (client != null) {
                new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClient(client);
            }
        } else if (zone.shrinkZone(this.data.getLevel(), rectangle)) {
            if (zone.shouldRemove()) {
                zone.remove();
                new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClientsAt(this.data.getLevel());
            } else {
                new SettlementWorkZoneChangedEvent(this.data, zone).applyAndSendToClientsAt(this.data.getLevel());
            }
        }
    }

    public SettlementWorkZone createZone(int zoneID, int uniqueID, Rectangle rectangle, Point anchor, ServerClient client) {
        try {
            SettlementWorkZone prev = this.getZone(uniqueID);
            if (prev != null) {
                new SettlementWorkZoneChangedEvent(this.data, prev).applyAndSendToClient(client);
            } else {
                SettlementWorkZone next = SettlementWorkZoneRegistry.getNewZone(zoneID);
                next.setUniqueID(uniqueID);
                Rectangle tileRectangle = this.data.networkData.getTileRectangle();
                next.expandZone(this.data.getLevel(), rectangle, anchor, (Integer x, Integer y) -> !tileRectangle.contains((int)x, (int)y) || this.zones.values().stream().anyMatch(z -> z.containsTile((int)x, (int)y)));
                if (!next.shouldRemove()) {
                    next.generateDefaultName(this.zones.values());
                    this.zones.put(uniqueID, next);
                    next.init(this);
                    new SettlementWorkZoneChangedEvent(this.data, next).applyAndSendToClientsAt(this.data.getLevel());
                    return next;
                }
                next.remove();
                new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClient(client);
            }
        }
        catch (Exception e) {
            new SettlementWorkZoneRemovedEvent(this.data, uniqueID).applyAndSendToClient(client);
        }
        return null;
    }
}

