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
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.FallenLandscapingStation2Object;
import necesse.level.gameObject.container.LandscapingStationObject;

public class FallenLandscapingStationObject
extends LandscapingStationObject {
    public static int[] registerFallenLandscapingStation() {
        int cb2i;
        FallenLandscapingStationObject o1 = new FallenLandscapingStationObject();
        FallenLandscapingStation2Object o2 = new FallenLandscapingStation2Object();
        int cb1i = ObjectRegistry.registerObject("fallenlandscapingstation", o1, 20.0f, true);
        o1.counterID = cb2i = ObjectRegistry.registerObject("fallenlandscapingstation2", o2, 0.0f, false);
        o2.counterID = cb1i;
        return new int[]{cb1i, cb2i};
    }

    protected FallenLandscapingStationObject() {
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallenlandscapingstation");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FALLEN_LANDSCAPING, RecipeTechRegistry.TUNGSTEN_LANDSCAPING, RecipeTechRegistry.LANDSCAPING};
    }
}

