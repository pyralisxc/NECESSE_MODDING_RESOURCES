/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.util.GameUtils
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.gfx.drawOptions.DrawOptions
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 *  necesse.level.maps.light.GameLight
 */
package aphorea.items.banners;

import aphorea.items.banners.logic.AphAbilityBanner;
import aphorea.registry.AphBuffs;
import aphorea.utils.AphColors;
import aphorea.utils.area.AphArea;
import aphorea.utils.area.AphAreaList;
import java.awt.Color;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BlankBannerItem
extends AphAbilityBanner {
    static int range = 200;
    static Color color = AphColors.green;
    public static AphAreaList areaList = new AphAreaList(new AphArea((float)range, 0.5f, color).setHealingArea(4));

    public BlankBannerItem() {
        super(Item.Rarity.NORMAL, 480, (Mob m) -> AphBuffs.BANNER.BLANK, 80, 10, new String[0]);
    }

    @Override
    public void runServerAbility(Level level, InventoryItem item, PlayerMob player) {
        areaList.execute((Mob)player, true);
    }

    public DrawOptions getStandDrawOptions(Level level, int tileX, int tileY, int drawX, int drawY, GameLight light) {
        int anim = GameUtils.getAnim((long)(level.getWorldEntity().getTime() + (long)tileX * 97L + (long)tileY * 151L), (int)5, (int)800);
        int xOffset = -30;
        int yOffset = -32;
        return this.holdTexture.initDraw().sprite(anim, 2, 128).light(light).pos(drawX - 16 + xOffset, drawY - 40 + yOffset + (anim % 2 != 0 ? 0 : 2));
    }

    @Override
    public void addExtraTooltips(ListGameTooltips tooltips, PlayerMob perspective) {
        areaList.addAreasToolTip(tooltips, (Attacker)perspective, true, null, null);
    }
}

