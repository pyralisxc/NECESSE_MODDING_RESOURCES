/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.IncursionPerksRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.OneWorldMigration;
import necesse.engine.world.ReturnedObjects;
import necesse.engine.world.worldData.incursions.OpenIncursion;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.fallenAltar.FallenAltarContainer;
import necesse.inventory.item.miscItem.GatewayTabletItem;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.AltarData;
import necesse.level.maps.incursion.IncursionData;
import necesse.level.maps.incursion.UniqueIncursionModifier;

public class FallenAltarObjectEntity
extends ObjectEntity
implements OEInventory {
    public Inventory inventory = new Inventory(1){

        @Override
        public void updateSlot(int slot) {
            IncursionData incursionData;
            super.updateSlot(slot);
            InventoryItem item = this.getItem(slot);
            if (item != null && item.item instanceof GatewayTabletItem && (incursionData = GatewayTabletItem.getIncursionData(item)) == null) {
                GatewayTabletItem.initializeGatewayTablet(item, new GameRandom(), 1, incursionData);
                this.markDirty(slot);
            }
        }
    };
    public OpenIncursion currentOpenIncursion;
    public AltarData altarData;

    public FallenAltarObjectEntity(Level level, int x, int y) {
        super(level, "fallenaltar", x, y);
        this.inventory.filter = (slot, item) -> item == null || item.item.getStringID().equals("gatewaytablet");
        this.altarData = new AltarData();
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory, "INVENTORY"));
        if (this.currentOpenIncursion != null) {
            SaveData openIncursionData = new SaveData("openIncursion");
            this.currentOpenIncursion.addSaveData(openIncursionData);
            save.addSaveData(openIncursionData);
        }
        SaveData altarDataSave = new SaveData("altarData");
        this.altarData.addSaveData(altarDataSave);
        save.addSaveData(altarDataSave);
    }

    @Override
    public void applyLoadData(LoadData save) {
        LoadData altarDataSave;
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
        LoadData openIncursionData = save.getFirstLoadDataByName("openIncursion");
        if (openIncursionData != null) {
            try {
                this.currentOpenIncursion = new OpenIncursion(openIncursionData);
            }
            catch (Exception e) {
                System.err.println("Could not load open incursion from altar at: " + this.tileX + "," + this.tileY);
                e.printStackTrace();
            }
        } else {
            this.currentOpenIncursion = null;
        }
        if ((altarDataSave = save.getFirstLoadDataByName("altarData")) != null) {
            try {
                this.altarData.applyLoadData(altarDataSave);
            }
            catch (Exception e) {
                System.err.println("Could not load altar data at: " + this.tileX + "," + this.tileY);
                e.printStackTrace();
            }
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
        writer.putNextBoolean(this.currentOpenIncursion != null);
        if (this.currentOpenIncursion != null) {
            this.currentOpenIncursion.writePacket(writer);
        }
        this.altarData.writePacket(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
        this.currentOpenIncursion = reader.getNextBoolean() ? new OpenIncursion(reader) : null;
        this.altarData.applyPacket(reader);
    }

    public InventoryItem getGatewayTablet() {
        InventoryItem invItem = this.inventory.getItem(0);
        if (invItem != null && invItem.item.getStringID().equals("gatewaytablet")) {
            return invItem;
        }
        return null;
    }

    public boolean hasOpenIncursion() {
        return this.currentOpenIncursion != null;
    }

    public OpenIncursion getOpenIncursion() {
        return this.currentOpenIncursion;
    }

    public void openIncursion(FallenAltarContainer container, IncursionData incursion, ServerClient client) {
        if (!this.isServer()) {
            throw new IllegalStateException("Only the server can open incursions");
        }
        InventoryItem invItem = this.getGatewayTablet();
        if (invItem != null) {
            Server server = this.getLevel().getServer();
            LevelIdentifier identifier = new LevelIdentifier("incursion" + incursion.getUniqueID());
            IncursionData newIncursionData = IncursionData.makeCopy(incursion);
            this.currentOpenIncursion = new OpenIncursion(identifier, newIncursionData);
            for (Integer obtainedPerkID : this.altarData.obtainedPerkIDs) {
                if (IncursionPerksRegistry.getPerk((int)obtainedPerkID.intValue()).affectsCurrentIncursion) {
                    this.currentOpenIncursion.incursionData.currentIncursionPerkIDs.add(obtainedPerkID);
                    continue;
                }
                this.currentOpenIncursion.incursionData.nextIncursionPerkIDs.add(obtainedPerkID);
            }
            this.currentOpenIncursion.incursionData.nextIncursionModifiers.updateModifiers();
            this.currentOpenIncursion.incursionData.onOpened(container, client);
            this.inventory.setItem(0, null);
            this.markDirty();
        }
    }

    public void closeOpenIncursion(FallenAltarContainer container, ServerClient client, ReturnedObjects returnedObjects) {
        if (!this.isServer()) {
            throw new IllegalStateException("Only the server can open incursions");
        }
        if (this.currentOpenIncursion != null) {
            this.currentOpenIncursion.incursionData.onClosed(this, client);
            this.getServer().world.levelManager.deleteLevel(this.currentOpenIncursion.incursionLevelIdentifier, returnedObjects);
            this.currentOpenIncursion = null;
            this.markDirty();
        }
    }

    public boolean markCanComplete(IncursionLevel currentLevel) {
        if (!this.isServer()) {
            throw new IllegalStateException("Only the server can mark incursions as complete");
        }
        if (this.currentOpenIncursion != null) {
            this.currentOpenIncursion.canComplete = true;
            if (this.altarData != null) {
                for (Integer obtainedPerkID : this.altarData.obtainedPerkIDs) {
                    IncursionPerksRegistry.getPerk(obtainedPerkID).onIncursionLevelCompleted(currentLevel, this.altarData, obtainedPerkID);
                }
            }
            ArrayList<UniqueIncursionModifier> uniqueIncursionModifiers = this.currentOpenIncursion.incursionData.getUniqueIncursionModifiers();
            for (int i = 0; i < uniqueIncursionModifiers.size(); ++i) {
                uniqueIncursionModifiers.get(i).onIncursionLevelCompleted(currentLevel, i);
            }
            this.getServer().network.sendToClientsAtEntireLevel((Packet)new PacketChatMessage(new LocalMessage("ui", "incursionnowcomplete")), this.currentOpenIncursion.incursionLevelIdentifier);
            this.markDirty();
            return true;
        }
        return false;
    }

    public void completeOpenIncursion(FallenAltarContainer container, ServerClient client, ReturnedObjects returnedObjects) {
        if (!this.isServer()) {
            throw new IllegalStateException("Only the server can open incursions");
        }
        if (this.currentOpenIncursion != null) {
            this.getServer().world.levelManager.deleteLevel(this.currentOpenIncursion.incursionLevelIdentifier, returnedObjects);
            this.currentOpenIncursion = null;
            this.markDirty();
        }
    }

    public void enterIncursion(ServerClient client) {
        OpenIncursion openIncursion;
        if (!this.isServer()) {
            throw new IllegalStateException("Only the server can enter incursions");
        }
        if (this.currentOpenIncursion != null && (openIncursion = this.getOpenIncursion()) != null) {
            client.setFallbackLevel(this.getLevel(), this.tileX, this.tileY);
            client.changeLevel(this.currentOpenIncursion.incursionLevelIdentifier, identifier -> {
                IncursionLevel newIncursionLevel = openIncursion.incursionData.getNewIncursionLevel(this, (LevelIdentifier)identifier, client.getServer(), client.getServer().world.worldEntity, this.altarData);
                newIncursionLevel.altarLevelIdentifier = this.getLevel().getIdentifier();
                newIncursionLevel.altarTileX = this.tileX;
                newIncursionLevel.altarTileY = this.tileY;
                newIncursionLevel.fallbackIdentifier = this.getLevel().getIdentifier();
                newIncursionLevel.fallbackTilePos = new Point(this.tileX, this.tileY);
                for (Integer incursionPerkID : openIncursion.incursionData.currentIncursionPerkIDs) {
                    IncursionPerksRegistry.getPerk(incursionPerkID).onIncursionLevelGenerated(newIncursionLevel, this.altarData, incursionPerkID);
                }
                ArrayList<UniqueIncursionModifier> uniqueIncursionModifiers = openIncursion.incursionData.getUniqueIncursionModifiers();
                for (int i = 0; i < uniqueIncursionModifiers.size(); ++i) {
                    uniqueIncursionModifiers.get(i).onIncursionLevelGenerated(newIncursionLevel, i);
                }
                return newIncursionLevel;
            }, newLevel -> {
                Point returnPortalPosition = newLevel instanceof IncursionLevel ? ((IncursionLevel)newLevel).getReturnPortalPosition() : new Point(newLevel.tileWidth / 2 * 32, newLevel.tileHeight / 2 * 32);
                return client.getPlayerPosFromTile((Level)newLevel, GameMath.getTileCoordinate(returnPortalPosition.x), GameMath.getTileCoordinate(returnPortalPosition.y));
            }, true);
        }
    }

    @Override
    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> list = new ArrayList<InventoryItem>();
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i)) continue;
            list.add(this.inventory.getItem(i));
        }
        return list;
    }

    @Override
    public void clientTick() {
        super.clientTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
    }

    @Override
    public void serverTick() {
        super.serverTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        this.serverTickInventorySync(this.getLevel().getServer(), this);
    }

    @Override
    public void markClean() {
        super.markClean();
        this.inventory.clean();
    }

    @Override
    public Inventory getInventory() {
        return this.inventory;
    }

    @Override
    public GameMessage getInventoryName() {
        return this.getObject().getLocalization();
    }

    @Override
    public boolean canSetInventoryName() {
        return false;
    }

    @Override
    public boolean canQuickStackInventory() {
        return false;
    }

    @Override
    public boolean canRestockInventory() {
        return false;
    }

    @Override
    public boolean canSortInventory() {
        return false;
    }

    @Override
    public boolean canUseForNearbyCrafting() {
        return false;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        return null;
    }

    @Override
    public void migrateToOneWorld(OneWorldMigration migrationData, LevelIdentifier oldLevelIdentifier, Point tileOffset, Point positionOffset) {
        super.migrateToOneWorld(migrationData, oldLevelIdentifier, tileOffset, positionOffset);
        if (this.currentOpenIncursion != null && this.currentOpenIncursion.incursionLevelIdentifier != null) {
            migrationData.addFoundOpenIncursion(this.currentOpenIncursion.incursionLevelIdentifier);
        }
    }
}

