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
import necesse.level.gameObject.container.TungstenWorkstation2Object;
import necesse.level.gameObject.container.WorkstationDuoObject;

public class TungstenWorkstationObject
extends WorkstationDuoObject {
    protected TungstenWorkstationObject() {
        this.mapColor = new Color(65, 69, 89);
        this.rarity = Item.Rarity.UNCOMMON;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "tungstenworkstation");
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("fallenworkstation"), new Ingredient("upgradeshard", 15), new Ingredient("alchemyshard", 15));
    }

    @Override
    public HashSet<ItemCategory> getForcedSoloCraftingCategories() {
        HashSet<ItemCategory> depths = super.getForcedSoloCraftingCategories();
        depths.add(ItemCategory.craftingManager.getCategory("equipment", "trinkets"));
        return depths;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/tungstenworkstation");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.TUNGSTEN_WORKSTATION, RecipeTechRegistry.DEMONIC_WORKSTATION, RecipeTechRegistry.WORKSTATION};
    }

    public static int[] registerWorkstation() {
        int i2;
        TungstenWorkstationObject o1 = new TungstenWorkstationObject();
        TungstenWorkstation2Object o2 = new TungstenWorkstation2Object();
        int i1 = ObjectRegistry.registerObject("tungstenworkstation", o1, 140.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("tungstenworkstation2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}

