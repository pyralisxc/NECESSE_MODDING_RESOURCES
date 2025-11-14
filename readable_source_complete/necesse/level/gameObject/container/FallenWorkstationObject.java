/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.HashSet;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.FallenWorkstation2Object;
import necesse.level.gameObject.container.WorkstationDuoObject;

public class FallenWorkstationObject
extends WorkstationDuoObject {
    protected FallenWorkstationObject() {
        this.mapColor = new Color(0, 107, 109);
        this.rarity = Item.Rarity.RARE;
        this.toolType = ToolType.ALL;
        this.isLightTransparent = true;
        this.lightLevel = 100;
        this.lightHue = 220.0f;
        this.lightSat = 0.2f;
        this.hoverHitbox = new Rectangle(0, -16, 32, 48);
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    @Override
    public GameMessage getNewLocalization() {
        return new LocalMessage("object", "fallenworkstation");
    }

    @Override
    public HashSet<ItemCategory> getForcedSoloCraftingCategories() {
        HashSet<ItemCategory> depths = super.getForcedSoloCraftingCategories();
        depths.add(ItemCategory.craftingManager.getCategory("equipment", "trinkets"));
        return depths;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallenworkstation");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FALLEN_WORKSTATION, RecipeTechRegistry.ADVANCED_WORKSTATION, RecipeTechRegistry.DEMONIC_WORKSTATION, RecipeTechRegistry.WORKSTATION};
    }

    public static int[] registerWorkstation() {
        int i2;
        FallenWorkstationObject o1 = new FallenWorkstationObject();
        FallenWorkstation2Object o2 = new FallenWorkstation2Object();
        int i1 = ObjectRegistry.registerObject("fallenworkstation", o1, 200.0f, true);
        o1.counterID = i2 = ObjectRegistry.registerObject("fallenworkstation2", o2, 0.0f, false);
        o2.counterID = i1;
        return new int[]{i1, i2};
    }
}

