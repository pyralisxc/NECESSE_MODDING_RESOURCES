/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import java.util.ArrayList;
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
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

public abstract class ProcessingInventoryObjectEntity
extends ObjectEntity
implements OEInventory,
IProgressObjectEntity {
    public final Inventory inventory;
    public final int inputSlots;
    protected boolean updateProcessing;
    protected boolean isProcessing;
    protected int currentProcessTime;
    public long lastProcessTime;
    protected boolean useWorldTime = true;
    private boolean processingDirty = false;

    public ProcessingInventoryObjectEntity(Level level, String type, int x, int y, int inputSlots, int outputSlots) {
        super(level, type, x, y);
        this.inputSlots = inputSlots;
        this.inventory = new Inventory(inputSlots + outputSlots){

            @Override
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                ProcessingInventoryObjectEntity.this.onSlotUpdate(slot);
            }
        };
        this.inventory.filter = (slot, item) -> {
            if (item == null) {
                return true;
            }
            if (slot < inputSlots) {
                return this.isValidInputItem(item);
            }
            return false;
        };
        this.updateProcessing = true;
    }

    protected void onSlotUpdate(int slot) {
        if (slot < this.inputSlots) {
            this.updateProcessing = true;
        }
    }

    @Override
    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory));
        save.addLong("lastProcessTime", this.lastProcessTime);
    }

    @Override
    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
        this.lastProcessTime = save.getLong("lastProcessTime", 0L);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
        writer.putNextBoolean(this.isProcessing);
        writer.putNextLong(this.lastProcessTime);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
        this.isProcessing = reader.getNextBoolean();
        this.lastProcessTime = reader.getNextLong();
    }

    @Override
    public void setupProgressPacket(PacketWriter writer) {
        writer.putNextBoolean(this.isProcessing);
        writer.putNextLong(this.lastProcessTime);
    }

    @Override
    public void applyProgressPacket(PacketReader reader) {
        this.isProcessing = reader.getNextBoolean();
        this.lastProcessTime = reader.getNextLong();
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
        long currentTime;
        super.clientTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        long l = currentTime = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
        if (!this.isProcessing) {
            this.lastProcessTime = currentTime;
        }
    }

    @Override
    public void serverTick() {
        long currentTime;
        super.serverTick();
        Performance.record((PerformanceTimerManager)this.getLevel().tickManager(), "tickItems", () -> this.inventory.tickItems(this));
        boolean oldIsProcessing = this.isProcessing;
        long l = currentTime = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
        if (this.lastProcessTime <= 0L) {
            this.lastProcessTime = currentTime;
        }
        if (this.updateProcessing || this.isProcessing) {
            block8: {
                while (true) {
                    if (this.updateProcessing) {
                        this.isProcessing = this.canProcessInput();
                        this.currentProcessTime = this.getProcessTime();
                    }
                    if (!this.isProcessing || this.lastProcessTime + (long)this.currentProcessTime > currentTime) break block8;
                    if (!this.processInput()) break;
                    this.lastProcessTime += (long)this.currentProcessTime;
                    this.markProcessingDirty();
                }
                this.lastProcessTime = currentTime - (long)this.currentProcessTime + (long)Math.min(this.currentProcessTime / 10, 1000);
            }
            this.updateProcessing = false;
        }
        if (!this.isProcessing) {
            this.lastProcessTime = currentTime;
        }
        if (oldIsProcessing != this.isProcessing) {
            this.markProcessingDirty();
        }
        if (this.processingDirty) {
            if (this.isServer()) {
                this.getLevel().getServer().network.sendToClientsWithTile(new PacketOEProgressUpdate(this), this.getLevel(), this.tileX, this.tileY);
            }
            this.processingDirty = false;
        }
        this.serverTickInventorySync(this.getLevel().getServer(), this);
    }

    @Override
    public void markClean() {
        super.markClean();
        this.inventory.clean();
        this.processingDirty = false;
    }

    public abstract boolean isValidInputItem(InventoryItem var1);

    public abstract boolean canProcessInput();

    public abstract int getProcessTime();

    public abstract boolean processInput();

    public abstract ProcessingHelp getProcessingHelp();

    public boolean canAddOutput(InventoryItem ... outputItems) {
        Inventory copy = this.inventory.copy();
        for (InventoryItem outputItem : outputItems) {
            InventoryItem outputItemCopy = outputItem.copy();
            if (copy.addItem(this.getLevel(), null, outputItemCopy, this.inputSlots, copy.getSize() - 1, false, "add", true, false, null)) {
                if (outputItemCopy.getAmount() <= 0) continue;
                return false;
            }
            return false;
        }
        return true;
    }

    public void addOutput(InventoryItem ... outputItems) {
        for (InventoryItem outputItem : outputItems) {
            this.inventory.addItem(this.getLevel(), null, outputItem, this.inputSlots, this.inventory.getSize() - 1, false, "add", true, false, null);
        }
    }

    public void markProcessingDirty() {
        this.processingDirty = true;
    }

    public boolean isProcessing() {
        return this.isProcessing;
    }

    public float getProcessingProgress() {
        if (this.isProcessing) {
            long currentTime = this.useWorldTime ? this.getWorldEntity().getWorldTime() : this.getWorldEntity().getTime();
            int processTime = this.getProcessTime();
            if (processTime <= 0) {
                return 1.0f;
            }
            return GameMath.limit((float)(currentTime - this.lastProcessTime) / (float)processTime, 0.0f, 1.0f);
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

    public InventoryRange getInputInventoryRange() {
        return new InventoryRange(this.inventory, 0, this.inputSlots - 1);
    }

    public InventoryRange getOutputInventoryRange() {
        return new InventoryRange(this.inventory, this.inputSlots, this.inventory.getSize() - 1);
    }
}

