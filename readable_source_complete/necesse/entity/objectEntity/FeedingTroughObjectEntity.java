/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketTroughFeed;
import necesse.engine.network.server.Server;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.FeedingTroughMob;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.consumableItem.food.GrainItem;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.ConnectedSubRegionsResult;
import necesse.level.maps.regionSystem.RegionType;
import necesse.level.maps.regionSystem.SubRegion;

public class FeedingTroughObjectEntity
extends ObjectEntity
implements OEInventory {
    public static int feedTileRange = 12;
    public static final int INV_SIZE = 10;
    public final Inventory inventory = new Inventory(10){

        @Override
        public void updateSlot(int slot) {
            super.updateSlot(slot);
            FeedingTroughObjectEntity.this.updateFeed = true;
        }
    };
    private boolean updateFeed;
    private boolean hasFeed;
    private int feedTimer;

    public FeedingTroughObjectEntity(Level level, int x, int y) {
        super(level, "feedingtrough", x, y);
        this.inventory.filter = (slot, item) -> item == null || this.isValidFeed(item.item);
        this.updateFeed = true;
        this.feedTimer = 200;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory));
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
        writer.putNextBoolean(this.hasFeed);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
        this.hasFeed = reader.getNextBoolean();
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
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        Server server = this.getLevel().getServer();
        this.serverTickInventorySync(server, this);
        if (this.updateFeed) {
            this.updateFeed();
        }
        if (this.hasFeed() && server != null) {
            --this.feedTimer;
            if (this.feedTimer <= 0) {
                int maxSize = 2304;
                ConnectedSubRegionsResult connected = this.getLevel().regionManager.getSubRegionByTile(this.tileX, this.tileY).getAllConnected(sr -> sr.getType() == RegionType.OPEN || sr.getType() == RegionType.SOLID, maxSize);
                Set regionIDs = connected.connectedRegions.stream().map(SubRegion::getRegionID).collect(Collectors.toSet());
                Point midPos = this.getMidPos();
                List mobs = this.getLevel().entityManager.mobs.getInRegionByTileRange(this.tileX, this.tileY, feedTileRange).stream().filter(m -> regionIDs.contains(this.getLevel().getRegionID(m.getTileX(), m.getTileY()))).filter(m -> m instanceof FeedingTroughMob).map(m -> (FeedingTroughMob)((Object)m)).filter(m -> !m.isOnFeedCooldown() && GameMath.diagonalMoveDistance(midPos, ((Mob)((Object)m)).getPositionPoint()) <= (double)(feedTileRange * 32)).collect(Collectors.toList());
                block0: for (int i = 0; i < this.inventory.getSize(); ++i) {
                    if (this.inventory.isSlotClear(i)) continue;
                    InventoryItem item = this.inventory.getItem(i);
                    for (FeedingTroughMob mob : mobs) {
                        if (!mob.canFeed(item)) continue;
                        if (this.isServer()) {
                            this.getServer().network.sendToClientsWithEntity(new PacketTroughFeed((HusbandryMob)mob, item.copy()), (HusbandryMob)mob);
                        }
                        mob.onFed(item);
                        if (item.getAmount() <= 0) {
                            this.inventory.clearSlot(i);
                            continue block0;
                        }
                        this.inventory.markDirty(i);
                    }
                }
                this.feedTimer = 200;
            }
        }
    }

    private Point getMidPos() {
        byte rotation = this.getLevel().getObjectRotation(this.tileX, this.tileY);
        if (rotation == 0) {
            return new Point(this.tileX * 32 + 16, this.tileY * 32);
        }
        if (rotation == 1) {
            return new Point(this.tileX * 32 + 32, this.tileY * 32 + 16);
        }
        if (rotation == 2) {
            return new Point(this.tileX * 32 + 16, this.tileY * 32 + 32);
        }
        return new Point(this.tileX * 32, this.tileY * 32 + 16);
    }

    private void updateFeed() {
        this.updateFeed = false;
        boolean oldHasFeed = this.hasFeed;
        this.hasFeed = false;
        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (this.inventory.isSlotClear(i) || !this.isValidFeed(this.inventory.getItemSlot(i))) continue;
            this.hasFeed = true;
            break;
        }
        if (oldHasFeed != this.hasFeed) {
            this.markDirty();
        }
    }

    private boolean isValidFeed(Item item) {
        if (item == null) {
            return false;
        }
        return item instanceof GrainItem;
    }

    public boolean hasFeed() {
        return this.hasFeed;
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
    public boolean isSettlementStorageItemDisabled(Item item) {
        return !this.isValidFeed(item);
    }
}

