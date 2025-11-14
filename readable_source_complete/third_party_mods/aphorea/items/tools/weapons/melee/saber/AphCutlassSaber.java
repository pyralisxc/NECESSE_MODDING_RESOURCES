/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.util.GameBlackboard
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 *  necesse.level.maps.incursion.IncursionData
 */
package aphorea.items.tools.weapons.melee.saber;

import aphorea.items.tools.weapons.melee.saber.AphSaberToolItem;
import aphorea.projectiles.toolitem.AircutProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class AphCutlassSaber
extends AphSaberToolItem {
    public AphCutlassSaber() {
        super(1150, true);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(40.0f).setUpgradedValue(1.0f, 80.0f);
        this.attackRange.setBaseValue(70);
        this.knockback.setBaseValue(80);
        this.attackXOffset = 12;
        this.attackYOffset = 22;
        this.canBeUsedForRaids = true;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.chargeAnimTime.setBaseValue(500);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, int targetX, int targetY, ItemAttackerMob attackerMob, InventoryItem item, float powerPercent, int seed) {
        return new AircutProjectile.GoldAircutProjectile(level, (Mob)attackerMob, x, y, targetX, targetY, 400.0f * powerPercent, (int)(500.0f * powerPercent), this.getAttackDamage(item).modDamage(powerPercent * 0.5f), (int)((float)this.getKnockback(item, (Attacker)attackerMob) * powerPercent));
    }

    @Override
    public ListGameTooltips getPostEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPostEnchantmentTooltips(item, perspective, blackboard);
        tooltips.removeLast();
        tooltips.add(Localization.translate((String)"global", (String)"aphorearework"));
        return tooltips;
    }
}

