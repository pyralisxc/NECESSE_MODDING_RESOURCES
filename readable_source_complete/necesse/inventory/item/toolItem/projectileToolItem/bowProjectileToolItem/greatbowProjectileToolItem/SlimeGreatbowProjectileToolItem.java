/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SlimeGreatBowArrowProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.arrowItem.ArrowItem;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;
import necesse.inventory.lootTable.presets.IncursionGreatbowWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;
import necesse.level.maps.incursion.IncursionData;

public class SlimeGreatbowProjectileToolItem
extends GreatbowProjectileToolItem {
    public int attackSpriteStretch = 8;
    public Color particleColor = new Color(255, 219, 36);
    public int projectileMaxHeight;

    public SlimeGreatbowProjectileToolItem() {
        super(1900, IncursionGreatbowWeaponsLootTable.incursionGreatbowWeapons);
        this.knockback.setBaseValue(10);
        this.projectileMaxHeight = 600;
        this.attackAnimTime.setBaseValue(500);
        this.rarity = Item.Rarity.EPIC;
        this.attackDamage.setBaseValue(96.0f).setUpgradedValue(1.0f, 122.50003f);
        this.velocity.setBaseValue(350);
        this.attackRange.setBaseValue(1600);
        this.attackXOffset = 12;
        this.attackYOffset = 38;
        this.resilienceGain.setBaseValue(1.0f);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.2f;
        this.useForRaidsOnlyIfObtained = true;
    }

    public SlimeGreatBowArrowProjectile getSlimeGreatbowProjectile(Level level, int x, int y, Mob owner, GameDamage damage, float velocity, float range, float knockback, float resilienceGain) {
        Point2D.Float targetPoints = new Point2D.Float(x, y);
        Point2D.Float normalizedVector = GameMath.normalize(targetPoints.x - owner.x, targetPoints.y - owner.y);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)owner.x, (double)owner.y, (double)normalizedVector.x, (double)normalizedVector.y, targetPoints.distance(owner.x, owner.y), 0, new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock));
        if (!hits.isEmpty()) {
            Ray first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            targetPoints.y = (float)first.y2;
            Point2D.Double hitVector = GameMath.normalize(first.x1 - first.x2, first.y1 - first.y2);
            targetPoints.x = (float)((double)targetPoints.x + hitVector.x * 2.0);
            targetPoints.y = (float)((double)targetPoints.y + hitVector.y * 2.0);
        }
        return new SlimeGreatBowArrowProjectile(level, owner, owner.x, owner.y, owner.x, owner.y - 1.0f, velocity, this.projectileMaxHeight, damage, resilienceGain, (int)knockback, targetPoints, false);
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        drawOptions.rotation(-85.0f);
    }

    @Override
    protected void addExtraBowTooltips(ListGameTooltips tooltips, InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        super.addExtraBowTooltips(tooltips, item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "slimegreatbowtip"), 400);
    }

    @Override
    public Projectile getProjectile(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item, int seed, ArrowItem arrow, boolean consumeAmmo, GNDItemMap mapContent) {
        float knockbackMod;
        float damageMod;
        float rangeMod;
        float velocityMod;
        float percentCharge = item.getGndData().getFloat("chargePercent");
        if ((percentCharge = GameMath.limit(percentCharge, 0.0f, 1.0f)) >= 1.0f) {
            velocityMod = 1.0f;
            rangeMod = 1.0f;
            damageMod = 1.0f;
            knockbackMod = 1.0f;
        } else {
            velocityMod = GameMath.lerp(percentCharge, 0.1f, 0.4f);
            rangeMod = GameMath.lerp(percentCharge, 0.05f, 0.4f);
            damageMod = GameMath.lerp(percentCharge, 0.05f, 0.4f);
            knockbackMod = GameMath.lerp(percentCharge, 0.05f, 0.2f);
        }
        GameDamage damage = arrow.modDamage(this.getAttackDamage(item)).modDamage(damageMod);
        float velocity = arrow.modVelocity(this.getProjectileVelocity(item, attackerMob)) * velocityMod;
        float range = (float)arrow.modRange(this.getAttackRange(item)) * rangeMod;
        float knockback = (float)arrow.modKnockback(this.getKnockback(item, attackerMob)) * knockbackMod;
        float resilienceGain = this.getResilienceGain(item);
        return this.getSlimeGreatbowProjectile(level, x, y, attackerMob, damage, velocity, range, knockback, resilienceGain);
    }

    @Override
    public Point getControllerAttackLevelPos(Level level, float aimDirX, float aimDirY, PlayerMob player, InventoryItem item) {
        float range = 500.0f;
        return new Point((int)(player.x + aimDirX * range), (int)(player.y + aimDirY * range));
    }

    @Override
    protected SoundSettings getGreatbowShootSoundWeak() {
        return new SoundSettings(GameResources.slimeGreatBowWeak).volume(0.4f);
    }

    @Override
    protected SoundSettings getGreatbowShootSoundStrong() {
        return new SoundSettings(GameResources.slimeGreatBowStrong).volume(0.6f);
    }
}

