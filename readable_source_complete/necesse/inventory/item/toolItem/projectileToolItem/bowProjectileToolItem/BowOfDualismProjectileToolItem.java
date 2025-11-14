/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.GritArrowProjectile;
import necesse.entity.projectile.SageArrowProjectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.BowProjectileToolItem;
import necesse.inventory.lootTable.presets.BowWeaponsLootTable;
import necesse.level.maps.Level;

public class BowOfDualismProjectileToolItem
extends BowProjectileToolItem {
    public BowOfDualismProjectileToolItem() {
        super(1800, BowWeaponsLootTable.bowWeapons);
        this.attackAnimTime.setBaseValue(450);
        this.rarity = Item.Rarity.RARE;
        this.attackDamage.setBaseValue(52.0f).setUpgradedValue(1.0f, 93.33336f);
        this.velocity.setBaseValue(220);
        this.attackRange.setBaseValue(800);
        this.attackXOffset = 12;
        this.attackYOffset = 28;
        this.canBeUsedForRaids = true;
        this.raidTicketsModifier = 0.25f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        tooltips.add(Localization.translate("itemtooltip", "bowofdualismtip"));
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        Point attackPosition = super.getItemAttackerAttackPosition(level, attackerMob, target, seed, item);
        float distance = attackerMob.getDistance(attackPosition.x, attackPosition.y);
        float angleOffsetMod = Math.min(distance / 300.0f, 1.0f);
        float attackAngle = GameMath.getAngle(GameMath.normalize((float)attackPosition.x - attackerMob.x, (float)attackPosition.y - attackerMob.y));
        float angleOffset = 5.0f * angleOffsetMod;
        if (new GameRandom(seed + 1337).nextBoolean()) {
            angleOffset = -angleOffset;
        }
        Point2D.Float newDir = GameMath.getAngleDir(attackAngle + angleOffset);
        return new Point((int)(attackerMob.x + newDir.x * distance), (int)(attackerMob.y + newDir.y * distance));
    }

    @Override
    protected void fireProjectiles(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean dropItem, GNDItemMap mapContent) {
        GameRandom random = new GameRandom(seed);
        boolean left = (float)x < attackerMob.x;
        GritArrowProjectile gritProjectile = new GritArrowProjectile(attackerMob, attackerMob.x, attackerMob.y, x, y, (float)this.getProjectileVelocity(item, attackerMob) * arrow.speedMod, this.getAttackRange(item), this.getAttackDamage(item).add(arrow.damage, arrow.armorPen, arrow.critChance), this.getKnockback(item, attackerMob));
        gritProjectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        gritProjectile.setAngle(gritProjectile.getAngle() + (float)(left ? 5 : -5));
        gritProjectile.dropItem = false;
        gritProjectile.getUniqueID(random);
        SageArrowProjectile sageProjectile = new SageArrowProjectile(attackerMob, attackerMob.x, attackerMob.y, x, y, (float)this.getProjectileVelocity(item, attackerMob) * arrow.speedMod, this.getAttackRange(item), this.getAttackDamage(item).add(arrow.damage, arrow.armorPen, arrow.critChance), this.getKnockback(item, attackerMob));
        sageProjectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        sageProjectile.setAngle(sageProjectile.getAngle() - (float)(left ? 5 : -5));
        sageProjectile.dropItem = false;
        sageProjectile.getUniqueID(random);
        attackerMob.addAndSendAttackerProjectile(gritProjectile);
        attackerMob.addAndSendAttackerProjectile(sageProjectile);
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.magicbolt4).volume(0.3f);
    }
}

