/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.LandscapingStationObject;
import necesse.level.gameObject.container.TungstenLandscapingStation2Object;

public class TungstenLandscapingStationObject
extends LandscapingStationObject {
    public static int[] registerTungstenLandscapingStation() {
        int cb2i;
        TungstenLandscapingStationObject o1 = new TungstenLandscapingStationObject();
        TungstenLandscapingStation2Object o2 = new TungstenLandscapingStation2Object();
        int cb1i = ObjectRegistry.registerObject("tungstenlandscapingstation", o1, 20.0f, true);
        o1.counterID = cb2i = ObjectRegistry.registerObject("tungstenlandscapingstation2", o2, 0.0f, false);
        o2.counterID = cb1i;
        return new int[]{cb1i, cb2i};
    }

    protected TungstenLandscapingStationObject() {
        this.rarity = Item.Rarity.UNCOMMON;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("fallenlandscapingstation"), new Ingredient("basalt", 12), new Ingredient("emerald", 6), new Ingredient("alchemyshard", 5));
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/tungstenlandscapingstation");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.TUNGSTEN_LANDSCAPING, RecipeTechRegistry.LANDSCAPING};
    }
}

