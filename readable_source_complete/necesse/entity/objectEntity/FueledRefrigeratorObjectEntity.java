/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.entity.objectEntity.InventoryObjectEntity;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledRefrigeratorObjectEntity
extends InventoryObjectEntity {
    public static int FUEL_TIME_ADDED = 240000;
    public int fuelSlots;
    public float spoilRate;
    public boolean forceUpdate = true;
    protected int remainingFuelTime;
    protected int usedFuelTime;
    protected long lastTickedTime;

    public FueledRefrigeratorObjectEntity(Level level, int x, int y, int fuelSlots, int inventorySlots, float spoilRate) {
        super(level, x, y, inventorySlots + fuelSlots);
        this.fuelSlots = fuelSlots;
        this.spoilRate = spoilRate;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addInt("remainingFuelTime", this.remainingFuelTime);
        save.addInt("usedFuelTime", this.usedFuelTime);
        save.addLong("lastTickedTime", this.lastTickedTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.remainingFuelTime = save.getInt("remainingFuelTime", 0);
        this.usedFuelTime = save.getInt("usedFuelTime", 0);
        this.lastTickedTime = save.getLong("lastTickedTime", 0L);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        writer.putNextInt(this.remainingFuelTime);
        writer.putNextInt(this.usedFuelTime);
        writer.putNextLong(this.lastTickedTime);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.remainingFuelTime = reader.getNextInt();
        this.usedFuelTime = reader.getNextInt();
        this.lastTickedTime = reader.getNextLong();
    }

    @Override
    protected void onInventorySlotUpdated(int slot) {
        super.onInventorySlotUpdated(slot);
        this.forceUpdate = true;
    }

    @Override
    public boolean isItemValid(int slot, InventoryItem item) {
        if (slot < this.fuelSlots) {
            return item == null || this.isFuel(item);
        }
        return super.isItemValid(slot, item);
    }

    @Override
    public void init() {
        super.init();
        this.updateInventorySpoilRate();
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.updateFuel();
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.updateFuel();
    }

    public void updateFuel() {
        long usedTime;
        long currentWorldTime = this.getWorldTime();
        long lastTime = this.lastTickedTime == 0L ? currentWorldTime : this.lastTickedTime;
        for (long addedTime = Math.max(0L, currentWorldTime - lastTime); addedTime > 0L || this.forceUpdate; addedTime -= usedTime) {
            if (this.forceUpdate) {
                this.forceUpdate = false;
                if (this.remainingFuelTime <= 0) {
                    this.tryUseFuel();
                }
            }
            boolean couldNotUseFuel = false;
            if (this.remainingFuelTime <= 0 && !this.tryUseFuel()) {
                couldNotUseFuel = true;
            }
            usedTime = Math.max(0L, GameMath.min(new long[]{this.remainingFuelTime, addedTime}));
            this.remainingFuelTime = (int)((long)this.remainingFuelTime - usedTime);
            this.usedFuelTime = (int)((long)this.usedFuelTime + usedTime);
            if (this.remainingFuelTime > 0) continue;
            this.usedFuelTime = 0;
            if (couldNotUseFuel) break;
            this.forceUpdate = true;
        }
        this.updateInventorySpoilRate();
        this.lastTickedTime = currentWorldTime;
    }

    public boolean tryUseFuel() {
        for (int i = 0; i < this.fuelSlots; ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (!this.isFuel(item)) continue;
            this.remainingFuelTime += FUEL_TIME_ADDED;
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                this.inventory.setItem(i, null);
            }
            this.inventory.markDirty(i);
            return true;
        }
        return false;
    }

    public boolean isFuel(InventoryItem item) {
        return item != null && item.item.isGlobalIngredient("anycoolingfuel");
    }

    public void updateInventorySpoilRate() {
        this.inventory.spoilRateModifier = this.remainingFuelTime > 0 ? this.spoilRate : 1.0f;
    }

    public boolean hasFuel() {
        return this.remainingFuelTime > 0;
    }

    public float getFuelProgress() {
        if (this.remainingFuelTime > 0) {
            int totalFuelTime = this.usedFuelTime + this.remainingFuelTime;
            return Math.abs(GameMath.limit((float)this.usedFuelTime / (float)totalFuelTime, 0.0f, 1.0f) - 1.0f);
        }
        return 0.0f;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        Inventory inventory = this.getInventory();
        return new InventoryRange(inventory, this.fuelSlots, inventory.getSize() - 1);
    }

    public InventoryRange getFuelInventoryRange() {
        return new InventoryRange(this.getInventory(), 0, this.fuelSlots - 1);
    }

    @Override
    public InventoryRange getSettlementFuelInventoryRange() {
        return this.getFuelInventoryRange();
    }

    @Override
    public SettlementRequestOptions getSettlementFuelRequestOptions() {
        return new SettlementRequestOptions(5, 10){

            @Override
            public SettlementStorageRecordsRegionData getRequestStorageData(SettlementStorageRecords records) {
                return records.getIndex(SettlementStorageGlobalIngredientIDIndex.class).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anycoolingfuel"));
            }
        };
    }
}

