/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.util.HashSet;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.DemonicWorkstationDuo2Object;
import necesse.level.gameObject.container.WorkstationDuoObject;

public class DemonicWorkstationDuoObject
extends WorkstationDuoObject {
    protected DemonicWorkstationDuoObject() {
        this.mapColor = new Color(156, 51, 39);
        this.rarity = Item.Rarity.COMMON;
        this.lightLevel = 100;
        this.lightHue = 270.0f;
        this.lightSat = 0.3f;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "demonicworkstation");
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("tungstenworkstation"), new Ingredient("tungstenbar", 8), new Ingredient("quartz", 4));
    }

    @Override
    public HashSet<ItemCategory> getForcedSoloCraftingCategories() {
        HashSet<ItemCategory> depths = super.getForcedSoloCraftingCategories();
        depths.add(ItemCategory.craftingManager.getCategory("equipment", "trinkets"));
        return depths;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/demonicworkstationduo");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.DEMONIC_WORKSTATION, RecipeTechRegistry.WORKSTATION};
    }

    public static int[] registerWorkstation() {
        int i2;
        DemonicWorkstationDuoObject o1 = new DemonicWorkstationDuoObject();
        DemonicWorkstationDuo2Object o2 = new DemonicWorkstationDuo2Object();
        int i1 = ObjectRegistry.registerObject("demonicworkstationduo", o1, 40.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("demonicworkstationduo2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}

