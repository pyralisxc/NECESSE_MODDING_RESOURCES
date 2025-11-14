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
import necesse.level.gameObject.container.CarpentersBenchObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.TungstenCarpentersBench2Object;

public class TungstenCarpentersBenchObject
extends CarpentersBenchObject {
    protected TungstenCarpentersBenchObject() {
        this.rarity = Item.Rarity.UNCOMMON;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("fallencarpentersbench"), new Ingredient("dryadlog", 16), new Ingredient("bone", 6), new Ingredient("bamboo", 4), new Ingredient("upgradeshard", 5));
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/tungstencarpentersbench");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.TUNGSTEN_CARPENTER, RecipeTechRegistry.CARPENTER};
    }

    public static int[] registerTungstenCarpentersBench() {
        int cb2i;
        TungstenCarpentersBenchObject cb1o = new TungstenCarpentersBenchObject();
        TungstenCarpentersBench2Object cb2o = new TungstenCarpentersBench2Object();
        int cb1i = ObjectRegistry.registerObject("tungstencarpentersbench", cb1o, 20.0f, true);
        cb1o.counterID = cb2i = ObjectRegistry.registerObject("tungstencarpentersbench2", cb2o, 0.0f, false);
        cb2o.counterID = cb1i;
        return new int[]{cb1i, cb2i};
    }
}

