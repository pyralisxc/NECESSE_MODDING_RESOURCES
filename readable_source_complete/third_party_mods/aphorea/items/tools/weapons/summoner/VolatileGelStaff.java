/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.entity.mobs.itemAttacker.FollowPosition
 *  necesse.inventory.item.Item$Rarity
 */
package aphorea.items.tools.weapons.summoner;

import aphorea.items.vanillaitemtypes.weapons.AphSummonToolItem;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.inventory.item.Item;

public class VolatileGelStaff
extends AphSummonToolItem {
    public VolatileGelStaff() {
        super("volatilegelslime", FollowPosition.WALK_CLOSE, 1.0f, 400);
        this.summonType = "summonedmobtemp";
        this.rarity = Item.Rarity.COMMON;
        this.attackDamage.setBaseValue(60.0f).setUpgradedValue(1.0f, 120.0f);
    }
}

