/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
import java.util.function.Function;
import necesse.engine.gameLoop.tickManager.Performance;
import necesse.engine.gameLoop.tickManager.PerformanceTimerManager;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketOEProgressUpdate;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.engine.util.GameMath;
import necesse.entity.objectEntity.IProgressObjectEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public abstract class FueledInventoryObjectEntity
extends ObjectEntity
implements OEInventory,
IProgressObjectEntity {
    public final Inventory inventory;
    public final boolean alwaysOn;
    protected boolean updateFueled;
    public int fuelBurnTime;
    public long fuelStartTime;
    public boolean keepRunning;
    public boolean useWorldTime = true;
    private boolean fueledDirty = false;

    public FueledInventoryObjectEntity(Level level, String type, int x, int y, int slots, boolean alwaysOn) {
        super(level, type, x, y);
        this.alwaysOn = alwaysOn;
        this.inventory = new Inventory(slots){

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                FueledInventoryObjectEntity.this.updateFueled = true;
            }
        };
        this.inventory.filter = (slot, item) -> {
            if (item == null) {
                return true;
            }
            return this.isValidFuelItem(slot, item);
        };
        this.updateFueled = true;
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory));
        save.addInt("fuelBurnTime", this.fuelBurnTime);
        save.addLong("fuelStartTime", this.fuelStartTime);
        if (!this.alwaysOn) {
            save.addBoolean("keepRunning", this.keepRunning);
        }
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
        this.fuelBurnTime = save.getInt("fuelBurnTime", 0);
        this.fuelStartTime = save.getLong("fuelStartTime", 0L);
        if (!this.alwaysOn) {
            this.keepRunning = save.getBoolean("keepRunning", this.keepRunning);
        }
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
        writer.putNextInt(this.fuelBurnTime);
        writer.putNextLong(this.fuelStartTime);
        if (!this.alwaysOn) {
            writer.putNextBoolean(this.keepRunning);
        }
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
        boolean oldIsFueled = this.isFueled();
        this.fuelBurnTime = reader.getNextInt();
        this.fuelStartTime = reader.getNextLong();
        if (!this.alwaysOn) {
            this.keepRunning = reader.getNextBoolean();
        }
        if (oldIsFueled != this.isFueled()) {
            this.getLevel().lightManager.updateStaticLight(this.tileX, this.tileY);
        }
    }

    @Override
    public void setupProgressPacket(PacketWriter writer) {
        writer.putNextInt(this.fuelBurnTime);
        writer.putNextLong(this.fuelStartTime);
        if (!this.alwaysOn) {
            writer.putNextBoolean(this.keepRunning);
        }
    }

    @Override
    public void applyProgressPacket(PacketReader reader) {
        boolean oldIsFueled = this.isFueled();
        this.fuelBurnTime = reader.getNextInt();
        this.fuelStartTime = reader.getNextLong();
        if (!this.alwaysOn) {
            this.keepRunning = reader.getNextBoolean();
        }
        if (oldIsFueled != this.isFueled()) {
            this.getLevel().lightManager.updateStaticLight(this.tileX, this.tileY);
            if (!this.isFueled()) {
                this.onRanOutOfFuel();
            }
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
        long currentTime;
        super.serverTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        boolean oldIsFueled = this.isFueled();
        long l = currentTime = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
        if (this.updateFueled || this.fuelBurnTime > 0) {
            if (this.fuelStartTime <= 0L) {
                this.fuelStartTime = currentTime;
            }
            if ((this.alwaysOn || this.keepRunning) && this.fuelBurnTime == 0) {
                this.useFuel();
            }
            while (this.fuelBurnTime > 0 && this.fuelStartTime + (long)this.fuelBurnTime <= currentTime) {
                this.fuelStartTime += (long)this.fuelBurnTime;
                if (this.alwaysOn || this.keepRunning) {
                    this.useFuel();
                } else {
                    this.fuelBurnTime = 0;
                }
                this.markFuelDirty();
            }
            this.updateFueled = false;
        }
        if (!this.isFueled()) {
            this.fuelStartTime = currentTime;
        }
        if (oldIsFueled != this.isFueled()) {
            this.markFuelDirty();
        }
        if (this.fueledDirty) {
            if (this.isServer()) {
                this.getLevel().getServer().network.sendToClientsWithEntity(new PacketOEProgressUpdate(this), this);
            }
            this.fueledDirty = false;
            this.getLevel().lightManager.updateStaticLight(this.tileX, this.tileY);
        }
        this.serverTickInventorySync(this.getLevel().getServer(), this);
    }

    public void useFuel() {
        boolean oldIsFueled = this.isFueled();
        this.fuelBurnTime = this.getNextFuelBurnTime(true);
        if (this.fuelBurnTime > 0) {
            this.markFuelDirty();
        }
        if (oldIsFueled != this.isFueled()) {
            this.getLevel().lightManager.updateStaticLight(this.tileX, this.tileY);
        }
    }

    protected void onRanOutOfFuel() {
        if (this.ambientSoundPlayer != null) {
            this.ambientSoundPlayer.stop();
        }
    }

    public boolean canFuel() {
        return this.getNextFuelBurnTime(false) > 0;
    }

    @Override
    public void markClean() {
        super.markClean();
        this.inventory.clean();
        this.fueledDirty = false;
    }

    public abstract boolean isValidFuelItem(int var1, InventoryItem var2);

    public abstract int getNextFuelBurnTime(boolean var1);

    protected int itemToBurnTime(boolean useItem, Function<InventoryItem, Integer> itemToBurnTime) {
        for (int i = this.inventory.getSize() - 1; i >= 0; --i) {
            int fuelBurnTime;
            InventoryItem item = this.inventory.getItem(i);
            if (item == null || (fuelBurnTime = itemToBurnTime.apply(item).intValue()) <= 0) continue;
            if (useItem) {
                this.inventory.addAmount(i, -1);
                if (this.inventory.getAmount(i) <= 0) {
                    this.inventory.setItem(i, null);
                }
                this.inventory.markDirty(i);
            }
            return fuelBurnTime;
        }
        return 0;
    }

    public void markFuelDirty() {
        this.fueledDirty = true;
        this.updateFueled = true;
    }

    public boolean isFueled() {
        return this.fuelBurnTime > 0;
    }

    public float getFuelProgressLeft() {
        if (this.isFueled()) {
            long currentTime = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
            long timeLeft = currentTime - this.fuelStartTime;
            float progress = GameMath.limit((float)timeLeft / (float)this.fuelBurnTime, 0.0f, 1.0f);
            return Math.abs(progress - 1.0f);
        }
        return 0.0f;
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
    public InventoryRange getSettlementStorage() {
        return null;
    }
}

