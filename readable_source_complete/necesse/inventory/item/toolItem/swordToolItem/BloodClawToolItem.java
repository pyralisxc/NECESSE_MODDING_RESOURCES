/*
 * Decompiled with CFR 0.152.
 */
package necesse.inventory.item.toolItem.swordToolItem;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.Point2D;
import java.util.LinkedList;
import necesse.engine.localization.Localization;
import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.sound.SoundSettings;
import necesse.engine.util.GameBlackboard;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LineHitbox;
import necesse.entity.levelEvent.SwordCleanSliceAttackEvent;
import necesse.entity.levelEvent.mobAbilityLevelEvent.ToolItemMobAbilityEvent;
import necesse.entity.mobs.AttackAnimMob;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.BloodClawProjectile;
import necesse.entity.trails.Trail;
import necesse.entity.trails.TrailVector;
import necesse.gfx.GameResources;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.gameTexture.GameSprite;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemControllerInteract;
import necesse.inventory.item.ItemInteractAction;
import necesse.inventory.item.toolItem.swordToolItem.SwordToolItem;
import necesse.inventory.lootTable.presets.IncursionCloseRangeWeaponsLootTable;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.incursion.IncursionData;

public class BloodClawToolItem
extends SwordToolItem
implements ItemInteractAction {
    public BloodClawToolItem() {
        super(1900, IncursionCloseRangeWeaponsLootTable.incursionCloseRangeWeapons);
        this.rarity = Item.Rarity.EPIC;
        this.attackAnimTime.setBaseValue(180);
        this.attackDamage.setBaseValue(45.0f).setUpgradedValue(1.0f, 58.33335f);
        this.attackRange.setBaseValue(100);
        this.knockback.setBaseValue(40);
        this.attackXOffset = 4;
        this.attackYOffset = 4;
        this.resilienceGain.setBaseValue(1.0f);
        this.canBeUsedForRaids = true;
        this.minRaidTier = 1;
        this.maxRaidTier = IncursionData.ITEM_TIER_UPGRADE_CAP;
        this.raidTicketsModifier = 0.5f;
        this.useForRaidsOnlyIfObtained = true;
    }

    @Override
    public ListGameTooltips getPreEnchantmentTooltips(InventoryItem item, PlayerMob perspective, GameBlackboard blackboard) {
        ListGameTooltips tooltips = super.getPreEnchantmentTooltips(item, perspective, blackboard);
        tooltips.add(Localization.translate("itemtooltip", "bloodclawtip"), 400);
        tooltips.add(Localization.translate("itemtooltip", "bloodclawsecondarytip"), 400);
        return tooltips;
    }

    public void showBloodClawAttack(Level level, AttackAnimMob mob, int seed, final InventoryItem item) {
        GameRandom random = new GameRandom(seed);
        int rndStartX = random.getIntBetween(30, 45);
        int rndStartY = random.getIntBetween(30, 45);
        boolean rndXNegative = random.getEveryXthChance(2);
        boolean rndYNegative = random.getEveryXthChance(2);
        if (rndXNegative) {
            rndStartX = -rndStartX;
        }
        if (rndYNegative) {
            rndStartY = -rndStartY;
        }
        final int finalRndY = rndStartY;
        final int finalRndX = rndStartX;
        final Point2D.Float sliceDirection = GameMath.normalize(-rndStartX, -rndStartY);
        final double sliceDistance = Math.sqrt(rndStartX * rndStartX + rndStartY * rndStartY) * 2.0;
        level.entityManager.events.addHidden(new SwordCleanSliceAttackEvent(mob, seed, 12, this){
            Trail trail;
            final float maxSliceThickness = 20.0f;
            float thickness;
            final float mobStartX;
            final float mobStartY;
            {
                super(attackMob, attackSeed, totalTicksPerAttack, sword);
                this.trail = null;
                this.maxSliceThickness = 20.0f;
                this.mobStartX = this.attackMob.x;
                this.mobStartY = this.attackMob.y;
            }

            @Override
            public void tick(float angle, float currentAttackProgress) {
                this.thickness = currentAttackProgress < 0.33f ? 60.0f * currentAttackProgress : (currentAttackProgress > 0.66f ? 60.0f * (1.0f - currentAttackProgress) : 20.0f);
                int attackRange = BloodClawToolItem.this.getAttackRange(item);
                Point2D.Float angleDir = GameMath.getAngleDir(angle);
                float newX = this.mobStartX + angleDir.x * ((float)attackRange * 0.7f) + (float)this.attackMob.getCurrentAttackDrawXOffset();
                newX = (float)((double)newX + ((double)finalRndX + (double)(sliceDirection.x * currentAttackProgress) * sliceDistance));
                float newY = this.mobStartY + angleDir.y * ((float)attackRange * 0.7f) + (float)this.attackMob.getCurrentAttackDrawYOffset();
                newY = (float)((double)newY + ((double)finalRndY + (double)(sliceDirection.y * currentAttackProgress) * sliceDistance));
                if (this.trail == null) {
                    this.trail = new Trail(new TrailVector(newX, newY, sliceDirection.x, sliceDirection.y, this.thickness, 0.0f), this.level, new Color(164, 0, 0), 500);
                    this.trail.removeOnFadeOut = false;
                    this.trail.sprite = new GameSprite(GameResources.chains, 7, 0, 32);
                    this.level.entityManager.addTrail(this.trail);
                } else {
                    this.trail.addPoint(new TrailVector(newX, newY, sliceDirection.x, sliceDirection.y, this.thickness, 0.0f));
                }
            }

            @Override
            public void onDispose() {
                super.onDispose();
                if (this.trail != null) {
                    this.trail.removeOnFadeOut = true;
                }
            }
        });
    }

    @Override
    public void showAttack(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, int animAttack, int seed, GNDItemMap mapContent) {
        super.showAttack(level, x, y, attackerMob, attackHeight, item, animAttack, seed, mapContent);
        if (level.isClient()) {
            this.showBloodClawAttack(level, attackerMob, seed, item);
        }
    }

    @Override
    public void hitMob(InventoryItem item, ToolItemMobAbilityEvent event, Level level, Mob target, Mob attacker) {
        super.hitMob(item, event, level, target, attacker);
        if (!attacker.isServer()) {
            return;
        }
        ActiveBuff ab = new ActiveBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF, attacker, 10.0f, (Attacker)attacker);
        attacker.addBuff(ab, true);
    }

    @Override
    public boolean canItemAttackerHitTarget(ItemAttackerMob attackerMob, float fromX, float fromY, Mob target, InventoryItem item) {
        int attackRange;
        float distance = attackerMob.getDistance(target);
        if (distance < (float)(attackRange = this.getAttackRange(item))) {
            return super.canItemAttackerHitTarget(attackerMob, fromX, fromY, target, item);
        }
        if (distance < (float)(attackRange * 5)) {
            return !attackerMob.getLevel().collides((Shape)new LineHitbox(fromX, fromY, target.x, target.y, 45.0f), attackerMob.modifyChasingCollisionFilter(new CollisionFilter().projectileCollision(), target));
        }
        return false;
    }

    @Override
    public boolean canLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, InventoryItem item) {
        return attackerMob.buffManager.hasBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF);
    }

    @Override
    public InventoryItem onLevelInteract(Level level, int x, int y, ItemAttackerMob attackerMob, int attackHeight, InventoryItem item, ItemAttackSlot slot, int seed, GNDItemMap mapContent) {
        if (attackerMob.buffManager.hasBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF)) {
            int projectileCount = attackerMob.buffManager.getStacks(BuffRegistry.BLOOD_CLAW_STACKS_BUFF);
            this.consumeLife(projectileCount, attackerMob, item);
            for (int i = 0; i < projectileCount; ++i) {
                float rangeMod = 2.5f;
                float velocity = 300.0f;
                float finalVelocity = Math.round(this.getEnchantment(item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
                GameRandom random = new GameRandom(seed);
                GameDamage specialAttackDmg = this.getAttackDamage(item).modFinalMultiplier(0.75f);
                BloodClawProjectile projectile = new BloodClawProjectile(level, attackerMob.x, attackerMob.y, x, y, finalVelocity, (int)((float)this.getAttackRange(item) * rangeMod), specialAttackDmg, attackerMob);
                projectile.getUniqueID(random);
                attackerMob.addAndSendAttackerProjectile(projectile, 20, random.getIntBetween(-30, 30));
            }
            attackerMob.buffManager.removeBuff(BuffRegistry.BLOOD_CLAW_STACKS_BUFF, level.isServer());
            if (level.isClient()) {
                SoundManager.playSound(GameResources.bloodClawShoot, (SoundEffect)SoundEffect.effect(attackerMob).volume(0.8f));
            }
        }
        return item;
    }

    @Override
    public ItemControllerInteract getControllerInteract(Level level, PlayerMob player, InventoryItem item, boolean beforeObjectInteract, int interactDir, LinkedList<Rectangle> mobInteractBoxes, LinkedList<Rectangle> tileInteractBoxes) {
        Point2D.Float controllerAimDir = player.getControllerAimDir();
        Point levelPos = this.getControllerAttackLevelPos(level, controllerAimDir.x, controllerAimDir.y, player, item);
        return new ItemControllerInteract(levelPos.x, levelPos.y){

            @Override
            public DrawOptions getDrawOptions(GameCamera camera) {
                return null;
            }

            @Override
            public void onCurrentlyFocused(GameCamera camera) {
            }
        };
    }

    @Override
    protected SoundSettings getAttackSound() {
        return new SoundSettings(GameResources.bloowClawSwing).volume(0.2f);
    }
}

