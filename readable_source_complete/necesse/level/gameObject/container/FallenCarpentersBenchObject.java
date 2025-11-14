/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.CarpentersBenchObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.FallenCarpentersBench2Object;

public class FallenCarpentersBenchObject
extends CarpentersBenchObject {
    protected FallenCarpentersBenchObject() {
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallencarpentersbench");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FALLEN_CARPENTER, RecipeTechRegistry.TUNGSTEN_CARPENTER, RecipeTechRegistry.CARPENTER};
    }

    public static int[] registerFallenCarpentersBench() {
        int cb2i;
        FallenCarpentersBenchObject cb1o = new FallenCarpentersBenchObject();
        FallenCarpentersBench2Object cb2o = new FallenCarpentersBench2Object();
        int cb1i = ObjectRegistry.registerObject("fallencarpentersbench", cb1o, 20.0f, true);
        cb1o.counterID = cb2i = ObjectRegistry.registerObject("fallencarpentersbench2", cb2o, 0.0f, false);
        cb2o.counterID = cb1i;
        return new int[]{cb1i, cb2i};
    }
}

