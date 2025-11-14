/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.miscItem;

import necesse.engine.GameState;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItem;
import necesse.engine.network.gameNetworkData.GNDItemInventory;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.world.GameClock;
import necesse.engine.world.WorldSettings;
import necesse.entity.Entity;
import necesse.entity.TileEntity;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public interface InternalInventoryItemInterface {
    public int getInternalInventorySize();

    default public Inventory getInternalInventory(InventoryItem item) {
        GNDItem gndItem = item.getGndData().getItem("inventory");
        if (gndItem instanceof GNDItemInventory) {
            GNDItemInventory gndInventory = (GNDItemInventory)gndItem;
            if (gndInventory.inventory.getSize() != this.getInternalInventorySize()) {
                gndInventory.inventory.changeSize(this.getInternalInventorySize());
            }
            return gndInventory.inventory;
        }
        Inventory inventory = this.getNewInternalInventory(item);
        item.getGndData().setItem("inventory", (GNDItem)new GNDItemInventory(inventory));
        return inventory;
    }

    default public Inventory getNewInternalInventory(InventoryItem item) {
        return new Inventory(this.getInternalInventorySize());
    }

    default public void tickInternalInventory(InventoryItem item, GameClock clock, GameState state, Entity entity, TileEntity tileEntity, WorldSettings worldSettings) {
        this.getInternalInventory(item).tickItems(clock, state, entity, tileEntity, worldSettings);
    }

    default public void saveInternalInventory(InventoryItem item, Inventory inventory) {
        GNDItem gndItem = item.getGndData().getItem("inventory");
        if (gndItem instanceof GNDItemInventory) {
            GNDItemInventory gndInventory = (GNDItemInventory)gndItem;
            gndInventory.inventory.override(inventory, true, true);
        } else {
            item.getGndData().setItem("inventory", (GNDItem)new GNDItemInventory(inventory));
        }
    }

    default public boolean isValidItem(InventoryItem item) {
        return true;
    }

    public static void setInternalInventory(InventoryItem item, Inventory inventory) {
        if (!(item.item instanceof InternalInventoryItemInterface)) {
            throw new IllegalArgumentException("InventoryItem \"" + item + "\" does not implement InternalInventoryItemInterface");
        }
        ((InternalInventoryItemInterface)((Object)item.item)).saveInternalInventory(item, inventory);
    }

    default public GameTooltips getPickupToggleTooltip(boolean isDisabled) {
        if (isDisabled) {
            return new StringTooltips(Localization.translate("itemtooltip", "autopickupdisabled"));
        }
        return new StringTooltips(Localization.translate("itemtooltip", "autopickupenabled"));
    }

    default public boolean canDisablePickup() {
        return true;
    }

    default public boolean canQuickStackInventory() {
        return true;
    }

    default public boolean canRestockInventory() {
        return true;
    }

    default public boolean canSortInventory() {
        return true;
    }

    default public boolean canChangePouchName() {
        return true;
    }

    default public String getPouchName(InventoryItem item) {
        return item.getGndData().getString("pouchName", null);
    }

    default public void setPouchName(InventoryItem item, String name) {
        if (name.isEmpty() || name.equals(ItemRegistry.getLocalization(item.item.getID()).translate())) {
            item.getGndData().setItem("pouchName", null);
        } else {
            item.getGndData().setString("pouchName", name);
        }
    }

    default public void setPouchPickupDisabled(InventoryItem item, boolean disabled) {
        item.getGndData().setBoolean("pickupDisabled", disabled);
    }

    default public boolean isPickupDisabled(InventoryItem item) {
        return item.getGndData().getBoolean("pickupDisabled");
    }
}

