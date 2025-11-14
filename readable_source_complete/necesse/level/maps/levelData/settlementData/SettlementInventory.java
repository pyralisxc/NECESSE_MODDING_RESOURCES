/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.maps.Level;
import necesse.level.maps.levelData.settlementData.SettlementOEInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestInventory;
import necesse.level.maps.levelData.settlementData.SettlementRequestOptions;

public class SettlementInventory
extends SettlementOEInventory {
    public ItemCategoriesFilter filter = new ItemCategoriesFilter(true){

        @Override
        public boolean isItemDisabled(Item item) {
            if (super.isItemDisabled(item)) {
                return true;
            }
            return SettlementInventory.this.oeInventory != null && SettlementInventory.this.oeInventory.isSettlementStorageItemDisabled(item);
        }
    };
    public int priority = 0;
    public SettlementRequestInventory fuelInventory;

    public static GameMessage getPriorityText(int priority) {
        for (Priority p : Priority.values()) {
            if (p.priorityValue != priority) continue;
            return p.displayName;
        }
        return new StaticMessage("" + priority);
    }

    private SettlementInventory(Level level, int tileX, int tileY, boolean setup) {
        super(level, tileX, tileY, setup);
        if (setup && this.oeInventory != null) {
            this.oeInventory.setupDefaultSettlementStorage(this);
        }
    }

    public SettlementInventory(Level level, int tileX, int tileY) {
        this(level, tileX, tileY, true);
    }

    public void addSaveData(SaveData save) {
        save.addPoint("tile", new Point(this.tileX, this.tileY));
        SaveData filtersSave = new SaveData("filter");
        this.filter.addSaveData(filtersSave);
        if (!filtersSave.isEmpty()) {
            save.addSaveData(filtersSave);
        }
        save.addInt("priority", this.priority);
    }

    public static SettlementInventory fromLoadData(Level level, LoadData save, int tileXOffset, int tileYOffset) throws LoadDataException {
        LoadData filterSave;
        Point tile = save.getPoint("tile", null);
        if (tile == null) {
            throw new LoadDataException("Missing position");
        }
        tile.translate(tileXOffset, tileYOffset);
        SettlementInventory out = new SettlementInventory(level, tile.x, tile.y, false);
        LoadData filtersSave = save.getFirstLoadDataByName("filters");
        if (filtersSave != null) {
            out.filter.applyLoadData(filtersSave);
        }
        if ((filterSave = save.getFirstLoadDataByName("filter")) != null) {
            out.filter.applyLoadData(filterSave);
        }
        out.priority = save.getInt("priority", 0, false);
        return out;
    }

    @Override
    public InventoryRange getInventoryRange() {
        this.refreshOEInventory();
        if (this.oeInventory == null) {
            return null;
        }
        return this.oeInventory.getSettlementStorage();
    }

    public SettlementRequestInventory getFuelInventory() {
        this.refreshOEInventory();
        if (this.oeInventory == null) {
            return null;
        }
        SettlementRequestOptions fuelRequestOptions = this.oeInventory.getSettlementFuelRequestOptions();
        if (fuelRequestOptions != null) {
            if (this.fuelInventory == null) {
                this.fuelInventory = new SettlementRequestInventory(this.level, this.tileX, this.tileY, fuelRequestOptions){

                    @Override
                    public InventoryRange getInventoryRange() {
                        this.refreshOEInventory();
                        if (this.oeInventory == null) {
                            return null;
                        }
                        return this.oeInventory.getSettlementFuelInventoryRange();
                    }
                };
            }
            this.fuelInventory.filter = new ItemCategoriesFilter(fuelRequestOptions.minAmount, fuelRequestOptions.maxAmount, true);
            if (!this.fuelInventory.isValid()) {
                this.fuelInventory = null;
            }
            return this.fuelInventory;
        }
        this.fuelInventory = null;
        return null;
    }

    @Override
    public ItemCategoriesFilter getFilter() {
        return this.filter;
    }

    public static enum Priority {
        TOP(300, new LocalMessage("ui", "prioritytop")),
        HIGHER(200, new LocalMessage("ui", "priorityhigher")),
        HIGH(100, new LocalMessage("ui", "priorityhigh")),
        NORMAL(0, new LocalMessage("ui", "prioritynormal")),
        LOW(-100, new LocalMessage("ui", "prioritylow")),
        LOWER(-200, new LocalMessage("ui", "prioritylower")),
        LAST(-300, new LocalMessage("ui", "prioritylast"));

        public final int priorityValue;
        public final GameMessage displayName;

        private Priority(int priorityValue, GameMessage displayName) {
            this.priorityValue = priorityValue;
            this.displayName = displayName;
        }
    }
}

