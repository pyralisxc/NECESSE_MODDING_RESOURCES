/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.CheckSlotType;
import necesse.entity.mobs.itemAttacker.FollowPosition;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.mobs.summon.summonFollowingMob.attackingFollowingMob.SlimeGreatswordFollowingMob;
import necesse.entity.projectile.Projectile;
import necesse.entity.projectile.SlimeGreatswordProjectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.summonToolItem.SummonToolItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

public class SlimeGreatswordAttackHandler
extends GreatswordAttackHandler {
    public SlimeGreatswordAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        switch (this.currentChargeLevel) {
            case 2: {
                this.summonSlimeMob(dir);
            }
            case 1: {
                this.launchSlimeProjectile(dir);
            }
        }
    }

    private void summonSlimeMob(Point2D.Float dir) {
        if (this.attackerMob.isServer()) {
            SlimeGreatswordFollowingMob mob = new SlimeGreatswordFollowingMob();
            this.attackerMob.serverFollowersManager.addFollower("slimegreatswordslime", (Mob)mob, FollowPosition.WALK_CLOSE, "summonedmob", 1.0f, 10, null, false);
            Point2D.Float spawnPoint = SummonToolItem.findSpawnLocation(mob, this.attackerMob.getLevel(), this.attackerMob.x, this.attackerMob.y);
            mob.updateDamage(this.toolItem.getAttackDamage(this.item));
            mob.setEnchantment(this.toolItem.getEnchantment(this.item));
            if (!this.attackerMob.isPlayer) {
                mob.setRemoveWhenNotInInventory(ItemRegistry.getItem("slimegreatsword"), CheckSlotType.WEAPON);
            }
            mob.dx = dir.x * 300.0f;
            mob.dy = dir.y * 300.0f;
            this.attackerMob.getLevel().entityManager.addMob(mob, spawnPoint.x, spawnPoint.y);
        }
        if (this.attackerMob.getLevel().isClient()) {
            SoundManager.playSound(GameResources.slimeSpawn, (SoundEffect)SoundEffect.effect(this.attackerMob).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
        }
    }

    private void launchSlimeProjectile(Point2D.Float dir) {
        float rangeMod = 7.0f;
        float velocity = 140.0f;
        float finalVelocity = Math.round(this.toolItem.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * this.attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
        SlimeGreatswordProjectile projectile = new SlimeGreatswordProjectile(this.attackerMob.getLevel(), this.attackerMob.x, this.attackerMob.y, this.attackerMob.x + dir.x * 100.0f, this.attackerMob.y + dir.y * 100.0f, finalVelocity, (int)((float)this.toolItem.getAttackRange(this.item) * rangeMod), this.toolItem.getAttackDamage(this.item), this.attackerMob);
        GameRandom random = new GameRandom(this.seed);
        projectile.resetUniqueID(random);
        this.attackerMob.addAndSendAttackerProjectile((Projectile)projectile, 20);
        if (this.attackerMob.getLevel().isClient()) {
            SoundManager.playSound(GameResources.slimeGreatswordWaveProjectile, (SoundEffect)SoundEffect.effect(this.attackerMob).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
        }
    }
}

