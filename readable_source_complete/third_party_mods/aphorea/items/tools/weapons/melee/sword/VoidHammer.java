/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.entity.projectile.modifiers.ProjectileModifier
 *  necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.melee.sword;

import aphorea.items.vanillaitemtypes.weapons.AphHeavyHammerProjectileToolItem;
import aphorea.projectiles.toolitem.VoidStoneProjectile;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class VoidHammer
extends AphHeavyHammerProjectileToolItem {
    public VoidHammer() {
        super(650);
        this.attackRange.setBaseValue(400);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 50.0f);
        this.rarity = Item.Rarity.RARE;
        this.attackXOffset = 12;
        this.attackYOffset = 12;
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        VoidStoneProjectile projectile = new VoidStoneProjectile((Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item), this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob));
        projectile.setModifier((ProjectileModifier)new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile);
        return item;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"voidhammer"));
        return tooltips;
    }
}

