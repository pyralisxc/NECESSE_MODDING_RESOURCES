/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.objectEntity;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.GlobalIngredientRegistry;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.objectEntity.FueledProcessingInventoryObjectEntity;
import necesse.entity.objectEntity.interfaces.OEUsers;
import necesse.gfx.GameResources;
import necesse.gfx.forms.presets.containerComponent.object.ProcessingHelp;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecordsRegionData;

public class FueledIncineratorObjectEntity
extends FueledProcessingInventoryObjectEntity
implements OEUsers {
    public static int FUEL_TIME_ADDED = 240000;
    public final OEUsers.Users users = this.constructUsersObject(2000L);

    public FueledIncineratorObjectEntity(Level level, int x, int y, int fuelSlots, int inventorySlots) {
        super(level, "incinerator", x, y, fuelSlots, inventorySlots, 0, false, false, true);
    }

    @Override
    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.users.writeUsersSpawnPacket(writer);
    }

    @Override
    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.users.readUsersSpawnPacket(reader, this);
    }

    @Override
    public void clientTick() {
        super.clientTick();
        this.users.clientTick(this);
    }

    @Override
    public void serverTick() {
        super.serverTick();
        this.users.serverTick(this);
    }

    @Override
    public boolean isValidFuelItem(InventoryItem item) {
        return item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"));
    }

    @Override
    public int getNextFuelBurnTime(boolean useFuel) {
        return this.itemToBurnTime(useFuel, item -> {
            if (item.item.isGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredient("anylog"))) {
                return FUEL_TIME_ADDED;
            }
            return 0;
        });
    }

    @Override
    public boolean isValidInputItem(InventoryItem item) {
        return true;
    }

    @Override
    public FueledProcessingInventoryObjectEntity.NextProcessTask getNextProcessTask() {
        InventoryRange inputRange = this.getInputInventoryRange();
        for (int i = inputRange.startSlot; i <= inputRange.endSlot; ++i) {
            InventoryItem item = inputRange.inventory.getItem(i);
            if (item == null) continue;
            int recipeHash = item.item.getID() * GameRandom.prime(341);
            return new FueledProcessingInventoryObjectEntity.NextProcessTask(recipeHash == 0 ? 2260395 : recipeHash, item.item.getIncinerationRate());
        }
        return null;
    }

    @Override
    public boolean processInput() {
        InventoryRange inputRange = this.getInputInventoryRange();
        for (int i = inputRange.startSlot; i <= inputRange.endSlot; ++i) {
            InventoryItem item = inputRange.inventory.getItem(i);
            if (item == null) continue;
            item.setAmount(item.getAmount() - 1);
            if (item.getAmount() <= 0) {
                inputRange.inventory.setItem(i, null);
            }
            this.compressInventory();
            inputRange.inventory.markDirty(i);
            return true;
        }
        return false;
    }

    protected void compressInventory() {
        InventoryRange range = this.getInputInventoryRange();
        int firstEmptySlot = -1;
        for (int i = range.startSlot; i <= range.endSlot; ++i) {
            InventoryItem item = range.inventory.getItem(i);
            if (item == null) {
                if (firstEmptySlot != -1) continue;
                firstEmptySlot = i;
                continue;
            }
            if (firstEmptySlot == -1) continue;
            range.inventory.markDirty(firstEmptySlot);
            range.inventory.setItem(firstEmptySlot, item);
            range.inventory.setItem(i, null);
            range.inventory.markDirty(i);
            ++firstEmptySlot;
        }
    }

    @Override
    public ProcessingHelp getProcessingHelp() {
        return null;
    }

    @Override
    public InventoryRange getSettlementStorage() {
        return this.getInputInventoryRange();
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
                return records.getIndex(SettlementStorageGlobalIngredientIDIndex.class).getGlobalIngredient(GlobalIngredientRegistry.getGlobalIngredientID("anylog"));
            }
        };
    }

    @Override
    public void setupDefaultSettlementStorage(SettlementInventory inventory) {
        super.setupDefaultSettlementStorage(inventory);
        inventory.filter.master.setAllowed(false);
        inventory.priority = SettlementInventory.Priority.LAST.priorityValue;
    }

    @Override
    public OEUsers.Users getUsersObject() {
        return this.users;
    }

    @Override
    public GameMessage getCanUseError(Mob mob) {
        return null;
    }

    @Override
    public void onIsInUseChanged(boolean isInUse) {
    }

    @Override
    public void remove() {
        super.remove();
        this.users.onRemoved(this);
    }

    @Override
    public boolean shouldPlayAmbientSound() {
        return this.isFuelRunning();
    }

    @Override
    protected SoundSettings getAmbientSound() {
        return new SoundSettings(GameResources.campfireAmbient).volume(0.3f);
    }
}

