/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.particle.Particle;
import necesse.entity.projectile.BarkBladeLeafProjectile;
import necesse.inventory.InventoryItem;
import necesse.inventory.enchants.ToolItemModifiers;
import necesse.inventory.item.toolItem.swordToolItem.BarkBladeSwordToolItem;

public class BarkBladeGreatswordAttackHandler
extends GreatswordAttackHandler {
    private final BarkBladeSwordToolItem toolItem;

    public BarkBladeGreatswordAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, BarkBladeSwordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem.greatswordItem, seed, startX, startY, toolItem.getChargeLevels());
        this.toolItem = toolItem;
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
        switch (this.currentChargeLevel) {
            case 2: {
                ActiveBuff ab = new ActiveBuff(BuffRegistry.BARKBLADE_ENHANCED, (Mob)this.attackerMob, 5000, null);
                this.attackerMob.buffManager.addBuff(ab, false);
            }
            case 1: {
                float rangeMod = 7.0f;
                float velocity = 140.0f;
                for (int i = -2; i < 3; ++i) {
                    float finalVelocity = Math.round(this.toolItem.getEnchantment(this.item).applyModifierLimited(ToolItemModifiers.VELOCITY, (Float)ToolItemModifiers.VELOCITY.defaultBuffManagerValue).floatValue() * velocity * this.attackerMob.buffManager.getModifier(BuffModifiers.PROJECTILE_VELOCITY).floatValue());
                    BarkBladeLeafProjectile projectile = new BarkBladeLeafProjectile(this.attackerMob.getLevel(), this.attackerMob.x, this.attackerMob.y, this.attackerMob.x + dir.x * 100.0f, this.attackerMob.y + dir.y * 100.0f, finalVelocity, (int)((float)this.toolItem.getAttackRange(this.item) * rangeMod), new GameDamage(this.toolItem.getAttackDamage((InventoryItem)this.item).damage / 3.0f), this.attackerMob);
                    GameRandom random = new GameRandom(this.seed);
                    projectile.resetUniqueID(random);
                    this.attackerMob.addAndSendAttackerProjectile(projectile, 20, 10 * i);
                }
                break;
            }
        }
    }

    @Override
    public void drawWeaponParticles(InventoryItem showItem, Color color) {
        float chargePercent = showItem.getGndData().getFloat("chargePercent");
        showItem.getGndData().setBoolean("charging", true);
        float angle = this.toolItem.getSwingRotation(showItem, this.attackerMob.getDir(), chargePercent);
        int attackDir = this.attackerMob.getDir();
        int offsetX = 0;
        int offsetY = 0;
        if (attackDir == 0) {
            angle = -angle - 90.0f;
            offsetY = -8;
        } else if (attackDir == 1) {
            angle = -angle + 180.0f + 45.0f;
            offsetX = 8;
        } else if (attackDir == 2) {
            angle = -angle + 90.0f;
            offsetY = 12;
        } else {
            angle = angle + 90.0f + 45.0f;
            offsetX = -8;
        }
        float dx = GameMath.sin(angle);
        float dy = GameMath.cos(angle);
        int range = GameRandom.globalRandom.getIntBetween(0, this.toolItem.greatswordRange);
        this.attackerMob.getLevel().entityManager.addParticle(this.attackerMob.x + (float)offsetX + dx * (float)range + GameRandom.globalRandom.floatGaussian() * 3.0f, this.attackerMob.y + 4.0f + GameRandom.globalRandom.floatGaussian() * 4.0f, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.attackerMob.dx, this.attackerMob.dy).color(color).height(20.0f - dy * (float)range - (float)offsetY);
    }
}

