/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.level.gameObject.container.CraftingStationObject
 */
package aphorea.objects;

import java.awt.Rectangle;
import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.container.CraftingStationObject;

public abstract class AphCraftingStationObject
extends CraftingStationObject {
    public AphCraftingStationObject(Rectangle collision) {
        super(collision);
    }

    public ListGameTooltips getItemTooltips(InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getItemTooltips(item, perspective);
        this.addItemToolTips(tooltips, item, perspective);
        tooltips.add(Localization.translate((String)"global", (String)"aphorea"));
        return tooltips;
    }

    public void addItemToolTips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective) {
    }
}

