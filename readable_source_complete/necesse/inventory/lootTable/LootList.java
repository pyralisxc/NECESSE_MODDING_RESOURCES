/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.lootTable;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameUtils;
import necesse.gfx.fairType.FairItemGlyph;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;

public class LootList {
    protected LinkedHashSet<Integer> itemIDs = new LinkedHashSet();
    protected ArrayList<InventoryItem> customItems = new ArrayList();
    protected ArrayList<CustomRewardStringAdder> customRewardStrings = new ArrayList();

    public LootList add(int itemID) {
        if (itemID != -1) {
            this.itemIDs.add(itemID);
        }
        return this;
    }

    public LootList add(Item item) {
        if (item == null) {
            return this;
        }
        return this.add(item.getID());
    }

    public LootList add(String itemStringID) {
        return this.add(ItemRegistry.getItemID(itemStringID));
    }

    public LootList addCustom(InventoryItem inventoryItem) {
        if (inventoryItem == null) {
            return this;
        }
        inventoryItem.combineOrAddToList(null, null, this.customItems, "add");
        return this;
    }

    public LootList addCustomRewardString(CustomRewardStringAdder adder) {
        this.customRewardStrings.add(adder);
        return this;
    }

    public Iterable<Integer> getItemIDs() {
        return this.itemIDs;
    }

    public Iterable<Item> getItems() {
        return GameUtils.mapIterable(this.itemIDs.iterator(), ItemRegistry::getItem);
    }

    public IntStream streamItemIDs() {
        return this.itemIDs.stream().mapToInt(id -> id);
    }

    public Stream<Item> streamItems() {
        return this.itemIDs.stream().map(ItemRegistry::getItem);
    }

    public Iterable<InventoryItem> getCustomItems() {
        return GameUtils.mapIterable(this.customItems.iterator(), InventoryItem::copy);
    }

    public Stream<InventoryItem> streamCustomItems() {
        return this.customItems.stream().map(InventoryItem::copy);
    }

    public Stream<InventoryItem> streamItemsAndCustomItems() {
        return Stream.concat(this.streamItems().map(i -> i.getDefaultItem(null, 1)), this.streamCustomItems());
    }

    public ArrayList<InventoryItem> getCombinedItemsAndCustomItems() {
        ArrayList<InventoryItem> out = new ArrayList<InventoryItem>();
        for (Item item : this.getItems()) {
            item.getDefaultItem(null, 1).combineOrAddToList(null, null, out, "add");
        }
        for (InventoryItem customItem : this.getCustomItems()) {
            customItem.combineOrAddToList(null, null, out, "add");
        }
        return out;
    }

    public String getRewardsItemString(boolean addDisplayNames) {
        StringBuilder builder = new StringBuilder();
        boolean isFirstReward = true;
        for (InventoryItem item : this.getCombinedItemsAndCustomItems()) {
            if (!isFirstReward) {
                builder.append(", ");
            }
            builder.append(TypeParsers.getItemParseString(item));
            if (addDisplayNames) {
                builder.append(" ").append(item.getItemDisplayName());
            }
            isFirstReward = false;
        }
        for (CustomRewardStringAdder adder : this.customRewardStrings) {
            String itemString = adder.getCustomRewardString(addDisplayNames);
            if (itemString == null) continue;
            if (!isFirstReward) {
                builder.append(", ");
            }
            builder.append(itemString);
            isFirstReward = false;
        }
        if (builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }

    public boolean addRewardsToFairType(FairType fairType, FontOptions fontOptions, boolean addDisplayNames, boolean applyItemIconParsers, Function<String, String> fullRewardTextGetter) {
        String itemString = this.getRewardsItemString(addDisplayNames);
        if (itemString == null) {
            return false;
        }
        fairType.append(fontOptions, fullRewardTextGetter == null ? itemString : fullRewardTextGetter.apply(itemString));
        if (applyItemIconParsers) {
            fairType.applyParsers(TypeParsers.ItemIcon(fontOptions.getSize(), true, FairItemGlyph::onlyShowNameTooltip));
        }
        return true;
    }

    public FairType getRewardType(FontOptions fontOptions, boolean addDisplayNames, Function<String, String> fullRewardTextGetter) {
        FairType fairType = new FairType();
        if (this.addRewardsToFairType(fairType, fontOptions, addDisplayNames, true, fullRewardTextGetter)) {
            return fairType;
        }
        return null;
    }

    public FairType getRewardType(FontOptions fontOptions, boolean addDisplayNames) {
        return this.getRewardType(fontOptions, addDisplayNames, itemString -> Localization.translate("quests", "reward", "reward", itemString));
    }

    @FunctionalInterface
    public static interface CustomRewardStringAdder {
        public String getCustomRewardString(boolean var1);
    }
}

