/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.inventory.item.ItemCategory
 */
package aphorea.registry;

import necesse.inventory.item.ItemCategory;

public class AphItemCategories {
    public static void registerCore() {
        ItemCategory.createCategory((String)"A-A-E", (String[])new String[]{"equipment", "tools", "healing"});
        ItemCategory.createCategory((String)"A-F-A", (String[])new String[]{"misc", "runes"});
        ItemCategory.createCategory((String)"A-F-A", (String[])new String[]{"misc", "runes", "baserunes"});
        ItemCategory.createCategory((String)"A-F-B", (String[])new String[]{"misc", "runes", "modifierrunes"});
        AphItemCategories.equipmentCategories();
        AphItemCategories.craftingCategories();
    }

    public static void equipmentCategories() {
        ItemCategory.equipmentManager.createCategory("C-A-A", new String[]{"tools"});
        ItemCategory.equipmentManager.createCategory("C-B-A", new String[]{"tools", "healingtools"});
    }

    public static void craftingCategories() {
        ItemCategory.craftingManager.createCategory("D-B-F", new String[]{"equipment", "tools", "healingtools"});
        ItemCategory.craftingManager.createCategory("J-A-A", new String[]{"runes"});
        ItemCategory.craftingManager.createCategory("J-A-A", new String[]{"runes", "runesinjectors"});
        ItemCategory.craftingManager.createCategory("J-B-A", new String[]{"runes", "baserunes"});
        ItemCategory.craftingManager.createCategory("J-c-A", new String[]{"runes", "modifierrunes"});
    }
}

