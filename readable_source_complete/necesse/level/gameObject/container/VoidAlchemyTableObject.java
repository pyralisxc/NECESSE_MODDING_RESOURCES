/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject.container;

import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Tech;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.ObjectDamagedTextureArray;
import necesse.level.gameObject.container.AlchemyTableObject;
import necesse.level.gameObject.container.CraftingStationUpgrade;

public class VoidAlchemyTableObject
extends AlchemyTableObject {
    public VoidAlchemyTableObject() {
        this.mapColor = new Color(148, 99, 25);
        this.rarity = Item.Rarity.COMMON;
    }

    @Override
    public CraftingStationUpgrade getStationUpgrade() {
        return new CraftingStationUpgrade(ObjectRegistry.getObject("caveglowalchemytable"), new Ingredient("glassbottle", 6), new Ingredient("caveglow", 12));
    }

    @Override
    public void loadTextures() {
        this.texture = ObjectDamagedTextureArray.loadAndApplyOverlay((GameObject)this, "objects/voidalchemytable");
    }

    @Override
    public Tech[] getCraftingTechs() {
        return new Tech[]{RecipeTechRegistry.VOID_ALCHEMY, RecipeTechRegistry.ALCHEMY};
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "alchemytabletip"));
        return tooltips;
    }
}

