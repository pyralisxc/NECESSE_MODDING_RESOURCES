/*
 * Decompiled with CFR 0.152.
 */
package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.sound.SoundEffect;
import necesse.engine.sound.SoundManager;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.attackHandler.GreatswordAttackHandler;
import necesse.entity.mobs.attackHandler.GreatswordChargeLevel;
import necesse.entity.mobs.itemAttacker.ItemAttackSlot;
import necesse.entity.mobs.itemAttacker.ItemAttackerMob;
import necesse.entity.projectile.NecroticGreatswordWaveProjectile;
import necesse.entity.projectile.Projectile;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

public class NecroticGreatswordAttackHandler
extends GreatswordAttackHandler {
    public NecroticGreatswordAttackHandler(ItemAttackerMob attackerMob, ItemAttackSlot slot, InventoryItem item, GreatswordToolItem toolItem, int seed, int startX, int startY, GreatswordChargeLevel ... chargeLevels) {
        super(attackerMob, slot, item, toolItem, seed, startX, startY, chargeLevels);
    }

    @Override
    public void onEndAttack(boolean bySelf) {
        super.onEndAttack(bySelf);
        if (this.currentChargeLevel == 2) {
            Point2D.Float dir = GameMath.getAngleDir(this.currentAngle);
            this.launchWaveProjectile(dir);
        }
    }

    public void launchWaveProjectile(Point2D.Float dir) {
        GameRandom random = new GameRandom(this.seed).nextSeeded(24);
        GameDamage damage = this.toolItem.getAttackDamage(this.item);
        int range = 544;
        float speed = 100.0f;
        Projectile projectile = this.getWaveProjectile(damage, range, speed, dir);
        projectile.resetUniqueID(random);
        this.attackerMob.addAndSendAttackerProjectile(projectile, 10);
        if (this.attackerMob.getLevel().isClient()) {
            SoundManager.playSound(GameResources.necroticGreatswordWaveProjectile, (SoundEffect)SoundEffect.effect(this.attackerMob).volume(0.3f).pitch(GameRandom.globalRandom.getFloatBetween(0.95f, 1.05f)));
        }
    }

    protected Projectile getWaveProjectile(GameDamage damage, int range, float speed, Point2D.Float dir) {
        return new NecroticGreatswordWaveProjectile(this.attackerMob.getLevel(), this.attackerMob, this.attackerMob.getX(), this.attackerMob.getY(), (int)(this.attackerMob.x + dir.x * 100.0f), (int)(this.attackerMob.y + dir.y * 100.0f), damage, speed, range);
    }
}

