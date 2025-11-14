/*
 * Decompiled with CFR 0.152.
 */
package necesse.engine;

import java.util.HashMap;
import necesse.inventory.item.ItemCategory;

public class ItemCategoryExpandedSetting {
    public final ItemCategory category;
    private final boolean defaultExpanded;
    private boolean isExpanded;
    private final HashMap<String, ItemCategoryExpandedSetting> children = new HashMap();

    public ItemCategoryExpandedSetting(ItemCategory category, boolean defaultExpanded) {
        this.category = category;
        this.defaultExpanded = defaultExpanded;
        this.isExpanded = defaultExpanded;
    }

    public ItemCategoryExpandedSetting(boolean defaultExpanded) {
        this(ItemCategory.masterCategory, defaultExpanded);
        this.isExpanded = true;
    }

    public ItemCategoryExpandedSetting getChild(ItemCategory childCategory) {
        if (this.category.getChild(childCategory.stringID) == childCategory) {
            return this.children.compute(childCategory.stringID, (key, last) -> {
                if (last == null) {
                    return new ItemCategoryExpandedSetting(childCategory, this.defaultExpanded);
                }
                return last;
            });
        }
        return null;
    }

    public void setExpanded(boolean expanded) {
        this.isExpanded = expanded;
    }

    public boolean isExpanded() {
        return this.isExpanded;
    }

    public void setChildExpanded(ItemCategory childCategory, boolean expanded) {
        ItemCategoryExpandedSetting child = this.getChild(childCategory);
        if (child != null) {
            child.setExpanded(expanded);
        }
    }

    public boolean isChildExpanded(ItemCategory childCategory) {
        ItemCategoryExpandedSetting child = this.getChild(childCategory);
        return child != null && child.isExpanded;
    }
}

