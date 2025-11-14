/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.engine.util.GameRandom
 *  necesse.entity.mobs.Attacker
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
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
package aphorea.items.tools.weapons.throwable;

import aphorea.items.vanillaitemtypes.weapons.AphThrowToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.projectiles.toolitem.UnstableGelvelineProjectile;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ProjectileModifier;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.Level;

public class UnstableGelveline
extends AphThrowToolItem {
    float topBaseDamage = 30.0f;
    float topTier1Damage = 80.0f;

    public UnstableGelveline() {
        super(400);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(250);
        this.attackCooldownTime.setBaseValue(500);
        this.attackDamage.setBaseValue(this.topBaseDamage).setUpgradedValue(1.0f, this.topTier1Damage);
        this.velocity.setBaseValue(200);
        this.attackRange.setBaseValue(1000);
    }

    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();
        if (attackerMob.isServer()) {
            int strength = 60;
            Point2D.Float dir = GameMath.normalize((float)((float)x - attackerMob.x), (float)((float)y - attackerMob.y));
            level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, dir.x, dir.y, strength), level);
        }
        UnstableGelvelineProjectile projectile = new UnstableGelvelineProjectile(this.getAttackDamage(item), this.getKnockback(item, (Attacker)attackerMob), this, item, level, (Mob)attackerMob, attackerMob.x, attackerMob.y, x, y, this.getProjectileVelocity(item, (Mob)attackerMob), this.getAttackRange(item));
        projectile.setModifier((ProjectileModifier)new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom((long)seed));
        attackerMob.addAndSendAttackerProjectile((Projectile)projectile);
        return item;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"projectilearea"));
        return tooltips;
    }
}

