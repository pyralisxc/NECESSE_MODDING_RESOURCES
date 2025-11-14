/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  necesse.engine.localization.Localization
 *  necesse.engine.network.Packet
 *  necesse.engine.network.gameNetworkData.GNDItemMap
 *  necesse.engine.registries.BuffRegistry
 *  necesse.engine.registries.ItemRegistry
 *  necesse.engine.util.GameBlackboard
 *  necesse.engine.util.GameMath
 *  necesse.entity.mobs.GameDamage
 *  necesse.entity.mobs.Mob
 *  necesse.entity.mobs.PlayerMob
 *  necesse.entity.mobs.buffs.ActiveBuff
 *  necesse.entity.mobs.itemAttacker.ItemAttackSlot
 *  necesse.entity.mobs.itemAttacker.ItemAttackerMob
 *  necesse.entity.projectile.Projectile
 *  necesse.gfx.gameTooltips.ListGameTooltips
 *  necesse.inventory.InventoryItem
 *  necesse.inventory.item.Item$Rarity
 *  necesse.inventory.item.arrowItem.ArrowItem
 *  necesse.level.maps.Level
 */
package aphorea.items.tools.weapons.range.greatbow;

import aphorea.items.vanillaitemtypes.weapons.AphGreatbowProjectileToolItem;
import aphorea.packets.AphCustomPushPacket;
import aphorea.utils.AphColors;
import aphorea.utils.AphMaths;
import java.awt.geom.Point2D;
import java.util.Objects;
import necesse.engine.localization.Localization;
import necesse.engine.network.Packet;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.level.maps.Level;

public class UnstableGelGreatbow
extends AphGreatbowProjectileToolItem {
    public UnstableGelGreatbow() {
        super(400);
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(700);
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 50.0f);
        this.attackRange.setBaseValue(800);
        this.velocity.setBaseValue(300);
        this.attackXOffset = 10;
        this.attackYOffset = 34;
        this.particleColor = AphColors.unstableGel;
    }

    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        for (int i = 0; i < 3; ++i) {
            float endX = x;
            float endY = y;
            float[] vector = AphMaths.perpendicularVector(x, y, attackerMob.x, attackerMob.y);
            if (i == 1) {
                endX = (float)x + vector[0] / 4.0f;
                endY = (float)y + vector[1] / 4.0f;
            } else if (i == 2) {
                endX = (float)x - vector[0] / 4.0f;
                endY = (float)y - vector[1] / 4.0f;
            }
            super.fireProjectiles(level, (int)endX, (int)endY, attackerMob, item, seed, arrow, i == 2, mapContent);
        }
    }

    public InventoryItem superOnAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        super.superOnAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
        attackerMob.buffManager.addBuff(new ActiveBuff(BuffRegistry.FOW_ACTIVE, (Mob)attackerMob, 0.15f, null), level.isServer());
        attackerMob.buffManager.forceUpdateBuffs();
        if (attackerMob.isServer()) {
            int strength = 50;
            Point2D.Float dir = GameMath.normalize((float)((float)x - attackerMob.x), (float)((float)y - attackerMob.y));
            level.getServer().network.sendToClientsAtEntireLevel((Packet)new AphCustomPushPacket((Mob)attackerMob, -dir.x, -dir.y, strength), level);
        }
        return item;
    }

    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"stikybuff2"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"unstablegelgreatbow"));
        tooltips.add(Localization.translate((String)"itemtooltip", (String)"threearrows"));
        return tooltips;
    }

    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob owner, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, float velocity, int range, GameDamage damage, int knockback, float resilienceGain, GNDItemMap mapContent) {
        if (Objects.equals(arrow.getStringID(), "stonearrow") || Objects.equals(arrow.getStringID(), "gelarrow")) {
            return super.getProjectile(level, x, y, owner, item, seed, (ArrowItem)ItemRegistry.getItem((String)"unstablegelarrow"), consumeAmmo, velocity, range, damage, knockback, resilienceGain, mapContent);
        }
        return super.getProjectile(level, x, y, owner, item, seed, arrow, consumeAmmo, velocity, range, damage, knockback, resilienceGain, mapContent);
    }
}

