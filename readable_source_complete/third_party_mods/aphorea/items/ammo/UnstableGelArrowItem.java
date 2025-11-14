/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.toolItem.ToolItem
 */
package aphorea.items.ammo;

import aphorea.items.vanillaitemtypes.AphArrowItem;
import aphorea.projectiles.arrow.UnstableGelArrowProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.ToolItem;

public class UnstableGelArrowItem
extends AphArrowItem {
    public UnstableGelArrowItem() {
        this.damage = 10;
    }

    public Projectile getProjectile(float x, float y, float targetX, float targetY, float velocity, int range, GameDamage damage, int knockback, ItemAttackerMob owner) {
        ToolItem toolItem = null;
        InventoryItem item = null;
        if (owner.isPlayer) {
            PlayerMob player = (PlayerMob)owner;
            item = player.attackSlot.getItem(player.getInv());
            toolItem = (ToolItem)item.item;
        }
        return new UnstableGelArrowProjectile(damage, knockback, toolItem, item, owner.getLevel(), (Mob)owner, x, y, targetX, targetY, velocity, range);
    }

    protected ListGameTooltips getBaseTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getBaseTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"projectilearea"));
        return tooltips;
    }
}

