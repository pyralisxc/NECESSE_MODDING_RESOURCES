/*
 * Decompiled with CFR 0.152.
 */
package necesse.level.gameObject;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.gameObject.PaintingObject;

public class ChristmasWreathObject
extends PaintingObject {
    public ChristmasWreathObject() {
        super(Item.Rarity.UNCOMMON);
        this.texturePath = "christmaswreath";
    }

    @Override
    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        tooltips.add(Localization.translate("itemtooltip", "christmastreetip"));
        return tooltips;
    }
}

