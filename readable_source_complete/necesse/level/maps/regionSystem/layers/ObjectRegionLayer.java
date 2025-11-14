/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.regionSystem.layers;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.TickManager;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.Server;
import necesse.engine.registries.ObjectLayerRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LevelSave;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.PointHashSet;
import necesse.engine.world.worldData.SettlementsWorldData;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.LevelSortedDrawable;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObject;
import necesse.level.maps.levelData.jobs.LevelJob;
import necesse.level.maps.levelData.settlementData.ServerSettlementData;
import necesse.level.maps.light.GameLight;
import necesse.level.maps.regionSystem.Region;
import necesse.level.maps.regionSystem.SimulatePriorityList;
import necesse.level.maps.regionSystem.layers.ObjectLayerAbstract;
import necesse.level.maps.regionSystem.layers.RegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.LevelJobsSubmitterRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.RegionPacketHandlerRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.SaveDataRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickEffectRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.TileTickRegionLayer;
import necesse.level.maps.regionSystem.layers.interfaces.WireUpdateRegionLayer;

public class ObjectRegionLayer
extends RegionLayer
implements TileTickRegionLayer,
TileTickEffectRegionLayer,
WireUpdateRegionLayer,
LevelJobsSubmitterRegionLayer,
RegionPacketHandlerRegionLayer,
SaveDataRegionLayer {
    protected ObjectLayerAbstract[] layers = ObjectLayerRegistry.getNewLayersArray(this);

    public ObjectRegionLayer(Region region) {
        super(region);
    }

    @Override
    public void init() {
    }

    public String getTileDebugStringByRegion(int regionTileX, int regionTileY) {
        StringBuilder builder = new StringBuilder();
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            if (builder.length() != 0) {
                builder.append(" - ");
            }
            builder.append(layerID).append(": ").append(this.getObjectIDByRegion(layerID, regionTileX, regionTileY)).append(",").append(this.getObjectRotationByRegion(layerID, regionTileX, regionTileY)).append(",").append(this.isPlayerPlacedByRegion(layerID, regionTileX, regionTileY));
        }
        return builder.toString();
    }

    public int getTileObjectsHashByRegion(int regionTileX, int regionTileY) {
        int hash = 1;
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            int objectID = this.getObjectIDByRegion(layerID, regionTileX, regionTileY);
            hash = 31 * hash + objectID;
            if (objectID == 0) continue;
            hash = 31 * hash + this.getObjectRotationByRegion(layerID, regionTileX, regionTileY);
            hash = 31 * hash + Boolean.hashCode(this.isPlayerPlacedByRegion(layerID, regionTileX, regionTileY));
        }
        return hash;
    }

    @Override
    public void writeLayerPacket(PacketWriter writer) {
        for (ObjectLayerAbstract layer : this.layers) {
            layer.writeLayerPacket(writer);
        }
    }

    @Override
    public boolean applyLayerPacket(PacketReader reader) {
        for (ObjectLayerAbstract layer : this.layers) {
            if (layer.readLayerPacket(reader)) continue;
            return false;
        }
        return true;
    }

    @Override
    public void onLayerUnloaded() {
        for (ObjectLayerAbstract layer : this.layers) {
            layer.clearLayer();
        }
    }

    @Override
    public void onLayerLoaded() {
        this.updateWireActive();
    }

    @Override
    public void onLoadingComplete() {
        this.updateWireActive();
    }

    protected void updateWireActive() {
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                GameObject obj = this.getObjectByRegion(0, regionTileX, regionTileY);
                for (int wireID = 0; wireID < 4; ++wireID) {
                    boolean isWireActive = obj.isWireActive(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, wireID);
                    this.level.wireManager.updateWire(regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, wireID, isWireActive);
                }
            }
        }
    }

    @Override
    public void simulateWorld(long worldTimeIncrease, boolean sendChanges) {
        Performance.recordConstant(LevelSave.debugLoadingPerformance, "objects", () -> {
            long seconds = worldTimeIncrease / 1000L;
            if (seconds > 0L) {
                SimulatePriorityList simulateList = new SimulatePriorityList();
                for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                    for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
                        this.getObjectByRegion(0, regionTileX, regionTileY).addSimulateLogic(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, seconds, simulateList, sendChanges);
                    }
                }
                simulateList.run();
            }
        });
    }

    @Override
    public void tickTileByRegion(int regionTileX, int regionTileY) {
        this.getObjectByRegion(0, regionTileX, regionTileY).tick(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
    }

    @Override
    public void tickTileEffectByRegion(GameCamera camera, PlayerMob perspective, int regionTileX, int regionTileY) {
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            this.getObjectByRegion(layerID, regionTileX, regionTileY).tickEffect(this.level, layerID, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
        }
    }

    @Override
    public void onWireUpdateByRegion(int regionTileX, int regionTileY, int wireID, boolean active) {
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            this.getObjectByRegion(layerID, regionTileX, regionTileY).onWireUpdate(this.level, layerID, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, wireID, active);
        }
    }

    @Override
    public List<LevelJob> getLevelJobsByRegion(int regionTileX, int regionTileY) {
        return this.getObjectByRegion(0, regionTileX, regionTileY).getLevelJobs(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
    }

    public int getObjectIDByRegion(int layerID, int regionTileX, int regionTileY) {
        return this.layers[layerID].getObjectID(regionTileX, regionTileY);
    }

    public GameObject getObjectByRegion(int layerID, int regionTileX, int regionTileY) {
        return ObjectRegistry.getObject(this.getObjectIDByRegion(layerID, regionTileX, regionTileY));
    }

    public void setObjectByRegion(int layerID, int regionTileX, int regionTileY, int objectID) {
        this.setObjectByRegion(layerID, regionTileX, regionTileY, objectID, false);
    }

    public void setObjectByRegion(int layerID, int regionTileX, int regionTileY, int objectID, boolean forceDontUpdate) {
        boolean triggerChange = false;
        ObjectLayerAbstract layer = this.layers[layerID];
        short oldObjectID = layer.getObjectID(regionTileX, regionTileY);
        int tileX = regionTileX + this.region.tileXOffset;
        int tileY = regionTileY + this.region.tileYOffset;
        if (layerID == 0) {
            if (!(this.region.isLoadingComplete() && this.level.isLoadingComplete() || forceDontUpdate)) {
                if (oldObjectID != objectID) {
                    layer.setObjectID(regionTileX, regionTileY, (short)objectID);
                    this.level.replaceObjectEntity(tileX, tileY);
                }
            } else if (oldObjectID != objectID) {
                GameObject oldObject = ObjectRegistry.getObject(oldObjectID);
                boolean[] wireActive = new boolean[4];
                for (int i = 0; i < 4; ++i) {
                    wireActive[i] = oldObject.isWireActive(this.level, tileX, tileY, i);
                }
                layer.setObjectID(regionTileX, regionTileY, (short)objectID);
                this.level.replaceObjectEntity(tileX, tileY);
                GameObject newObject = ObjectRegistry.getObject(objectID);
                for (int i = 0; i < 4; ++i) {
                    if (newObject.isWireActive(this.level, tileX, tileY, i) == wireActive[i]) continue;
                    this.level.wireManager.updateWire(tileX, tileY, i, !wireActive[i]);
                }
                this.region.subRegionData.onObjectChanged(regionTileX, regionTileY, oldObject, newObject);
                if (oldObject.stopsTerrainSplatting() != newObject.stopsTerrainSplatting()) {
                    this.manager.onSplattingChange(tileX, tileY);
                }
                triggerChange = true;
            }
            if (this.level.isClient()) {
                this.level.getClient().levelManager.updateMapTile(tileX, tileY);
            }
        } else if (oldObjectID != objectID) {
            layer.setObjectID(regionTileX, regionTileY, (short)objectID);
            boolean bl = triggerChange = (this.region.isLoadingComplete() || this.level.isLoadingComplete()) && !forceDontUpdate;
        }
        if (triggerChange) {
            ServerSettlementData settlement;
            this.level.addDirtyRegion(this.region);
            this.level.lightManager.updateStaticLight(tileX, tileY);
            if (this.level.isServer() && (settlement = SettlementsWorldData.getSettlementsData(this.level).getServerDataAtTile(this.level.getIdentifier(), tileX, tileY)) != null) {
                PointHashSet refreshTiles = new PointHashSet();
                refreshTiles.add(tileX, tileY);
                GameObject oldObject = ObjectRegistry.getObject(oldObjectID);
                GameObject newObject = ObjectRegistry.getObject(objectID);
                if (oldObject.getRegionType().roomInt != newObject.getRegionType().roomInt) {
                    refreshTiles.add(tileX - 1, tileY);
                    refreshTiles.add(tileX + 1, tileY);
                    refreshTiles.add(tileX, tileY - 1);
                    refreshTiles.add(tileX, tileY + 1);
                }
                settlement.rooms.refreshRooms(refreshTiles);
            }
        }
    }

    public byte getObjectRotationByRegion(int layerID, int regionTileX, int regionTileY) {
        return this.layers[layerID].getObjectRotation(regionTileX, regionTileY);
    }

    public void setObjectRotationByRegion(int layerID, int regionTileX, int regionTileY, int rotation) {
        this.layers[layerID].setObjectRotation(regionTileX, regionTileY, (byte)((rotation % 4 + 4) % 4));
    }

    public boolean isPlayerPlacedByRegion(int layerID, int regionTileX, int regionTileY) {
        return this.layers[layerID].isPlayerPlaced(regionTileX, regionTileY);
    }

    public void setIsPlayerPlacedByRegion(int layerID, int regionTileX, int regionTileY, boolean isPlayerPlaced) {
        this.layers[layerID].setIsPlayerPlaced(regionTileX, regionTileY, isPlayerPlaced);
    }

    public ArrayList<LevelObject> getHitboxPriorityListByRegion(int regionTileX, int regionTileY, boolean ignoreAir) {
        ArrayList<LevelObject> out = new ArrayList<LevelObject>(this.layers.length);
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            GameObject object = this.getObjectByRegion(layerID, regionTileX, regionTileY);
            if (ignoreAir && object.getID() == 0) continue;
            out.add(new LevelObject(this.level, layerID, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset));
        }
        out.sort(Comparator.comparingInt(lo -> -lo.getHitboxLayerPriority()));
        return out;
    }

    public synchronized GameLight getCombinedLightByRegion(int regionTileX, int regionTileY) {
        GameLight finalLight = null;
        for (int layerID = 0; layerID < this.layers.length; ++layerID) {
            GameLight light;
            GameObject object = this.getObjectByRegion(layerID, regionTileX, regionTileY);
            if (object.getID() == 0 || (light = object.getLight(this.level, layerID, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset)) == null) continue;
            if (finalLight == null) {
                finalLight = light;
                continue;
            }
            if (!(light.getLevel() > 0.0f)) continue;
            finalLight.combine(light);
        }
        if (finalLight == null) {
            return this.level.lightManager.newLight(0.0f, 0.0f, 0.0f);
        }
        return finalLight;
    }

    public synchronized GameObject addObjectDrawablesByRegion(List<LevelSortedDrawable> list, OrderableDrawables tileList, Level level, int regionTileX, int regionTileY, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
        GameObject object = this.getObjectByRegion(0, regionTileX, regionTileY);
        object.addDrawables(list, tileList, level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, tickManager, camera, perspective);
        for (int layerID = 1; layerID < this.layers.length; ++layerID) {
            int extraObjectID = this.getObjectIDByRegion(layerID, regionTileX, regionTileY);
            if (extraObjectID == 0) continue;
            GameObject extraObject = ObjectRegistry.getObject(extraObjectID);
            extraObject.addLayerDrawables(list, tileList, level, layerID, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset, tickManager, camera, perspective);
        }
        return object;
    }

    public synchronized void addObjectsDebugTooltipByRegion(StringTooltips tooltips, int regionTileX, int regionTileY) {
        String objectString = this.getObjectByRegion(0, regionTileX, regionTileY).getDisplayName() + " (" + this.getObjectIDByRegion(0, regionTileX, regionTileY) + "), " + this.getObjectRotationByRegion(0, regionTileX, regionTileY) + ", Placed: " + this.isPlayerPlacedByRegion(0, regionTileX, regionTileY);
        tooltips.add("Object: " + objectString);
        boolean addedExtraObjects = false;
        for (int layerID = 1; layerID < this.layers.length; ++layerID) {
            ObjectLayerAbstract layer = this.layers[layerID];
            short objectID = layer.getObjectID(regionTileX, regionTileY);
            if (objectID == 0) continue;
            if (!addedExtraObjects) {
                tooltips.add("Extra objects:");
                addedExtraObjects = true;
            }
            GameObject object = ObjectRegistry.getObject(objectID);
            tooltips.add("  " + ObjectLayerRegistry.getLayerStringID(layerID) + " (" + layerID + "): " + object.getDisplayName() + " (" + objectID + "), " + layer.getObjectRotation(regionTileX, regionTileY) + ", Placed: " + layer.isPlayerPlaced(regionTileX, regionTileY));
        }
    }

    public synchronized void clearExtraObjectsByRegion(int regionTileX, int regionTileY) {
        for (int layerID = 1; layerID < this.layers.length; ++layerID) {
            this.layers[layerID].clearTile(regionTileX, regionTileY);
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        HashSet<Integer> usedObjectIDs = new HashSet<Integer>();
        for (ObjectLayerAbstract layer : this.layers) {
            layer.addUsedObjectIDs(usedObjectIDs);
        }
        String[] objectIDs = VersionMigration.generateStringIDsArray(usedObjectIDs, ObjectRegistry::getObjectStringID);
        save.addStringArray("objectIDs", objectIDs);
        this.layers[0].addSaveData(save);
        for (int layerID = 1; layerID < this.layers.length; ++layerID) {
            String layerStringID = ObjectLayerRegistry.getLayerStringID(layerID);
            SaveData layerSave = new SaveData(layerStringID + "Objects");
            this.layers[layerID].addSaveData(layerSave);
            if (layerSave.isEmpty()) continue;
            save.addSaveData(layerSave);
        }
    }

    @Override
    public void loadSaveData(LoadData save) {
        int[] conversionArray = null;
        if (save.hasLoadDataByName("objectIDs")) {
            String[] objectIDs = save.getStringArray("objectIDs");
            conversionArray = VersionMigration.generateStringIDsArrayConversionArray(objectIDs, ObjectRegistry.getObjectStringIDs(), 0, VersionMigration.oldObjectStringIDs);
        }
        boolean migrated = this.layers[0].applyLoadData(save, conversionArray);
        for (int layerID = 1; layerID < this.layers.length; ++layerID) {
            String layerStringID = ObjectLayerRegistry.getLayerStringID(layerID);
            LoadData layerSave = save.getFirstLoadDataByName(layerStringID + "Objects");
            if (layerSave == null) continue;
            migrated = this.layers[layerID].applyLoadData(layerSave, conversionArray) || migrated;
        }
        if (migrated) {
            Server server = this.level.getServer();
            if (server != null) {
                server.migratedNames.add("object data");
                server.regionsMigrated.add(this.level.getIdentifier(), new Point(this.region.regionX, this.region.regionY));
            } else {
                System.out.println("Migrated level " + this.level.getIdentifier() + " region " + this.region.regionX + "x" + this.region.regionY + " object data");
            }
        }
        for (ObjectEntity objectEntity : this.level.entityManager.objectEntities.getInRegion(this.region.regionX, this.region.regionY)) {
            objectEntity.remove();
        }
        for (int regionTileX = 0; regionTileX < this.region.tileWidth; ++regionTileX) {
            for (int regionTileY = 0; regionTileY < this.region.tileHeight; ++regionTileY) {
                ObjectEntity objectEntity = this.getObjectByRegion(0, regionTileX, regionTileY).getNewObjectEntity(this.level, regionTileX + this.region.tileXOffset, regionTileY + this.region.tileYOffset);
                if (objectEntity == null) continue;
                this.level.entityManager.objectEntities.addHidden(objectEntity);
            }
        }
    }
}

