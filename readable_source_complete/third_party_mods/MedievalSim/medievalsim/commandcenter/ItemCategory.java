/*
 * Decompiled with CFR 0.152.
 */
package medievalsim.commandcenter;

public enum ItemCategory {
    ALL("All Items"),
    WEAPON("Weapons"),
    ARMOR("Armor"),
    TOOL("Tools"),
    MATERIAL("Materials"),
    CONSUMABLE("Consumables"),
    POTION("Potions"),
    ACCESSORY("Accessories"),
    FURNITURE("Furniture"),
    SEED("Seeds"),
    QUEST("Quest Items"),
    MISC("Miscellaneous");

    private final String displayName;

    private ItemCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return this.displayName;
    }
}

