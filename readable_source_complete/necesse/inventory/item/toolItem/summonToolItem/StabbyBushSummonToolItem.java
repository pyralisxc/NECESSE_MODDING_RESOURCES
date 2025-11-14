/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.summonToolItem;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.Ray;
import necesse.engine.util.RayLinkedList;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.ToolItemSummonedMob;
import necesse.entity.projectile.StabbyBushSpawnProjectile;
import necesse.gfx.GameResources;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.upgradeUtils.IntUpgradeValue;
import necesse.inventory.lootTable.presets.SummonWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.LevelObjectHit;

public class StabbyBushSummonToolItem
extends SummonToolItem {
    public IntUpgradeValue maxSummons = new IntUpgradeValue(3, 0.0f);

    public StabbyBushSummonToolItem() {
        super("stabbybushfollowingmob", FollowPosition.WALK_CLOSE, 1.0f, 400, SummonWeaponsLootTable.summonWeapons);
        this.summonType = "stabbybushfollowingmob";
        this.rarity = Item.Rarity.COMMON;
        this.attackAnimTime.setBaseValue(300);
        this.attackDamage.setBaseValue(18.0f).setUpgradedValue(1.0f, 49.000015f);
        this.knockback.setBaseValue(0);
        this.attackXOffset = 18;
        this.attackYOffset = 18;
        this.drawMaxSummons = false;
        this.maxSummons.setBaseValue(3).setUpgradedValue(1.0f, 5);
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "stabbybushtip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "secondarysummon"));
        tooltips.add(Localization.translate("itemtooltip", "stabbybushsummonmaxcap", "amount", (Object)this.getMaxSummons(item, perspective)));
        return tooltips;
    }

    @Override
    public int getMaxSummons(InventoryItem item, ItemAttackerMob attackerMob) {
        return this.maxSummons.getValue(this.getUpgradeTier(item));
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        if (level.isClient()) {
            SoundManager.playSound(GameResources.magicbolt4, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(1.6f, 1.8f)));
        }
    }

    @Override
    public Point getItemAttackerAttackPosition(Level level, ItemAttackerMob attackerMob, Mob target, int seed, InventoryItem item) {
        float distance = Math.min(attackerMob.getDistance(target), 600.0f);
        float castSpeed = distance / 3.0f;
        return this.applyInaccuracy(attackerMob, item, this.getPredictedItemAttackerAttackPosition(attackerMob, target, castSpeed, 0.0f));
    }

    @Override
    public String canAttack(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        if (!attackerMob.isPlayer && attackerMob.isServer() && (attackerMob.serverFollowersManager.getFollowerCount(this.summonType) >= (float)this.getMaxSummons(item, attackerMob) || this.getSummonSpaceTaken(item, attackerMob) > (float)this.getMaxSummons(item, attackerMob)) && attackerMob.isOnGenericCooldown("stabbyBushSummon")) {
            return "";
        }
        return super.superCanAttack(level, x, y, attackerMob, item);
    }

    @Override
    public InventoryItem onAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int animAttack, int seed, GNDItemMap mapContent) {
        return super.onAttack(level, x, y, attackerMob, attackHeight, item, slot, animAttack, seed, mapContent);
    }

    @Override
    public void summonServerMob(ItemAttackerMob attackerMob, ToolItemSummonedMob mob, int x, int y, int attackHeight, InventoryItem item) {
        int distance;
        attackerMob.startGenericCooldown("stabbyBushSummon", 5000L);
        int xOffset = -15;
        if (attackerMob.getDir() == 3) {
            xOffset = 15;
        }
        Point2D.Float startPoints = new Point2D.Float(attackerMob.x + (float)xOffset, attackerMob.y + 10.0f);
        Point2D.Float targetPoints = new Point2D.Float(x, y);
        int reducedDistance = 0;
        Point2D.Float normalizedVector = GameMath.normalize(targetPoints.x - startPoints.x, targetPoints.y - startPoints.y);
        RayLinkedList<LevelObjectHit> hits = GameUtils.castRay(attackerMob.getLevel(), (double)startPoints.x, (double)startPoints.y, (double)normalizedVector.x, (double)normalizedVector.y, targetPoints.distance(startPoints.x, startPoints.y), 0, new CollisionFilter().projectileCollision().addFilter(tp -> tp.object().object.isWall || tp.object().object.isRock));
        if (!hits.isEmpty()) {
            Ray first = (Ray)hits.getLast();
            targetPoints.x = (float)first.x2;
            if (first.targetHit != null) {
                reducedDistance = 20;
            }
            targetPoints.y = (float)first.y2;
        }
        if ((distance = (int)targetPoints.distance(startPoints.x, startPoints.y) - reducedDistance) >= 600) {
            distance = 600;
        }
        float castSpeed = (float)distance / 3.0f;
        StabbyBushSpawnProjectile projectile = new StabbyBushSpawnProjectile(attackerMob.getLevel(), attackerMob, startPoints.x, startPoints.y, targetPoints.x, targetPoints.y, castSpeed, distance, new GameDamage(0.0f), 0, this, item);
        attackerMob.getLevel().entityManager.projectiles.add(projectile);
    }

    @Override
    public GameSprite getWorldItemSprite(InventoryItem item, PlayerMob perspective) {
        return this.getItemSprite(item, perspective);
    }
}

