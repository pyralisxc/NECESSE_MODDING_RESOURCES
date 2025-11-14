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
import necesse.level.gameObject.container.CraftingStationUpgrade;
import necesse.level.gameObject.container.IronAnvilObject;

public class FallenAnvilObject
extends IronAnvilObject {
    public FallenAnvilObject() {
        this.mapColor = new Color(51, 53, 56);
        this.rarity = Item.Rarity.RARE;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return null;
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/fallenanvil");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.FALLEN_ANVIL, RecipeTechRegistry.TUNGSTEN_ANVIL, RecipeTechRegistry.DEMONIC_ANVIL, RecipeTechRegistry.IRON_ANVIL};
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "anviltip"));
        return tooltips;
    }
}

