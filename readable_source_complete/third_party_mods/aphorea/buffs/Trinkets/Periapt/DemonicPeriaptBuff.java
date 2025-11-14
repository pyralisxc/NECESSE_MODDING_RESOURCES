/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Periapt;

import aphorea.AphDependencies;
import aphorea.buffs.Trinkets.Periapt.AphPeriaptActivableBuff;
import aphorea.utils.AphColors;
import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class DemonicPeriaptBuff
extends AphPeriaptActivableBuff {
    public boolean hasRPGMod = AphDependencies.checkRPGMod();

    public DemonicPeriaptBuff() {
        super("demonicperiaptactive");
    }

    @Override
    public Color getColor() {
        return (Color)GameRandom.globalRandom.getOneOf((Object[])AphColors.paletteDemonic);
    }

    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"periaptboost"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"demonicperiapt", (String)"amount", (String)(this.hasRPGMod ? "0.2" : "2")));
        if (this.hasRPGMod) {
            tooltips.add(Localization.translate((String)"itemtooltip", (String)"rpgmodnerf"));
        }
        return tooltips;
    }
}

