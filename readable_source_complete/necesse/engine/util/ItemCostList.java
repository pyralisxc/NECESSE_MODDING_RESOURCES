/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine.util;

import java.util.ArrayList;
import java.util.ListIterator;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerTempInventory;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.IngredientUser;
import necesse.level.maps.Level;

public class ItemCostList {
    protected ArrayList<ItemCost> items = new ArrayList();

    public ItemCostList() {
    }

    public ItemCostList(PacketReader reader) {
        int amount = reader.getNextShortUnsigned();
        this.items.ensureCapacity(amount);
        for (int i = 0; i < amount; ++i) {
            InventoryItem item = InventoryItem.fromContentPacket(reader);
            boolean matchGND = reader.getNextBoolean();
            this.items.add(new ItemCost(item, matchGND));
        }
    }

    public void writePacketData(PacketWriter writer) {
        writer.putNextShortUnsigned(this.items.size());
        for (ItemCost cost : this.items) {
            cost.item.addPacketContent(writer);
            writer.putNextBoolean(cost.matchGND);
        }
    }

    public void addSaveData(SaveData save, String componentName) {
        if (componentName != null) {
            SaveData saveData = save;
            save = new SaveData(componentName);
            saveData.addSaveData(save);
        }
        for (ItemCost cost : this.items) {
            SaveData itemSave = new SaveData("item");
            itemSave.addBoolean("matchGND", cost.matchGND);
            cost.item.addSaveData(itemSave);
            save.addSaveData(itemSave);
        }
    }

    public static ItemCostList fromLoadData(LoadData save, String componentName, boolean printWarning) {
        if (componentName != null) {
            LoadData last = save;
            if ((save = save.getFirstLoadDataByName(componentName)) == null) {
                if (printWarning) {
                    System.out.println("Could not find item cost list from \"" + last.getName() + "\" with name \"" + componentName + "\"");
                }
                return null;
            }
        }
        ItemCostList list = new ItemCostList();
        for (LoadData itemSave : save.getLoadDataByName("item")) {
            try {
                boolean matchGND = itemSave.getBoolean("matchGND", false);
                InventoryItem item = InventoryItem.fromLoadData(itemSave);
                if (item != null) {
                    list.addItem(item, matchGND);
                    continue;
                }
                if (!printWarning) continue;
                GameLog.warn.println("Could not load a cost item from " + save.getName());
            }
            catch (Exception e) {
                System.err.println("Error loading cost item from " + save.getName());
                e.printStackTrace();
            }
        }
        return list;
    }

    protected void addItem(InventoryItem item, boolean matchGND) {
        if (matchGND) {
            ListIterator<ItemCost> li = this.items.listIterator();
            while (li.hasNext()) {
                ItemCost next = li.next();
                if (next.matchGND) continue;
                int previousIndex = li.previousIndex();
                if (previousIndex == -1) {
                    this.items.add(0, new ItemCost(item, matchGND));
                } else {
                    this.items.add(previousIndex, new ItemCost(item, matchGND));
                }
                return;
            }
            this.items.add(new ItemCost(item, matchGND));
        } else {
            this.items.add(new ItemCost(item, matchGND));
        }
    }

    public void addSpecificItem(InventoryItem item) {
        this.addItem(item, true);
    }

    public void addItem(String itemStringID, int amount) {
        this.addItem(new InventoryItem(itemStringID, amount), false);
    }

    public Iterable<InventoryItem> getItems() {
        return GameUtils.mapIterable(this.items.iterator(), cost -> cost.item);
    }

    public int getSize() {
        return this.items.size();
    }

    public boolean isEmpty() {
        return this.items.isEmpty();
    }

    public boolean canBuy(PlayerMob player, boolean includeCloud, boolean includeTemp) {
        CanBuy canBuy = new CanBuy();
        if (this.countAmount(player.getLevel(), player, player.getInv().main, canBuy)) {
            return true;
        }
        if (includeCloud && this.countAmount(player.getLevel(), player, player.getInv().cloud, canBuy)) {
            return true;
        }
        if (includeTemp) {
            for (PlayerTempInventory inventory : player.getInv().getTempInventories()) {
                if (!this.countAmount(player.getLevel(), player, inventory, canBuy)) continue;
                return true;
            }
        }
        return false;
    }

    public void buy(PlayerMob player, boolean includeCloud, boolean includeTemp) {
        CanBuy canBuy = new CanBuy();
        if (this.useAmount(player.getLevel(), player, player.getInv().main, canBuy)) {
            return;
        }
        if (includeCloud && this.useAmount(player.getLevel(), player, player.getInv().cloud, canBuy)) {
            return;
        }
        if (includeTemp) {
            for (PlayerTempInventory inventory : player.getInv().getTempInventories()) {
                if (!this.useAmount(player.getLevel(), player, inventory, canBuy)) continue;
                return;
            }
        }
    }

    private boolean countAmount(final Level level, PlayerMob player, Inventory inventory, final CanBuy canBuy) {
        inventory.countIngredientAmount(level, player, "buy", new IngredientCounter(){

            @Override
            public void handle(Inventory inventory, int slot, InventoryItem item) {
                if (inventory != null && !inventory.canBeUsedForCrafting()) {
                    return;
                }
                int availableItems = item.getAmount();
                for (int i = 0; i < ItemCostList.this.items.size() && availableItems > 0; ++i) {
                    ItemCost cost = ItemCostList.this.items.get(i);
                    int alreadyFound = canBuy.getFoundAmount(i);
                    int missingItems = cost.item.getAmount() - alreadyFound;
                    if (missingItems <= 0 || !cost.matchesItem(level, item)) continue;
                    int remainingAvailable = Math.min(missingItems, availableItems);
                    canBuy.addFoundItems(i, remainingAvailable);
                    availableItems -= remainingAvailable;
                    if (!canBuy.canBuy()) continue;
                    return;
                }
            }
        });
        return canBuy.canBuy();
    }

    private boolean useAmount(final Level level, PlayerMob player, Inventory inventory, final CanBuy canBuy) {
        inventory.useIngredientAmount(level, player, "buy", new IngredientUser(){

            @Override
            public void handle(Inventory inventory, int slot, InventoryItem item, Runnable markDirty) {
                int availableItems;
                if (inventory != null && !inventory.canBeUsedForCrafting()) {
                    return;
                }
                for (int i = 0; i < ItemCostList.this.items.size() && (availableItems = item.getAmount()) > 0; ++i) {
                    ItemCost cost = ItemCostList.this.items.get(i);
                    int alreadyUsed = canBuy.getFoundAmount(i);
                    int missingItems = cost.item.getAmount() - alreadyUsed;
                    if (missingItems <= 0 || !cost.matchesItem(level, item)) continue;
                    int remainingAvailable = Math.min(missingItems, availableItems);
                    item.setAmount(item.getAmount() - remainingAvailable);
                    canBuy.addFoundItems(i, remainingAvailable);
                    if (item.getAmount() <= 0 && inventory != null) {
                        inventory.setItem(slot, null);
                        break;
                    }
                    markDirty.run();
                    if (!canBuy.canBuy()) continue;
                    return;
                }
            }
        });
        return canBuy.canBuy();
    }

    private static class ItemCost {
        public InventoryItem item;
        public boolean matchGND;

        public ItemCost(InventoryItem item, boolean matchGND) {
            this.item = item;
            this.matchGND = matchGND;
        }

        public boolean matchesItem(Level level, InventoryItem item) {
            return item.equals(level, this.item, true, !this.matchGND, "buy");
        }
    }

    public class CanBuy {
        protected int[] foundItemAmounts;
        protected int canBuy;

        public CanBuy() {
            this.foundItemAmounts = new int[ItemCostList.this.items.size()];
            this.canBuy = 0;
        }

        public void addFoundItems(int index, int amount) {
            ItemCost cost = ItemCostList.this.items.get(index);
            int costAmount = cost.item.getAmount();
            if (costAmount == 0) {
                if (this.foundItemAmounts[index] == 0) {
                    this.foundItemAmounts[index] = -1;
                    ++this.canBuy;
                }
            } else if (amount > 0) {
                boolean haveEnoughAfter;
                boolean haveEnoughBefore = this.foundItemAmounts[index] >= costAmount;
                int n = index;
                this.foundItemAmounts[n] = this.foundItemAmounts[n] + amount;
                boolean bl = haveEnoughAfter = this.foundItemAmounts[index] >= costAmount;
                if (!haveEnoughBefore && haveEnoughAfter) {
                    ++this.canBuy;
                }
            }
        }

        public int getFoundAmount(int index) {
            return this.foundItemAmounts[index];
        }

        public boolean canBuy() {
            return this.canBuy >= this.foundItemAmounts.length;
        }
    }
}

