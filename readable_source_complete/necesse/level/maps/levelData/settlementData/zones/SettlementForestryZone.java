/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.function.BooleanSupplier;
import necesse.engine.GameLog;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.VersionMigration;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementForestryZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementForestryZoneUpdateEvent;
import necesse.level.gameObject.ForestryJobObject;
import necesse.level.gameObject.ForestrySaplingObject;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.LevelObject;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.ForestryLevelJob;
import necesse.level.maps.levelData.jobs.PlantSaplingLevelJob;
import necesse.level.maps.levelData.settlementData.zones.SettlementTileTickZone;
import necesse.level.maps.levelData.settlementData.zones.SettlementWorkZone;

public class SettlementForestryZone
extends SettlementTileTickZone {
    protected boolean choppingAllowed = true;
    protected boolean replantChoppedDownTrees = true;
    protected int autoPlantSaplingID = -1;

    @Override
    public void addSaveData(SaveData save) {
        GameObject object;
        super.addSaveData(save);
        save.addBoolean("choppingAllowed", this.choppingAllowed);
        save.addBoolean("replantChoppedDownTrees", this.replantChoppedDownTrees);
        if (this.autoPlantSaplingID != -1 && (object = ObjectRegistry.getObject(this.autoPlantSaplingID)) instanceof ForestrySaplingObject) {
            save.addUnsafeString("autoPlantSaplingStringID", object.getStringID());
        }
    }

    @Override
    public void applySaveData(LoadData save, Collection<SettlementWorkZone> currentZones, int tileXOffset, int tileYOffset) {
        super.applySaveData(save, currentZones, tileXOffset, tileYOffset);
        this.choppingAllowed = save.getBoolean("choppingAllowed", this.choppingAllowed, false);
        this.replantChoppedDownTrees = save.getBoolean("replantChoppedDownTrees", this.replantChoppedDownTrees, false);
        String autoPlantSaplingStringID = save.getUnsafeString("autoPlantSaplingStringID", null, false);
        if (autoPlantSaplingStringID != null) {
            String newStringID = VersionMigration.tryFixStringID(autoPlantSaplingStringID, VersionMigration.oldObjectStringIDs);
            this.autoPlantSaplingID = ObjectRegistry.getObjectID(newStringID);
            this.validatePlantSaplingID();
        } else {
            this.autoPlantSaplingID = -1;
        }
    }

    @Override
    public void writePacket(PacketWriter writer) {
        super.writePacket(writer);
        writer.putNextBoolean(this.choppingAllowed);
        writer.putNextBoolean(this.replantChoppedDownTrees);
        writer.putNextInt(this.autoPlantSaplingID);
    }

    @Override
    public void applyPacket(PacketReader reader) {
        super.applyPacket(reader);
        this.choppingAllowed = reader.getNextBoolean();
        this.replantChoppedDownTrees = reader.getNextBoolean();
        this.autoPlantSaplingID = reader.getNextInt();
    }

    @Override
    protected void handleTile(Point tile) {
        if (this.isChoppingAllowed()) {
            LevelObject lo = this.manager.data.getLevel().getLevelObject(tile.x, tile.y);
            if (lo.object.getID() != 0 && lo.object instanceof ForestryJobObject) {
                this.manager.data.getLevel().jobsLayer.addJob(new ForestryLevelJob(tile.x, tile.y, this, false));
            }
        }
        if (!this.replantChoppedDownTrees()) {
            this.getNewPlantJob(tile.x, tile.y, null);
        }
    }

    public PlantSaplingLevelJob getNewPlantJob(int tileX, int tileY, GameObject choppedDownObject) {
        if (this.replantChoppedDownTrees()) {
            String saplingStringID;
            if (choppedDownObject instanceof ForestryJobObject && (saplingStringID = ((ForestryJobObject)((Object)choppedDownObject)).getSaplingStringID()) != null) {
                int saplingID = ObjectRegistry.getObjectID(saplingStringID);
                if (saplingID != -1) {
                    PlantSaplingLevelJob plantJob = new PlantSaplingLevelJob(tileX, tileY, saplingID, true);
                    return (PlantSaplingLevelJob)this.manager.data.getLevel().jobsLayer.addJob(plantJob);
                }
                GameLog.warn.println("Could not find forestry sapling object with stringID " + saplingStringID);
            }
        } else {
            Point first;
            int autoPlantSaplingID = this.getAutoPlantSaplingID();
            if (autoPlantSaplingID != -1 && (first = this.zoning.getTiles().first()) != null) {
                GameObject oldObject = this.manager.data.getLevel().getObject(tileX, tileY);
                if (oldObject.getID() != autoPlantSaplingID) {
                    if (oldObject instanceof ForestrySaplingObject) {
                        this.manager.data.getLevel().jobsLayer.addJob(new ForestryLevelJob(tileX, tileY, this, true));
                    } else {
                        ForestrySaplingObject newSaplingObject;
                        GameObject newObject = ObjectRegistry.getObject(autoPlantSaplingID);
                        if (newObject instanceof ForestrySaplingObject && newObject.canPlace(this.manager.data.getLevel(), tileX, tileY, 0, true) == null && (newSaplingObject = (ForestrySaplingObject)((Object)newObject)).shouldForestryPlantAtTile(first.x - tileX, first.y - tileY)) {
                            PlantSaplingLevelJob plantJob = new PlantSaplingLevelJob(tileX, tileY, autoPlantSaplingID, false);
                            return (PlantSaplingLevelJob)this.manager.data.getLevel().jobsLayer.addJob(plantJob);
                        }
                    }
                } else {
                    GameObject newObject = ObjectRegistry.getObject(autoPlantSaplingID);
                    if (newObject instanceof ForestrySaplingObject) {
                        ForestrySaplingObject newSaplingObject = (ForestrySaplingObject)((Object)newObject);
                        if (newSaplingObject.shouldForestryPlantAtTile(first.x - tileX, first.y - tileY)) {
                            PlantSaplingLevelJob plantJob = new PlantSaplingLevelJob(tileX, tileY, autoPlantSaplingID, false);
                            return (PlantSaplingLevelJob)this.manager.data.getLevel().jobsLayer.addJob(plantJob);
                        }
                        this.manager.data.getLevel().jobsLayer.addJob(new ForestryLevelJob(tileX, tileY, this, true));
                    }
                }
            }
        }
        return null;
    }

    @Override
    public boolean isHiddenSetting() {
        return Settings.hideSettlementForestryZones.get();
    }

    @Override
    public GameMessage getDefaultName(int number) {
        return new LocalMessage("ui", "settlementforestryzonedefname", "number", number);
    }

    @Override
    public GameMessage getAbstractName() {
        return new LocalMessage("ui", "settlementforestryzone");
    }

    @Override
    public HudDrawElement getHudDrawElement(int drawPriority, BooleanSupplier overrideShow) {
        return this.getHudDrawElement(drawPriority, overrideShow, new Color(217, 129, 33, 150), new Color(118, 70, 18, 75));
    }

    public boolean isChoppingAllowed() {
        return this.choppingAllowed;
    }

    public void setChoppingAllowed(boolean allowed) {
        this.choppingAllowed = allowed;
        if (this.manager != null && !this.isRemoved()) {
            new SettlementForestryZoneUpdateEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
        }
    }

    public boolean replantChoppedDownTrees() {
        return this.replantChoppedDownTrees;
    }

    public void setReplantChoppedDownTrees(boolean value) {
        this.replantChoppedDownTrees = value;
        if (this.manager != null && !this.isRemoved()) {
            new SettlementForestryZoneUpdateEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
        }
    }

    public int getAutoPlantSaplingID() {
        return this.autoPlantSaplingID;
    }

    protected boolean validatePlantSaplingID() {
        if (this.autoPlantSaplingID != -1) {
            String resultStringID;
            GameObject object = ObjectRegistry.getObject(this.autoPlantSaplingID);
            if (object instanceof ForestrySaplingObject && (resultStringID = ((ForestrySaplingObject)((Object)object)).getForestryResultObjectStringID()) != null) {
                return true;
            }
            this.autoPlantSaplingID = -1;
            return false;
        }
        return true;
    }

    public void setAutoPlantSaplingID(int saplingID) {
        if (saplingID < 0) {
            saplingID = -1;
        }
        this.autoPlantSaplingID = saplingID;
        this.validatePlantSaplingID();
        if (this.manager != null && !this.isRemoved()) {
            new SettlementForestryZoneUpdateEvent(this.manager.data, this).applyAndSendToClientsAt(this.manager.data.getLevel());
        }
    }

    @Override
    public void subscribeConfigEvents(SettlementContainer container, BooleanSupplier isActive) {
        super.subscribeConfigEvents(container, isActive);
        container.subscribeEvent(SettlementForestryZoneUpdateEvent.class, e -> e.settlementUniqueID == container.getSettlementUniqueID() && e.zoneUniqueID == this.getUniqueID(), isActive);
    }

    @Override
    public void writeSettingsForm(PacketWriter writer) {
        writer.putNextBoolean(this.choppingAllowed);
        writer.putNextBoolean(this.replantChoppedDownTrees);
        writer.putNextInt(this.autoPlantSaplingID);
    }

    @Override
    public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> assignWork, Runnable backPressed, PacketReader reader) {
        this.choppingAllowed = reader.getNextBoolean();
        this.replantChoppedDownTrees = reader.getNextBoolean();
        this.autoPlantSaplingID = reader.getNextInt();
        return new SettlementForestryZoneConfigForm(assignWork, this, backPressed);
    }
}

