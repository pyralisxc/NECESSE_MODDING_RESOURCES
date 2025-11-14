/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.GameRegistry;
import necesse.engine.util.GameUtils;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;

public class ItemCategoryManager {
    private final HashMap<Integer, ItemCategory> itemIDCategory = new HashMap();
    private final ArrayList<ItemCategory> categories = new ArrayList();
    public final ItemCategory masterCategory;

    public ItemCategoryManager(ItemCategory masterCategory) {
        this.masterCategory = masterCategory;
        this.masterCategory.manager = this;
        this.categories.add(masterCategory);
    }

    public ItemCategory createCategory(String categorySortString, String ... categoryTree) {
        return this.createCategory(categorySortString, new LocalMessage("itemcategory", categoryTree[categoryTree.length - 1]), categoryTree);
    }

    public ItemCategory createCategory(String categorySortString, GameMessage displayName, String ... categoryTree) {
        if (categoryTree.length == 0) {
            throw new IllegalArgumentException("Must have at least one category");
        }
        ItemCategory current = this.masterCategory;
        for (int i = 0; i < categoryTree.length; ++i) {
            String nextStringID = categoryTree[i];
            ItemCategory next = current.children.get(nextStringID);
            if (i == categoryTree.length - 1) {
                if (next != null) {
                    GameLog.debug.println("Tried to create duplicate item category: " + GameUtils.join(categoryTree, "."));
                    return next;
                }
                if (!GameRegistry.validStringID(nextStringID)) {
                    throw new IllegalArgumentException("Item category with stringID \"" + nextStringID + "\" is not a valid stringID");
                }
                ItemCategory newCategory = new ItemCategory(this.categories.size(), nextStringID, current, categorySortString, displayName);
                newCategory.manager = this;
                this.categories.add(newCategory);
                current.children.put(nextStringID, newCategory);
                return newCategory;
            }
            if (next == null) {
                throw new IllegalStateException("Must first create " + GameUtils.join(Arrays.copyOfRange(categoryTree, 0, i + 1), ".") + " item category " + GameUtils.join(categoryTree, "."));
            }
            current = next;
        }
        return current;
    }

    public ItemCategory getCategory(String ... categoryTree) {
        ItemCategory current = this.masterCategory;
        for (String stringID : categoryTree) {
            current = current.children.get(stringID);
            if (current != null) continue;
            throw new IllegalStateException("Must first create item category " + GameUtils.join(categoryTree, "."));
        }
        return current;
    }

    public ItemCategory getCategory(int categoryID) {
        if (categoryID < 0 || categoryID >= this.categories.size()) {
            return null;
        }
        return this.categories.get(categoryID);
    }

    public ItemCategory setItemCategory(Item item, String ... categoryTree) {
        return this.setItemCategory(item, this.getCategory(categoryTree));
    }

    public ItemCategory setItemCategory(Item item, ItemCategory category) {
        this.itemIDCategory.compute(item.getID(), (itemID, last) -> {
            if (last != null) {
                last.itemIDs.remove(itemID);
                while (last != null) {
                    last.thisOrChildrenItemIDs.remove(itemID);
                    last = last.parent;
                }
            }
            category.itemIDs.put((Integer)itemID, item);
            ItemCategory current = category;
            while (current != null) {
                current.thisOrChildrenItemIDs.put((Integer)itemID, item);
                current = current.parent;
            }
            return category;
        });
        this.itemIDCategory.put(item.getID(), category);
        return category;
    }

    public ItemCategory getItemsCategory(Item item) {
        return this.itemIDCategory.get(item.getID());
    }
}

