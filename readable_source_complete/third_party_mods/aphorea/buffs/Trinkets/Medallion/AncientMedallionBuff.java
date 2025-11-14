/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.registries.DamageTypeRegistry
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.trinketItem.TrinketItem
 */
package aphorea.buffs.Trinkets.Medallion;

import aphorea.buffs.Trinkets.AphAreaWhenHealTrinketBuff;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import necesse.engine.localization.Localization;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.trinketItem.TrinketItem;

public class AncientMedallionBuff
extends AphAreaWhenHealTrinketBuff {
    static int range = 300;
    static Color color = AphColors.darker_magic;
    public static AphAreaList areaList = new AphAreaList(new AphArea((float)range, color).setDamageArea(new GameDamage(DamageTypeRegistry.MAGIC, 30.0f, 10.0f)).setDebuffArea(5000, "cursedbuff"));

    public AncientMedallionBuff() {
        super(30, areaList);
    }

    @Override
    public ListGameTooltips getTrinketTooltip(TrinketItem trinketItem, InventoryItem item, PlayerMob perspective) {
        ListGameTooltips tooltips = super.getTrinketTooltip(trinketItem, item, perspective);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"cursedbuff"));
        return tooltips;
    }
}

