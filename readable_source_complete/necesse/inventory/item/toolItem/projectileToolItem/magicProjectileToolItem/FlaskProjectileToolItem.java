/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.Entity;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.modifiers.ResilienceOnHitProjectileModifier;
import necesse.gfx.GameResources;
import necesse.gfx.drawOptions.itemAttack.ItemAttackDrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.projectileToolItem.magicProjectileToolItem.MagicProjectileToolItem;
import necesse.inventory.lootTable.lootItem.OneOfLootItems;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public abstract class FlaskProjectileToolItem
extends MagicProjectileToolItem {
    public FlaskProjectileToolItem(int enchantCost, OneOfLootItems lootTableCategory) {
        super(enchantCost, lootTableCategory);
        this.rarity = Item.Rarity.RARE;
        this.attackAnimTime.setBaseValue(500);
        this.attackDamage.setBaseValue(20.0f).setUpgradedValue(1.0f, 126.00004f);
        this.velocity.setBaseValue(800);
        this.attackXOffset = 8;
        this.attackYOffset = 10;
        this.attackCooldownTime.setBaseValue(500);
        this.attackRange.setBaseValue(300);
        this.manaCost.setBaseValue(1.25f).setUpgradedValue(1.0f, 1.25f);
        this.resilienceGain.setBaseValue(1.0f);
        this.itemAttackerProjectileCanHitWidth = 5.0f;
        this.canBeUsedForRaids = false;
    }

    protected abstract Projectile getProjectile(Level var1, Mob var2, float var3, float var4, float var5, float var6, float var7, int var8, GameDamage var9, int var10, GameRandom var11);

    @Override
    public GameSprite getAttackSprite(InventoryItem item, PlayerMob player) {
        return null;
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.swing1, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.7f).pitch(GameRandom.globalRandom.getFloatBetween(0.9f, 1.0f)));
        }
    }

    @Override
    public void setDrawAttackRotation(InventoryItem item, ItemAttackDrawOptions drawOptions, float attackDirX, float attackDirY, float attackProgress) {
        if (this.getAnimInverted(item)) {
            drawOptions.swingRotationInv(attackProgress);
        } else {
            drawOptions.swingRotation(attackProgress);
        }
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        int velocity = this.getProjectileVelocity(item, attackerMob);
        float velocityPercent = 1.0f / ((float)velocity / 500.0f);
        int flaskAirTime = (int)(500.0f * velocityPercent);
        float predictedX = target.x + Entity.getPositionAfterMillis(target.dx, flaskAirTime);
        float predictedY = target.y + Entity.getPositionAfterMillis(target.dy, flaskAirTime);
        return this.applyInaccuracy(attackerMob, item, new Point((int)predictedX, (int)predictedY));
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        int attackRange = this.getAttackRange(item);
        float distanceFromTarget = GameMath.getExactDistance(attackerMob.x, attackerMob.y, x, y);
        int distance = (int)GameMath.limit(distanceFromTarget, 0.0f, (float)attackRange);
        Point2D.Float normalizedVector = GameMath.normalize((float)x - attackerMob.x, (float)y - attackerMob.y);
        CollisionFilter collisionFilter = new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(level, (double)attackerMob.x, (double)attackerMob.y, (double)normalizedVector.x, (double)normalizedVector.y, (double)distance, 0, collisionFilter);
        if (!hits.isEmpty()) {
            Ray first = (Ray)hits.getLast();
            x = (int)first.x2;
            y = (int)first.y2;
            distance = (int)GameMath.getExactDistance(attackerMob.x, attackerMob.y, x, y);
        }
        float baseDistance = this.attackRange.getValue(0.0f).intValue();
        float distancePercent = (float)distance / baseDistance;
        int velocity = this.getProjectileVelocity(item, attackerMob);
        float baseVelocity = this.velocity.getValue(0.0f).intValue();
        float velocityPercent = 1.0f / ((float)velocity / baseVelocity);
        int flaskAirTime = (int)(baseVelocity * velocityPercent * distancePercent);
        float speed = Entity.getTravelSpeedForMillis(flaskAirTime, distance);
        Projectile projectile = this.getProjectile(level, attackerMob, attackerMob.x, attackerMob.y, x, y, speed, distance, this.getAttackDamage(item), this.getKnockback(item, attackerMob), new GameRandom((long)seed * 2548L));
        projectile.setModifier(new ResilienceOnHitProjectileModifier(this.getResilienceGain(item)));
        projectile.resetUniqueID(new GameRandom(seed));
        attackerMob.addAndSendAttackerProjectile(projectile, 20);
        this.consumeMana(attackerMob, item);
        return item;
    }
}

