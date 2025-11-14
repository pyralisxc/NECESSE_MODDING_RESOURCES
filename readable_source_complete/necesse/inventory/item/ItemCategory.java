/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.CategoryString;
import necesse.engine.util.GameUtils;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategoryManager;

public class ItemCategory
implements Comparable<ItemCategory> {
    public static final ItemCategory masterCategory = new ItemCategory();
    public static final ItemCategoryManager masterManager = new ItemCategoryManager(masterCategory);
    public static final ItemCategory foodQualityMasterCategory = new ItemCategory();
    public static final ItemCategoryManager foodQualityManager = new ItemCategoryManager(foodQualityMasterCategory);
    public static final ItemCategory equipmentMasterCategory = new ItemCategory();
    public static final ItemCategoryManager equipmentManager = new ItemCategoryManager(equipmentMasterCategory);
    public static final ItemCategory craftingMasterCategory = new ItemCategory();
    public static final ItemCategoryManager craftingManager = new ItemCategoryManager(craftingMasterCategory);
    protected ItemCategoryManager manager;
    public final int id;
    public final int depth;
    public final String stringID;
    public final ItemCategory parent;
    public GameMessage displayName;
    protected String[] sortCategories;
    protected HashMap<String, ItemCategory> children = new HashMap();
    protected HashMap<Integer, Item> itemIDs = new HashMap();
    protected HashMap<Integer, Item> thisOrChildrenItemIDs = new HashMap();

    public static ItemCategory createCategory(String categorySortString, String ... categoryTree) {
        return masterManager.createCategory(categorySortString, categoryTree);
    }

    public static ItemCategory createCategory(String categorySortString, GameMessage displayName, String ... categoryTree) {
        return masterManager.createCategory(categorySortString, displayName, categoryTree);
    }

    public static ItemCategory getCategory(String ... categoryTree) {
        return masterManager.getCategory(categoryTree);
    }

    public static ItemCategory getCategory(int categoryID) {
        return masterManager.getCategory(categoryID);
    }

    public ItemCategory setItemCategory(Item item, ItemCategory category) {
        return masterManager.setItemCategory(item, category);
    }

    public static ItemCategory setItemCategory(Item item, String ... categoryTree) {
        return masterManager.setItemCategory(item, categoryTree);
    }

    public static ItemCategory getItemsCategory(Item item) {
        return masterManager.getItemsCategory(item);
    }

    protected ItemCategory(int id, String stringID, ItemCategory parent, String categorySortString, GameMessage displayName) {
        this.id = id;
        this.stringID = stringID;
        this.parent = parent;
        this.depth = parent == null ? 0 : parent.depth + 1;
        this.displayName = displayName;
        this.setSortCategories(categorySortString);
    }

    public ItemCategory() {
        this(0, "master", null, "Z-Z-Z", new StaticMessage("MASTER"));
    }

    public void setSortCategories(String categorySortString) {
        this.sortCategories = CategoryString.getCategories(categorySortString);
    }

    public Iterable<String> getChildrenStringIDs() {
        return this.children.keySet();
    }

    public Iterable<ItemCategory> getChildren() {
        return this.children.values();
    }

    public Stream<ItemCategory> streamChildren() {
        return this.children.values().stream();
    }

    public ItemCategory getChild(String stringID) {
        return this.children.get(stringID);
    }

    public Collection<Item> getItems() {
        return this.itemIDs.values();
    }

    public Stream<Item> streamItems() {
        return this.itemIDs.values().stream();
    }

    public boolean containsItem(Item item) {
        return this.itemIDs.containsKey(item.getID());
    }

    public boolean containsItemOrInChildren(Item item) {
        return this.thisOrChildrenItemIDs.containsKey(item.getID());
    }

    public int getItemCountIncludingChildren() {
        return this.thisOrChildrenItemIDs.size();
    }

    public String[] getStringIDTree(boolean includeMaster) {
        LinkedList<String> tree = new LinkedList<String>();
        tree.addFirst(this.stringID);
        ItemCategory parent = this.parent;
        while (parent != null && (parent.parent != null || includeMaster)) {
            tree.addFirst(parent.stringID);
            parent = parent.parent;
        }
        return tree.toArray(new String[0]);
    }

    public void printTree(PrintStream printStream, String prefix) {
        printStream.println(prefix + this.displayName.translate() + " (" + GameUtils.join(this.sortCategories, "-") + ")");
        this.children.values().stream().sorted().forEach(category -> category.printTree(printStream, prefix + "\t"));
        this.itemIDs.values().stream().filter(item -> ItemRegistry.isObtainable(item.getID())).map(i -> ItemRegistry.getDisplayName(i.getID())).filter(Objects::nonNull).sorted().forEach(name -> printStream.println(prefix + "\t" + name));
    }

    public boolean isOrHasParent(String categoryStringID) {
        if (this.stringID.equals(categoryStringID)) {
            return true;
        }
        if (this.parent != null) {
            return this.parent.isOrHasParent(categoryStringID);
        }
        return false;
    }

    private static int compare(ItemCategory me, ItemCategory him) {
        return new CategoryString(me.sortCategories, me.displayName.translate()).compareTo(new CategoryString(him.sortCategories, him.displayName.translate()));
    }

    @Override
    public int compareTo(ItemCategory him) {
        if (him == null) {
            return -1;
        }
        if (him == this) {
            return 0;
        }
        if (this.manager != null && this.manager == him.manager) {
            if (this.parent == him.parent) {
                return ItemCategory.compare(this, him);
            }
            ItemCategory me = this;
            if (me == him.parent) {
                return -1;
            }
            if (me.parent == him) {
                return 1;
            }
            while (him.parent != null && him.parent.depth > this.depth) {
                him = him.parent;
            }
            if (him.parent == null) {
                return 1;
            }
            while (me.parent != null && me.parent.depth > him.depth) {
                me = me.parent;
            }
            if (me.parent == null) {
                return -1;
            }
            return ItemCategory.compare(me, him);
        }
        return ItemCategory.compare(this, him);
    }

    static {
        ItemCategory.createCategory("A-A-A", "equipment");
        ItemCategory.createCategory("A-A-A", "equipment", "tools");
        ItemCategory.createCategory("A-A-A", "equipment", "tools", "creative");
        ItemCategory.createCategory("A-A-B", "equipment", "tools", "pickaxes");
        ItemCategory.createCategory("A-A-C", "equipment", "tools", "axes");
        ItemCategory.createCategory("A-A-D", "equipment", "tools", "shovels");
        ItemCategory.createCategory("A-A-E", "equipment", "tools", "fishingrods");
        ItemCategory.createCategory("A-A-F", "equipment", "tools", "misc");
        ItemCategory.createCategory("A-B-A", "equipment", "weapons");
        ItemCategory.createCategory("A-B-A", "equipment", "weapons", "meleeweapons");
        ItemCategory.createCategory("A-B-B", "equipment", "weapons", "rangedweapons");
        ItemCategory.createCategory("A-B-C", "equipment", "weapons", "magicweapons");
        ItemCategory.createCategory("A-B-D", "equipment", "weapons", "summonweapons");
        ItemCategory.createCategory("A-B-E", "equipment", "weapons", "throwweapons");
        ItemCategory.createCategory("A-C-A", "equipment", "ammo");
        ItemCategory.createCategory("A-C-B", "equipment", "bait");
        ItemCategory.createCategory("A-D-A", "equipment", "armor");
        ItemCategory.createCategory("A-E-A", "equipment", "trinkets");
        ItemCategory.createCategory("A-F-A", "equipment", "cosmetics");
        ItemCategory.createCategory("A-G-A", "equipment", "banners");
        ItemCategory.createCategory("B-A-A", "consumable");
        ItemCategory.createCategory("B-A-A", "consumable", "rawfood");
        ItemCategory.createCategory("B-B-A", "consumable", "commonfish");
        ItemCategory.createCategory("B-C-A", "consumable", "food");
        ItemCategory.createCategory("B-D-A", "consumable", "potions");
        ItemCategory.createCategory("B-E-A", "consumable", "bossitems");
        ItemCategory.createCategory("C-A-A", "materials");
        ItemCategory.createCategory("C-A-A", "materials", "ore");
        ItemCategory.createCategory("C-B-A", "materials", "minerals");
        ItemCategory.createCategory("C-C-A", "materials", "bars");
        ItemCategory.createCategory("C-D-A", "materials", "stone");
        ItemCategory.createCategory("C-E-A", "materials", "logs");
        ItemCategory.createCategory("C-F-A", "materials", "specialfish");
        ItemCategory.createCategory("C-G-A", "materials", "flowers");
        ItemCategory.createCategory("C-H-A", "materials", "mobdrops");
        ItemCategory.createCategory("C-I-A", "materials", "essences");
        ItemCategory.createCategory("D-A-A", "tiles");
        ItemCategory.createCategory("D-A-A", "tiles", "floors");
        ItemCategory.createCategory("D-B-A", "tiles", "liquids");
        ItemCategory.createCategory("D-C-A", "tiles", "terrain");
        ItemCategory.createCategory("E-A-A", "objects");
        ItemCategory.createCategory("E-A-A", "objects", "seeds");
        ItemCategory.createCategory("E-B-A", "objects", "saplings");
        ItemCategory.createCategory("E-C-A", "objects", "craftingstations");
        ItemCategory.createCategory("E-D-A", "objects", "lighting");
        ItemCategory.createCategory("E-E-A", "objects", "furniture");
        ItemCategory.createCategory("E-E-B", "objects", "furniture", "misc");
        ItemCategory.createCategory("E-E-C", "objects", "furniture", "oak");
        ItemCategory.createCategory("E-E-D", "objects", "furniture", "spruce");
        ItemCategory.createCategory("E-E-E", "objects", "furniture", "pine");
        ItemCategory.createCategory("E-E-F", "objects", "furniture", "palm");
        ItemCategory.createCategory("E-E-G", "objects", "furniture", "maple");
        ItemCategory.createCategory("E-E-H", "objects", "furniture", "birch");
        ItemCategory.createCategory("E-E-I", "objects", "furniture", "willow");
        ItemCategory.createCategory("E-E-J", "objects", "furniture", "dungeon");
        ItemCategory.createCategory("E-E-K", "objects", "furniture", "bone");
        ItemCategory.createCategory("E-E-L", "objects", "furniture", "dryad");
        ItemCategory.createCategory("E-E-M", "objects", "furniture", "bamboo");
        ItemCategory.createCategory("E-E-N", "objects", "furniture", "deadwood");
        ItemCategory.createCategory("E-E-O", "objects", "furniture", "spidercastle");
        ItemCategory.createCategory("E-F-A", "objects", "decorations");
        ItemCategory.createCategory("E-F-B", "objects", "decorations", "paintings");
        ItemCategory.createCategory("E-F-C", "objects", "decorations", "carpets");
        ItemCategory.createCategory("E-F-D", "objects", "decorations", "pots");
        ItemCategory.createCategory("E-F-E", "objects", "decorations", "banners");
        ItemCategory.createCategory("E-G-A", "objects", "wallsanddoors");
        ItemCategory.createCategory("E-H-A", "objects", "fencesandgates");
        ItemCategory.createCategory("E-I-A", "objects", "columns");
        ItemCategory.createCategory("E-J-A", "objects", "traps");
        ItemCategory.createCategory("E-K-A", "objects", "landscaping");
        ItemCategory.createCategory("E-K-A", "objects", "landscaping", "rocksandores");
        ItemCategory.createCategory("E-K-B", "objects", "landscaping", "forestrocksandores");
        ItemCategory.createCategory("E-K-C", "objects", "landscaping", "snowrocksandores");
        ItemCategory.createCategory("E-K-D", "objects", "landscaping", "plainsrocksandores");
        ItemCategory.createCategory("E-K-E", "objects", "landscaping", "swamprocksandores");
        ItemCategory.createCategory("E-K-F", "objects", "landscaping", "desertrocksandores");
        ItemCategory.createCategory("E-K-G", "objects", "landscaping", "incursionrocksandores");
        ItemCategory.createCategory("E-K-H", "objects", "landscaping", "crystals");
        ItemCategory.createCategory("E-K-I", "objects", "landscaping", "tabledecorations");
        ItemCategory.createCategory("E-K-J", "objects", "landscaping", "plants");
        ItemCategory.createCategory("E-K-K", "objects", "landscaping", "masonry");
        ItemCategory.createCategory("E-K-L", "objects", "landscaping", "misc");
        ItemCategory.createCategory("E-L-A", "objects", "misc");
        ItemCategory.createCategory("F-A-A", "wiring");
        ItemCategory.createCategory("F-A-A", "wiring", "logicgates");
        ItemCategory.createCategory("G-A-A", "mobs");
        ItemCategory.createCategory("G-B-A", "mobs", "passive");
        ItemCategory.createCategory("G-B-B", "mobs", "passive", "critters");
        ItemCategory.createCategory("G-B-C", "mobs", "passive", "humans");
        ItemCategory.createCategory("G-B-D", "mobs", "passive", "husbandry");
        ItemCategory.createCategory("G-C-A", "mobs", "hostile");
        ItemCategory.createCategory("G-C-B", "mobs", "hostile", "raiders");
        ItemCategory.createCategory("G-C-C", "mobs", "hostile", "bosses");
        ItemCategory.createCategory("Z-A-A", "misc");
        ItemCategory.createCategory("Z-A-A", "misc", "mounts");
        ItemCategory.createCategory("Z-B-A", "misc", "pets");
        ItemCategory.createCategory("Z-C-A", "misc", "pouches");
        ItemCategory.createCategory("Z-D-A", "misc", "vinyls");
        ItemCategory.createCategory("Z-E-A", "misc", "questitems");
        equipmentManager.createCategory("A-A-A", "weapons");
        equipmentManager.createCategory("A-B-A", "weapons", "meleeweapons");
        equipmentManager.createCategory("A-C-A", "weapons", "rangedweapons");
        equipmentManager.createCategory("A-D-A", "weapons", "magicweapons");
        equipmentManager.createCategory("A-E-A", "weapons", "summonweapons");
        equipmentManager.createCategory("A-F-A", "weapons", "throwweapons");
        equipmentManager.createCategory("B-A-A", "armor");
        craftingManager.createCategory("A-A-A", "craftingstations");
        craftingManager.createCategory("B-A-A", "incursions");
        craftingManager.createCategory("C-A-A", "consumable");
        craftingManager.createCategory("C-A-A", "consumable", "resourcepotions");
        craftingManager.createCategory("C-A-B", "consumable", "greaterbuffpotions");
        craftingManager.createCategory("C-A-C", "consumable", "buffpotions");
        craftingManager.createCategory("C-A-D", "consumable", "utilitypotions");
        craftingManager.createCategory("D-A-A", "equipment");
        craftingManager.createCategory("D-A-A", "equipment", "tools");
        craftingManager.createCategory("D-B-A", "equipment", "weapons");
        craftingManager.createCategory("D-B-A", "equipment", "weapons", "meleeweapons");
        craftingManager.createCategory("D-B-B", "equipment", "weapons", "rangedweapons");
        craftingManager.createCategory("D-B-C", "equipment", "weapons", "magicweapons");
        craftingManager.createCategory("D-B-D", "equipment", "weapons", "summonweapons");
        craftingManager.createCategory("D-B-E", "equipment", "weapons", "throwweapons");
        craftingManager.createCategory("D-C-A", "equipment", "ammo");
        craftingManager.createCategory("D-D-A", "equipment", "armor");
        craftingManager.createCategory("D-E-A", "equipment", "trinkets");
        craftingManager.createCategory("D-F-A", "equipment", "cosmetics");
        craftingManager.createCategory("E-A-A", "objects");
        craftingManager.createCategory("E-A-A", "objects", "lighting");
        craftingManager.createCategory("E-B-A", "objects", "furniture");
        craftingManager.createCategory("E-E-B", "objects", "furniture", "misc");
        craftingManager.createCategory("E-B-C", "objects", "furniture", "oak");
        craftingManager.createCategory("E-B-D", "objects", "furniture", "spruce");
        craftingManager.createCategory("E-B-E", "objects", "furniture", "pine");
        craftingManager.createCategory("E-B-F", "objects", "furniture", "palm");
        craftingManager.createCategory("E-B-G", "objects", "furniture", "maple");
        craftingManager.createCategory("E-B-H", "objects", "furniture", "birch");
        craftingManager.createCategory("E-B-I", "objects", "furniture", "willow");
        craftingManager.createCategory("E-B-J", "objects", "furniture", "dungeon");
        craftingManager.createCategory("E-B-K", "objects", "furniture", "bone");
        craftingManager.createCategory("E-B-L", "objects", "furniture", "dryad");
        craftingManager.createCategory("E-B-M", "objects", "furniture", "bamboo");
        craftingManager.createCategory("E-B-N", "objects", "furniture", "deadwood");
        craftingManager.createCategory("E-B-O", "objects", "furniture", "spidercastle");
        craftingManager.createCategory("E-C-A", "objects", "decorations");
        craftingManager.createCategory("E-C-B", "objects", "decorations", "paintings");
        craftingManager.createCategory("E-C-C", "objects", "decorations", "carpets");
        craftingManager.createCategory("E-C-D", "objects", "decorations", "pots");
        craftingManager.createCategory("E-C-E", "objects", "decorations", "banners");
        craftingManager.createCategory("E-D-A", "objects", "wallsanddoors");
        craftingManager.createCategory("E-E-A", "objects", "fencesandgates");
        craftingManager.createCategory("E-F-A", "objects", "columns");
        craftingManager.createCategory("E-G-A", "objects", "landscaping");
        craftingManager.createCategory("E-G-B", "objects", "landscaping", "misc");
        craftingManager.createCategory("E-G-C", "objects", "landscaping", "tabledecorations");
        craftingManager.createCategory("E-G-D", "objects", "landscaping", "plants");
        craftingManager.createCategory("E-G-E", "objects", "landscaping", "masonry");
        craftingManager.createCategory("E-G-F", "objects", "landscaping", "rocksandores");
        craftingManager.createCategory("E-G-G", "objects", "landscaping", "forestrocksandores");
        craftingManager.createCategory("E-G-H", "objects", "landscaping", "snowrocksandores");
        craftingManager.createCategory("E-G-I", "objects", "landscaping", "plainsrocksandores");
        craftingManager.createCategory("E-G-J", "objects", "landscaping", "swamprocksandores");
        craftingManager.createCategory("E-G-K", "objects", "landscaping", "desertrocksandores");
        craftingManager.createCategory("E-G-L", "objects", "landscaping", "incursionrocksandores");
        craftingManager.createCategory("E-G-M", "objects", "landscaping", "crystals");
        craftingManager.createCategory("E-H-A", "objects", "misc");
        craftingManager.createCategory("F-A-A", "materials");
        craftingManager.createCategory("G-A-A", "tiles");
        craftingManager.createCategory("H-A-A", "wiring");
        craftingManager.createCategory("I-A-A", "misc");
        craftingManager.createCategory("Z-Z-Z", new LocalMessage("itemcategory", "craftingstations"), "finalcraftingstations");
    }
}

