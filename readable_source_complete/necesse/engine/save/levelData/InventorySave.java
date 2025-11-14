/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.save.levelData;

import java.util.List;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;

public class InventorySave {
    public static Inventory loadSave(LoadData save) {
        Inventory inv = new Inventory(Integer.parseInt(save.getFirstDataByName("size"))){

            @Override
            public boolean canLockItem(int slot) {
                return true;
            }
        };
        List<LoadData> items = save.getLoadDataByName("ITEM");
        for (LoadData itemSave : items) {
            try {
                boolean locked = itemSave.getFirstLoadDataByName("locked") != null;
                int slot = itemSave.getInt("slot", -1, false);
                if (slot == -1) {
                    throw new NullPointerException();
                }
                InventoryItem item = InventoryItem.fromLoadData(itemSave);
                if (item == null) {
                    throw new NullPointerException();
                }
                inv.setItem(slot, item);
                inv.setItemLocked(slot, locked);
            }
            catch (Exception ex) {
                String stringID = itemSave.getUnsafeString("stringID", "N/A");
                System.err.println("Could not load inventory item: " + stringID);
            }
        }
        return inv;
    }

    public static SaveData getSave(Inventory inv, String componentName) {
        SaveData save = new SaveData(componentName);
        save.addInt("size", inv.getSize());
        for (int i = 0; i < inv.getSize(); ++i) {
            if (inv.isSlotClear(i) || inv.getItemSlot(i) == null) continue;
            SaveData itemSave = new SaveData("ITEM");
            itemSave.addInt("slot", i);
            if (inv.isItemLocked(i)) {
                itemSave.addBoolean("locked", true);
            }
            inv.getItem(i).addSaveData(itemSave);
            save.addSaveData(itemSave);
        }
        return save;
    }

    public static SaveData getSave(Inventory inv) {
        return InventorySave.getSave(inv, "INVENTORY");
    }
}

