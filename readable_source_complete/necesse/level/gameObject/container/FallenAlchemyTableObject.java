/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.AlchemyTableObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;

public class FallenAlchemyTableObject
extends AlchemyTableObject {
    public FallenAlchemyTableObject() {
        this.mapColor = new Color(90, 89, 97);
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "alchemytabletip"));
        return tooltips;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallenalchemytable");
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FALLEN_ALCHEMY, RecipeTechRegistry.CAVEGLOW_ALCHEMY, RecipeTechRegistry.VOID_ALCHEMY, RecipeTechRegistry.ALCHEMY};
    }
}

